package pro.grain.admin.web.rest.price;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.grain.admin.config.GrainProAdminProperties;
import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.repository.StationRepository;
import pro.grain.admin.repository.TransportationPriceRepository;
import pro.grain.admin.service.PriceDownloadService;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PriceResource {
    private final Logger log = LoggerFactory.getLogger(PriceResource.class);

    private final TransportationPriceRepository transportationPriceRepository;

    private final PriceDownloadService priceDownloadService;

    private final GrainProAdminProperties grainProAdminProperties;

    private final StationRepository stationRepository;

    @Autowired
    public PriceResource(TransportationPriceRepository transportationPriceRepository, PriceDownloadService priceDownloadService, GrainProAdminProperties grainProAdminProperties, StationRepository stationRepository) {
        this.transportationPriceRepository = transportationPriceRepository;
        this.priceDownloadService = priceDownloadService;
        this.grainProAdminProperties = grainProAdminProperties;
        this.stationRepository = stationRepository;
    }

    @RequestMapping(value = "/price",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getPrice(@RequestParam("from") String stationFromName,
                                            @RequestParam("to") String stationToName,
                                            @RequestParam(value = "priceWithNDS", required = false) Boolean isPriceWithNDS) {
        TransportationPrice transportationPrice = transportationPriceRepository.findByStationNames(
            stationFromName, stationToName, grainProAdminProperties.getPrice().getCurrentVersionNumber());
        if (transportationPrice != null) {
            return new ResponseEntity<>(
                (isPriceWithNDS ? transportationPrice.getPriceNds() : transportationPrice.getPrice()).toString(),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("0", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/price/new",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> setPrice(@RequestParam("from_station") String stationFromName,
                                           @RequestParam("to_station") String stationToName,
                                           @RequestParam("price") Float price,
                                           @RequestParam("price_nds") Float priceWithNDS,
                                           @RequestParam("distance") Long distance) {

        TransportationPrice tp = new TransportationPrice();
        tp.setStationFrom(stationRepository.findByName(stationFromName));
        tp.setStationTo(stationRepository.findByName(stationToName));
        tp.setPrice(price.longValue());
        tp.setPriceNds(priceWithNDS.longValue());
        tp.setDistance(distance.intValue());

        priceDownloadService.addNewPrice(tp);

        log.warn("!!! SQL !!! Save {} -> {}", tp.getStationFrom().getName(), tp.getStationTo().getName());

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/price/nextStations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<String>> getNextStations() {
        List<String> pair = priceDownloadService.getNextStations();

        if (pair != null) {
            log.warn("!!! SQL !!! Next {} -> {}", pair.get(0), pair.get(1));
            return new ResponseEntity<>(
                pair,
                HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/price/downloadStart",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> startDownloading() {
        priceDownloadService.initializeQueue();
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
