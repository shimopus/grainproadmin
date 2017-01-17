package pro.grain.admin.web.pages;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pro.grain.admin.service.PassportService;
import pro.grain.admin.service.dto.PassportDTO;

@Controller
@RequestMapping("/pages")
public class PassportImageController {
    private final Logger log = LoggerFactory.getLogger(PassportImageController.class);

    private final PassportService passportService;

    @Autowired
    public PassportImageController(PassportService passportService) {
        this.passportService = passportService;
    }

    @RequestMapping(value = "/passport/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Timed
    public ResponseEntity<byte[]> getAllRegionsForAdmin(@PathVariable(value = "id", required = false) Long passportId) {
        log.debug("REST request to get image for passport {}", passportId);

        PassportDTO passport = passportService.findOne(passportId);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(passport.getImage(), headers, HttpStatus.OK);
    }

}
