package pro.grain.admin.web.pages;

import com.codahale.metrics.annotation.Timed;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pro.grain.admin.config.GrainProAdminProperties;
import pro.grain.admin.domain.enumeration.BidType;
import pro.grain.admin.service.MarketService;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/pages")
public class MarketTableController {
    private final Logger log = LoggerFactory.getLogger(MarketTableController.class);

    private final MarketService marketService;

    private final GrainProAdminProperties grainProAdminProperties;

    @Inject
    public MarketTableController(MarketService marketService, GrainProAdminProperties grainProAdminProperties) throws IOException {
        this.marketService = marketService;
        this.grainProAdminProperties = grainProAdminProperties;
    }

    @RequestMapping(value = "/market-table/admin",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @Timed
    public ResponseEntity<String> getAllRegionsForAdmin(@RequestParam(value = "code", required = false) String code, @RequestParam("bidType") BidType bidType)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code on site {}", code);

        return ResponseEntity.ok()
            .body(marketService.getMarketTableHTML(code, bidType,"market_table_admin", ""));
    }

    @RequestMapping(value = "/market-table/site",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @Timed
    public ResponseEntity<String> getAllRegionsForSite(@RequestParam(value = "code", required = false) String code,
                                                       @RequestParam("bidType") BidType bidType,
                                                       @RequestParam(value = "v", required = false) String version)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code on site {}", code);

        return ResponseEntity.ok()
            .body(marketService.getMarketTableHTML(
                code,
                bidType,
                "2".equalsIgnoreCase(version) ? "market_table_site_v2" : "market_table_site",
                ""));
    }

    @RequestMapping(value = "/market-table/download",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public ResponseEntity<String> downloadAllRegions(@RequestParam(value = "code", required = false) String code, @RequestParam("bidType") BidType bidType)
        throws URISyntaxException {
        log.debug("REST request to download list of bids for station code {}", code);

        String baseUrl = grainProAdminProperties.getSite().getBaseUrl()
            + (bidType == BidType.SELL ?
            grainProAdminProperties.getSite().getSellPagePath() :
            grainProAdminProperties.getSite().getBuyPagePath());

        URIBuilder uri = new URIBuilder(baseUrl)
            .setParameter("from", "file");

        if (code != null && !"".equals(code)) {
            uri.setParameter("code", code);
        }

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(marketService.getMarketTableHTML(code, bidType,"market_table_download", uri.toString()));
    }

    @RequestMapping(value = "/market-table/email-inside",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public ResponseEntity<String> emailInsideAllRegions(
        @RequestParam(value = "rowsLimit", required = false) Integer rowsLimit,
        @RequestParam(value = "code", required = false) String code,
        @RequestParam("bidType") BidType bidType) throws URISyntaxException {
        log.debug("REST request to email inside list of bids for station code {}", code);

        String baseUrl = grainProAdminProperties.getSite().getBaseUrl()
            + (bidType == BidType.SELL ?
            grainProAdminProperties.getSite().getSellPagePath() :
            grainProAdminProperties.getSite().getBuyPagePath());

        URIBuilder uri = new URIBuilder(baseUrl)
            .setParameter("from", "email");

        if (code != null && !"".equals(code)) {
            uri.setParameter("code", code);
        }

        int rowsLimitI = rowsLimit == null ? -1 : rowsLimit;
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(marketService.getMarketTableHTML(code, bidType,"market_table_email_inside",
                uri.toString(), rowsLimitI));
    }
}
