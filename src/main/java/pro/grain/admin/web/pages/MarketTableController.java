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
import pro.grain.admin.service.MarketService;
import pro.grain.admin.service.StationService;
import pro.grain.admin.service.dto.BidPriceDTO;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.service.error.MarketGenerationException;
import pro.grain.admin.web.rest.errors.CustomParameterizedException;
import pro.grain.admin.web.rest.util.HeaderUtil;
import pro.grain.admin.web.utils.SoyTemplatesUtils;

import javax.inject.Inject;
import javax.mail.internet.MimeUtility;
import javax.xml.crypto.KeySelectorException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pages")
public class MarketTableController {
    private final Logger log = LoggerFactory.getLogger(MarketTableController.class);

    private final MarketService marketService;

    @Inject
    public MarketTableController(MarketService marketService) throws IOException {
        this.marketService = marketService;
    }

    @RequestMapping(value = "/market-table",
        method = RequestMethod.GET,
        produces = MediaType.TEXT_HTML_VALUE)
    @Timed
    public ResponseEntity<String> getAllRegions(@RequestParam(value = "code", required = false) String code)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code {}", code);

        try {
            return ResponseEntity.ok()
                .body(marketService.getMarketTableHTML(code));

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
                .body(marketService.getMarketTableHTMLForDownload(code));

        } catch (MarketGenerationException e) {
            return ResponseEntity.ok()
                .headers(HeaderUtil.createAlert(e.getCause().getMessage(), ""))
                .body(null);
        }
    }
}
