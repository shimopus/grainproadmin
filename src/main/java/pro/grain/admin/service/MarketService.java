package pro.grain.admin.service;

import com.google.common.base.Stopwatch;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.domain.enumeration.BidType;
import pro.grain.admin.domain.enumeration.NDS;
import pro.grain.admin.service.dto.BidPriceDTO;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.service.error.MarketGenerationException;
import pro.grain.admin.web.utils.SoyTemplatesUtils;

import javax.inject.Inject;
import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketService {
    private final Logger log = LoggerFactory.getLogger(MarketService.class);
    private final BidService bidService;
    private final StationService stationService;

    private final SoyTofu tofu;

    @Inject
    public MarketService(BidService bidService, StationService stationService) throws MarketGenerationException {
        this.bidService = bidService;
        this.stationService = stationService;

        try {
            // Bundle the Soy files for your project into a SoyFileSet.
            SoyFileSet sfs = SoyFileSet.builder()
                .add(getFileFromResources("templates/tables/market-table.soy"))
                .add(getFileFromResources("templates/tables/market-table-download.soy"))
                .add(getFileFromResources("templates/tables/market-table-admin.soy"))
                .add(getFileFromResources("templates/tables/market-table-site.soy"))
                .build();

            // Compile the template into a SoyTofu object.
            // SoyTofu's newRenderer method returns an object that can render any template in file set.
            tofu = sfs.compileToTofu();
        } catch (IOException e) {
            log.error("Could not open template file for market table", e);
            throw new MarketGenerationException("Could not open template file for market table", e);
        }
    }

    public String getMarketTableHTML(String stationCode, BidType bidType, String templateName, String baseUrl) {
        log.debug("Generate market HTML table for station code which should be downloaded {}", stationCode);

        SoyMapData templateData;
        try {
            templateData = generateCommonParameters(stationCode, bidType, baseUrl);
        } catch (MarketGenerationException e) {
            return tofu.newRenderer("tables.error")
                .setData(new SoyMapData("errors", SoyTemplatesUtils.objectToSoyData(e.getErrors())))
                .render();
        }
        return tofu.newRenderer("tables." + templateName)
            .setData(templateData)
            .render();
    }

    private SoyMapData generateCommonParameters(String stationCode, BidType bidType, String baseUrl) throws MarketGenerationException {
        Collection<ArrayList<BidPriceDTO>> bids = getBids(stationCode, bidType);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

        return new SoyMapData(
            "currentDate", dateFormat.format(new Date()),
            "station", SoyTemplatesUtils.objectToSoyData(stationService.findOne(stationCode)),
            "baseUrl", baseUrl,
            "adminBaseUrl", "https://grainpro.herokuapp.com/",
            "bids", SoyTemplatesUtils.objectToSoyData(bids),
            "bidType", SoyTemplatesUtils.objectToSoyData(bidType)
        );
    }

    private Collection<ArrayList<BidPriceDTO>> getBids(String stationCode, BidType bidType) throws MarketGenerationException {
        String newCode;
        List<BidPriceDTO> bids;
        List<String> errors = new ArrayList<>();

        if (stationCode != null) {
            try {
                newCode = calculateDestinationStation(stationCode);
                log.warn("Price calc: station code {}", stationCode);
                Stopwatch timer = Stopwatch.createStarted();
                bids = bidService.getAllCurrentBidsForStation(newCode, bidType);
                timer.stop();
                log.warn("Price calc: get bids with price {}ms", timer.elapsed(TimeUnit.MILLISECONDS));

                timer = timer.reset();
                timer.start();
                List<BidPriceDTO> fullBids = bidService.getAllCurrentBids(bidType);
                timer.stop();
                log.warn("Price calc: get all bids {}ms", timer.elapsed(TimeUnit.MILLISECONDS));

                //Check for errors
                List<BidPriceDTO> errorForBids = new ArrayList<>(bids.size());


                timer = timer.reset();
                timer.start();
                for (BidPriceDTO fullBidPriceDTO : fullBids) {
                    boolean exists = false;

                    for (BidPriceDTO bidPriceDTO : bids) {
                        if (fullBidPriceDTO.getId().equals(bidPriceDTO.getId())) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        //Если станция отправления равна станции прибытия
                        if (isStationFromEqualsStationTo(fullBidPriceDTO, stationCode, newCode)) {
                            bids.add(fullBidPriceDTO);
                        } else {
                            errorForBids.add(fullBidPriceDTO);
                        }
                    }
//                    }
                }
                timer.stop();
                log.warn("Price calc: sort and check for errors {}ms", timer.elapsed(TimeUnit.MILLISECONDS));

                if (errorForBids.size() != 0) {
                    log.error("Some bids could not be calculated for station " + newCode);
                    for (BidPriceDTO bid : errorForBids) {
                        String stationFromCode = bid.getElevator().getStationCode();
                        String baseStationFromCode;
                        try {
                            baseStationFromCode = calculateDestinationStation(stationFromCode);
                        } catch (KeySelectorException e) {
                            errors.add("Невозможно вычислить базовую станцию для станции " + stationFromCode +
                                " (" + bid.getElevator().getStationName() + ")");
                            continue;
                        }

                        errors.add("Нет цены для перевозки из " + baseStationFromCode + " в " + newCode);
                    }

                    throw new MarketGenerationException("Some bids could not be calculated for station " + newCode,
                        errors);
                }

                timer = timer.reset();
                timer.start();
                Collection<ArrayList<BidPriceDTO>> res = enrichAndSortMarket(bids, stationCode, newCode);
                timer.stop();
                log.warn("Price calc: sort and enrich {}ms", timer.elapsed(TimeUnit.MILLISECONDS));

                return res;

            } catch (KeySelectorException e) {
                log.error("Could not calculate destination station", e);
                errors.add("Не возможно вычислить базовую станцию для станции " + stationCode);
                throw new MarketGenerationException("Could not calculate destination station",
                    errors
                    , e);
            }
        } else {
            return enrichAndSortMarket(bidService.getAllCurrentBids(bidType), null, null);
        }
    }

    private String calculateDestinationStation(String byStationCode) throws KeySelectorException {
        StationDTO station = stationService.findOne(byStationCode);
        if (station.getRegionId() == null || station.getDistrictId() == null) {
            throw new KeySelectorException(
                String.format("Station with code \"%s\" doesn't have region and/or district. Please specify it.",
                    byStationCode));
        }

        StationDTO newStation = stationService.findByLocation(station.getRegionId(), station.getDistrictId(), station.getLocalityId());

        if (newStation == null) {
            throw new KeySelectorException(
                String.format("Base Station for location \"%s\", \"%s\", \"%s\" was not found. Please specify it.",
                    station.getRegionName(),
                    station.getDistrictName(),
                    station.getLocalityName()));
        }

        return newStation.getCode();
    }

    private Collection<ArrayList<BidPriceDTO>> enrichAndSortMarket(List<BidPriceDTO> bids, String stationCode, String baseStationCode) {
        if (bids == null) return null;

        return bids.stream()
            .peek(bid -> {
                if (!isStationFromEqualsStationTo(bid, stationCode, baseStationCode) || bid.getBidType() == BidType.BUY) {
                    Long price = getFCAPrice(bid, stationCode);
                    if (price != Long.MIN_VALUE) {
                        bid.setFcaPrice(price);
                    }

                    price = getCPTPrice(bid, stationCode);
                    if (price != Long.MIN_VALUE) {
                        bid.setCptPrice(price);
                    }
                }
            })
            .collect(Collectors.groupingBy(BidPriceDTO::getQualityClass, TreeMap::new,
                Collectors.collectingAndThen(
                    Collectors.toCollection(ArrayList::new),
                    l -> {
                        if (l.get(0).getBidType() == BidType.BUY) {
                            l.sort(Comparator.comparingLong(bid -> getPriceToCompare((BidPriceDTO)bid, stationCode, baseStationCode)).reversed());
                        } else {
                            l.sort(Comparator.comparingLong(bid -> getPriceToCompare(bid, stationCode, baseStationCode)));
                        }
                        return l;
                    }
                ))).values();
    }

    private Long getPriceToCompare(BidPriceDTO bid, String stationCode, String baseStationCode) {
        if (bid.getBidType() == BidType.SELL) {
            if (stationCode == null) {
                return getFCAPrice(bid, null);
            } else {
                //Если станция отгрузки равна станции доставки
                if (isStationFromEqualsStationTo(bid, stationCode, baseStationCode)) {
                    return bid.getPrice();
                } else {
                    return getCPTPrice(bid, stationCode);
                }
            }
        } else if (bid.getBidType() == BidType.BUY) {
            if (stationCode == null) {
                return getCPTPrice(bid, stationCode);
            } else {
                return getFCAPrice(bid, stationCode);
            }
        }

        return 0L;
    }

    private Long getFCAPrice(BidPriceDTO bid, String stationCode) {
        log.debug("BID {}", bid.getBidType());
        if (bid.getBidType() == BidType.SELL) {
            Long selfPrice = bid.getPrice();
            log.debug("BID price {}", selfPrice);
            Long loadPrice = 0L;

            if (bid.getElevator().getServicePrices() != null && bid.getElevator().getServicePrices().size() > 0) {
                loadPrice = bid.getElevator().getServicePrices().iterator().next().getPrice();
            }

            log.debug("BID load price {}", loadPrice);

            return selfPrice + loadPrice;
        } else if (bid.getBidType() == BidType.BUY) {
            log.debug("BID {}", bid.getBidType());
            if (stationCode == null) return Long.MIN_VALUE;

            return getCPTPrice(bid, stationCode) - getTransportationPrice(bid);
        }

        return Long.MIN_VALUE;
    }

    private Long getCPTPrice(BidPriceDTO bid, String stationCode) {
        if (bid.getBidType() == BidType.SELL) {

            if (stationCode == null) return Long.MIN_VALUE;

            return getFCAPrice(bid, stationCode) + getTransportationPrice(bid);
        } else if (bid.getBidType() == BidType.BUY) {
            return bid.getPrice();
        }

        return Long.MIN_VALUE;
    }

    private Long getTransportationPrice(BidPriceDTO bid) {

        Long transpPrice = 0L;

        if (bid.getNds().equals(NDS.EXCLUDED) && bid.getTransportationPricePrice() != null) {
            transpPrice = bid.getTransportationPricePrice();
        } else if (bid.getNds().equals(NDS.INCLUDED) && bid.getTransportationPricePriceNds() != null) {
            transpPrice = bid.getTransportationPricePriceNds();
        }

        return transpPrice;
    }

    private boolean isStationFromEqualsStationTo(BidPriceDTO bid, String stationTo, String baseStationTo) {
        return bid.getElevator().getStationCode().equals(stationTo)
            || bid.getElevator().getStationCode().equals(baseStationTo);
    }

    private File getFileFromResources(String path) throws IOException {
        File tempFile = File.createTempFile("tmp", null);
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(new ClassPathResource(path).getInputStream(), out);
        return tempFile;
    }

}
