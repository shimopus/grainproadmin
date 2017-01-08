package pro.grain.admin.service;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.domain.enumeration.QualityClass;
import pro.grain.admin.service.dto.BidPriceDTO;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.service.error.MarketGenerationException;
import pro.grain.admin.web.utils.SoyTemplatesUtils;

import javax.inject.Inject;
import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            File marketTableFile = new ClassPathResource("templates/tables/market-table.soy").getFile();
            File marketTableDownloadFile = new ClassPathResource("templates/tables/market-table-download.soy").getFile();

            // Bundle the Soy files for your project into a SoyFileSet.
            SoyFileSet sfs = SoyFileSet.builder()
                .add(marketTableFile)
                .add(marketTableDownloadFile)
                .build();

            // Compile the template into a SoyTofu object.
            // SoyTofu's newRenderer method returns an object that can render any template in file set.
            tofu = sfs.compileToTofu();
        } catch (IOException e) {
            log.error("Could not open template file for market table", e);
            throw new MarketGenerationException("Could not open template file for market table", e);
        }
    }

    public String getMarketTableHTML(String stationCode) throws MarketGenerationException {
        log.debug("Generate market HTML table for station code {}", stationCode);

        SoyMapData templateData = generateCommonParameters(stationCode, "");

        return tofu.newRenderer("tables.market_table")
            .setData(templateData)
            .render();
    }

    public String getMarketTableHTMLForDownload(String stationCode) throws MarketGenerationException {
        log.debug("Generate market HTML table for station code which should be downloaded {}", stationCode);

        SoyMapData templateData = generateCommonParameters(stationCode, "http://grain.pro");
        return tofu.newRenderer("tables.market_table_download")
            .setData(templateData)
            .render();
    }

    private SoyMapData generateCommonParameters(String stationCode, String baseUrl) throws MarketGenerationException {
        Map<QualityClass, List<BidPriceDTO>> bids = getBids(stationCode);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

        return new SoyMapData(
            "currentDate", dateFormat.format(new Date()),
            "station", SoyTemplatesUtils.objectToSoyData(stationService.findOne(stationCode)),
            "baseUrl", baseUrl,
            "bids", SoyTemplatesUtils.objectToSoyData(bids)
        );
    }

    private Map<QualityClass, List<BidPriceDTO>> getBids(String stationCode) throws MarketGenerationException {
        String newCode;
        Map<QualityClass, List<BidPriceDTO>> bids;

        if (stationCode != null) {
            try {
                newCode = calculateDestinationStation(stationCode);
                bids = bidService.getAllCurrentBidsForStation(newCode);
            } catch (KeySelectorException e) {
                log.error("Could not calculate destination station", e);
                throw new MarketGenerationException("Could not calculate destination station", e);
            }
        } else {
            bids = bidService.getAllCurrentBids();
        }
        return bids;
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

}
