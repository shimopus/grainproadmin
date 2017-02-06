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
import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.repository.TransportationPriceRepository;
import pro.grain.admin.service.PriceDownloadService;
import pro.grain.admin.service.dto.StationDTO;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PriceResource {
    private final Logger log = LoggerFactory.getLogger(PriceResource.class);

    private final TransportationPriceRepository transportationPriceRepository;

    private final PriceDownloadService priceDownloadService;

    @Autowired
    public PriceResource(TransportationPriceRepository transportationPriceRepository, PriceDownloadService priceDownloadService) {
        this.transportationPriceRepository = transportationPriceRepository;
        this.priceDownloadService = priceDownloadService;
    }

    @RequestMapping(value = "/price",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getPrice(@RequestParam("from") String stationFromName,
                                            @RequestParam("to") String stationToName,
                                            @RequestParam(value = "priceWithNDS", required = false) Boolean isPriceWithNDS) {
        TransportationPrice transportationPrice = transportationPriceRepository.findByStationNames(stationFromName, stationToName);
        if (transportationPrice != null) {
            return new ResponseEntity<>(
                (isPriceWithNDS ? transportationPrice.getPriceNds() : transportationPrice.getPrice()).toString(),
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("0", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/price/getNextStations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StationDTO>> getNextStations() {
        return null;
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
