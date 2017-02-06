package pro.grain.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class PriceDownloadService {
    private final Logger log = LoggerFactory.getLogger(PriceDownloadService.class);

    @Inject
    private PriceUpdateQueueService priceUpdateQueueService;

    public void initializeQueue() {
        log.debug("Initialize Queue");

        priceUpdateQueueService.clearQueue();
        priceUpdateQueueService.initializeQueue();

        log.debug("Queue initialized with size");
    }
}
