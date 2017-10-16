package pro.grain.admin.web.pages;

import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pro.grain.admin.domain.enumeration.MailOpenType;
import pro.grain.admin.service.TrackingService;
import pro.grain.admin.service.dto.TrackingDTO;
import pro.grain.admin.service.dto.TrackingOpenItemDTO;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/tracking")
public class TrackingImageController {
    private byte[] trackingGif = { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x1, 0x0, 0x1, 0x0, (byte) 0x80, 0x0, 0x0, (byte)  0xff, (byte)  0xff,  (byte) 0xff, 0x0, 0x0, 0x0, 0x2c, 0x0, 0x0, 0x0, 0x0, 0x1, 0x0, 0x1, 0x0, 0x0, 0x2, 0x2, 0x44, 0x1, 0x0, 0x3b };

    private final TrackingService trackingService;

    @Autowired
    public TrackingImageController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @RequestMapping(value = "/image/{partnerId}/1x1.gif",
        method = RequestMethod.GET)
    @Timed
    public ResponseEntity<byte[]> getPassportImage(
        @PathVariable(value = "partnerId", required = false) Long partnerId,
        @RequestParam(value = "date", required = false) String mailDate,
        @RequestParam(value = "type", required = false) MailOpenType openType) {

        if (partnerId != null && mailDate != null && openType != null) {
            TrackingDTO tracking = new TrackingDTO();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(mailDate, formatter);
            tracking.setMailDate(date);
            tracking.setPartnerId(partnerId);
            tracking.setOpenType(openType);
            tracking.setEventDate(ZonedDateTime.now());
            trackingService.save(tracking);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("image/gif"));
        return new ResponseEntity<>(trackingGif, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/byPartner",
        method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<TrackingOpenItemDTO>> getStatisticsByPartner(
        @RequestParam(value = "partnerId", required = false) Long partnerId) {
        return new ResponseEntity<>(trackingService.findAllByPartner(partnerId), HttpStatus.OK);
    }
}
