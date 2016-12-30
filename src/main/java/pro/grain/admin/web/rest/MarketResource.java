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
import pro.grain.admin.domain.enumeration.QualityClass;
import pro.grain.admin.service.BidService;
import pro.grain.admin.service.StationService;
import pro.grain.admin.service.dto.BidPriceDTO;
import pro.grain.admin.service.dto.StationDTO;
import pro.grain.admin.web.rest.util.HeaderUtil;

import javax.inject.Inject;
import javax.xml.crypto.KeySelectorException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MarketResource {
    private final Logger log = LoggerFactory.getLogger(MarketResource.class);

    private final BidService bidService;
    private final StationService stationService;

    @Inject
    public MarketResource(BidService bidService, StationService stationService) {
        this.bidService = bidService;
        this.stationService = stationService;
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

        Map<QualityClass, List<BidPriceDTO>> bids;

        if (code != null) {
            try {
                String newCode = calculateDestinationStation(code);
                bids = bidService.getAllCurrentBidsForStation(newCode);
            } catch (KeySelectorException e) {
                return ResponseEntity.ok()
                    .headers(HeaderUtil.createAlert(e.getMessage(), ""))
                    .body(null);

            }
        } else {
            bids = bidService.getAllCurrentBids();
        }

        return new ResponseEntity<>(bids, HttpStatus.OK);
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
