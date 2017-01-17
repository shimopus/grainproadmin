package pro.grain.admin.web.pages;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pro.grain.admin.service.MarketService;
import pro.grain.admin.service.error.MarketGenerationException;
import pro.grain.admin.web.rest.util.HeaderUtil;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/pages")
public class MarketTableController {
    private final Logger log = LoggerFactory.getLogger(MarketTableController.class);

    private final MarketService marketService;

    @Inject
    public MarketTableController(MarketService marketService) throws IOException {
        this.marketService = marketService;
    }

    @RequestMapping(value = "/market-table/admin",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @Timed
    public ResponseEntity<String> getAllRegionsForAdmin(@RequestParam(value = "code", required = false) String code)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code on site {}", code);

        try {
            return ResponseEntity.ok()
                .body(marketService.getMarketTableHTML(code, "market_table_admin", ""));

        } catch (MarketGenerationException e) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(e.getCause().getMessage(), ""))
                .body(null);
        }
    }

    @RequestMapping(value = "/market-table/site",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @Timed
    public ResponseEntity<String> getAllRegionsForSite(@RequestParam(value = "code", required = false) String code)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code on site {}", code);

        try {
            return ResponseEntity.ok()
                .body(marketService.getMarketTableHTML(code, "market_table_site", ""));

        } catch (MarketGenerationException e) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(e.getCause().getMessage(), ""))
                .body(null);
        }
    }

    @RequestMapping(value = "/market-table/download",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public ResponseEntity<String> downloadAllRegions(@RequestParam(value = "code", required = false) String code)
        throws URISyntaxException/*, UnsupportedEncodingException*/ {
        log.debug("REST request to download list of bids for station code {}", code);
/*
        SimpleDateFormat fileDateFormat = new SimpleDateFormat("dd_MM_yy");

        String fileName = "Пшеница_" + fileDateFormat.format(new Date()) + ".html";
        fileName = MimeUtility.encodeWord(fileName, "utf-8", "Q");*/

        try {
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(marketService.getMarketTableHTML(code, "market_table_download", "http://grain.pro/"));

        } catch (MarketGenerationException e) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(e.getCause().getMessage(), ""))
                .body(null);
        }
    }
}
