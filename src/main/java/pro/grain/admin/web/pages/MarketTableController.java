package pro.grain.admin.web.pages;

import com.codahale.metrics.annotation.Timed;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pro.grain.admin.domain.enumeration.QualityClass;
import pro.grain.admin.service.BidService;
import pro.grain.admin.service.StationService;
import pro.grain.admin.service.dto.BidPriceDTO;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.utils.SoyTemplatesUtils;

import javax.inject.Inject;
import javax.mail.internet.MimeUtility;
import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pages")
public class MarketTableController {
    private final Logger log = LoggerFactory.getLogger(MarketTableController.class);

    private final BidService bidService;
    private final StationService stationService;

    private final SoyTofu tofu;

    @Inject
    public MarketTableController(BidService bidService, StationService stationService) throws IOException {
        this.bidService = bidService;
        this.stationService = stationService;

        File marketTableFile = new ClassPathResource("templates/tables/market-table.soy").getFile();

        // Bundle the Soy files for your project into a SoyFileSet.
        SoyFileSet sfs = SoyFileSet.builder().add(marketTableFile).build();

        // Compile the template into a SoyTofu object.
        // SoyTofu's newRenderer method returns an object that can render any template in file set.
        tofu = sfs.compileToTofu();
    }

    @RequestMapping(value = "/market-table",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @Timed
    public ResponseEntity<String> getAllRegions(@RequestParam(value = "code", required = false) String code)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code {}", code);

        String newCode;
        Map<QualityClass, List<BidPriceDTO>> bids;

        if (code != null) {
            try {
                newCode = calculateDestinationStation(code);
                bids = bidService.getAllCurrentBidsForStation(newCode);
            } catch (KeySelectorException e) {
                log.error("Could not calculate destination station", e);
                return ResponseEntity.ok()
                    .headers(HeaderUtil.createAlert(e.getMessage(), ""))
                    .body(null);
            }
        } else {
            bids = bidService.getAllCurrentBids();
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

            SoyMapData templateData = new SoyMapData(
                "currentDate", dateFormat.format(new Date()),
                "station", SoyTemplatesUtils.objectToSoyData(stationService.findOne(code)),
                "bids", SoyTemplatesUtils.objectToSoyData(bids)
            );

            String table = tofu.newRenderer("tables.market_table")
                .setData(templateData)
                .render();

            SimpleDateFormat fileDateFormat = new SimpleDateFormat("dd_MM_yy");

            String fileName = "Пшеница_" + fileDateFormat.format(new Date()) + ".html";
            fileName = MimeUtility.encodeWord(fileName, "utf-8", "Q");

            return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(table);

        } catch (IOException e) {
            log.error("Could not open template file for market table", e);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(e.getMessage(), ""))
                .body(null);
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
}
