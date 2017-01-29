package pro.grain.admin.service;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.domain.enumeration.NDS;
import pro.grain.admin.domain.enumeration.QualityClass;
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

    public String getMarketTableHTML(String stationCode, String templateName, String baseUrl) {
        log.debug("Generate market HTML table for station code which should be downloaded {}", stationCode);

        SoyMapData templateData;
        try {
            templateData = generateCommonParameters(stationCode, baseUrl);
        } catch (MarketGenerationException e) {
            return tofu.newRenderer("tables.error")
                .setData(new SoyMapData("errors", SoyTemplatesUtils.objectToSoyData(e.getErrors())))
                .render();
        }
        return tofu.newRenderer("tables." + templateName)
            .setData(templateData)
            .render();
    }

    private SoyMapData generateCommonParameters(String stationCode, String baseUrl) throws MarketGenerationException {
        Collection<ArrayList<BidPriceDTO>> bids = getBids(stationCode);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

        return new SoyMapData(
            "currentDate", dateFormat.format(new Date()),
            "station", SoyTemplatesUtils.objectToSoyData(stationService.findOne(stationCode)),
            "baseUrl", baseUrl,
            "adminBaseUrl", "https://grainpro.herokuapp.com/",
            "bids", SoyTemplatesUtils.objectToSoyData(bids)
        );
    }

    private Collection<ArrayList<BidPriceDTO>> getBids(String stationCode) throws MarketGenerationException {
        String newCode;
        List<BidPriceDTO> bids;
        List<String> errors = new ArrayList<>();

        if (stationCode != null) {
            try {
                newCode = calculateDestinationStation(stationCode);
                bids = bidService.getAllCurrentBidsForStation(newCode);
                List<BidPriceDTO> fullBids = bidService.getAllCurrentBids();

                //Check for errors
                List<BidPriceDTO> errorForBids = new ArrayList<>(bids.size());

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

                if (errorForBids.size() != 0) {
                    log.error("Some bids could not be calculated for station " + newCode);
                    for (BidPriceDTO bid : errorForBids) {
                        String stationFromCode = bid.getElevator().getStationCode();
                        String baseStationFromCode;
                        try {
                            baseStationFromCode = calculateDestinationStation(stationFromCode);
                        } catch (KeySelectorException e) {
                            errors.add("Не возможно вычислить базовую станцию для станции " + stationFromCode +
                                " (" + bid.getElevator().getStationName() + ")");
                            continue;
                        }

                        errors.add("Нет цены для перевозки из " + baseStationFromCode + " в " + newCode);
                    }

                    throw new MarketGenerationException("Some bids could not be calculated for station " + newCode,
                        errors);
                }

                return enrichAndSortMarket(bids, stationCode, newCode);

            } catch (KeySelectorException e) {
                log.error("Could not calculate destination station", e);
                errors.add("Не возможно вычислить базовую станцию для станции " + stationCode);
                throw new MarketGenerationException("Could not calculate destination station",
                    errors
                    , e);
            }
        } else {
            return enrichAndSortMarket(bidService.getAllCurrentBids(), null, null);
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
            .map(bid -> {
                if (!isStationFromEqualsStationTo(bid, stationCode, baseStationCode)) {
                    bid.setFcaPrice(getFCAPrice(bid));
                    if (stationCode != null) {
                        bid.setCptPrice(getCPTPrice(bid));
                    }
                }
                return bid;
            })
            .collect(Collectors.groupingBy(BidPriceDTO::getQualityClass, TreeMap::new,
                Collectors.collectingAndThen(
                    Collectors.toCollection(ArrayList::new),
                    l -> {
                        l.sort(Comparator.comparingLong(bid -> getPriceToCompare(bid, stationCode, baseStationCode)));
                        return l;

                    }
                ))).values();
    }

    private Long getPriceToCompare(BidPriceDTO bid, String stationCode, String baseStationCode) {
        if (stationCode == null) {
            return getFCAPrice(bid);
        } else {
            //Если станция отгрузки равна станции доставки
            if (isStationFromEqualsStationTo(bid, stationCode, baseStationCode)) {
                return bid.getPrice();
            } else {
                return getCPTPrice(bid);
            }
        }
    }

    private Long getFCAPrice(BidPriceDTO bid) {
        Long selfPrice = bid.getPrice();
        Long loadPrice = 0L;

        if (bid.getElevator().getServicePrices() != null && bid.getElevator().getServicePrices().size() > 0) {
            loadPrice = bid.getElevator().getServicePrices().iterator().next().getPrice();
        }

        return selfPrice + loadPrice;
    }

    private Long getCPTPrice(BidPriceDTO bid) {
        Long transpPrice = 0L;

        if (bid.getNds().equals(NDS.EXCLUDED) && bid.getTransportationPricePrice() != null) {
            transpPrice = bid.getTransportationPricePrice();
        } else if (bid.getNds().equals(NDS.INCLUDED) && bid.getTransportationPricePriceNds() != null) {
            transpPrice = bid.getTransportationPricePriceNds();
        }

        return getFCAPrice(bid) + transpPrice;
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
