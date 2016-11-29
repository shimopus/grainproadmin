package pro.grain.admin.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.grain.admin.service.BidService;
import pro.grain.admin.service.dto.BidDTO;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketResource {
    private final Logger log = LoggerFactory.getLogger(MarketResource.class);

    private final BidService bidService;

    @Inject
    public MarketResource(BidService bidService) {
        this.bidService = bidService;
    }

    /**
     * GET  /market : get all bids for the station.
     *
     * @param code code of the station. If not defined - do not return delivery price
     * @return the ResponseEntity with status 200 (OK) and the list of bids in body
     * @throws URISyntaxException if there is an error to generate response
     */
    @RequestMapping(value = "/market",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getAllRegions(@RequestParam(value = "code", required = false) String code)
        throws URISyntaxException {
        log.debug("REST request to get a list of bids for station code {}", code);

        List<? extends BidDTO> bids;

        if (code != null) {
            bids = bidService.getAllCurrentBidsForStation(code);
        } else {
            bids = bidService.getAllCurrentBids();
        }


        return new ResponseEntity<>(bids, HttpStatus.OK);
    }
}
