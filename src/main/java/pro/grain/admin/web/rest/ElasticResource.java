package pro.grain.admin.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pro.grain.admin.service.ElasticSearchIndexRegenerateService;

import javax.inject.Inject;
import java.net.URISyntaxException;

@RestController
@RequestMapping(value = "/elastic", produces = MediaType.APPLICATION_JSON_VALUE)
public class ElasticResource {
    private final ElasticSearchIndexRegenerateService elasticSearchIndexRegenerateService;

    @Inject
    public ElasticResource(ElasticSearchIndexRegenerateService elasticSearchIndexRegenerateService) {
        this.elasticSearchIndexRegenerateService = elasticSearchIndexRegenerateService;
    }

    @RequestMapping(value = "reset", method = RequestMethod.GET)
    public ResponseEntity<String> reset() throws URISyntaxException {
        elasticSearchIndexRegenerateService.resetStations();
        elasticSearchIndexRegenerateService.resetPartners();
        return new ResponseEntity<>("{\"status\": \"Ok\"}", HttpStatus.OK);
    }
}
