package pro.grain.admin.service;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.domain.Station;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class PriceDownloadService {
    private final Logger log = LoggerFactory.getLogger(PriceDownloadService.class);

    @Inject
    private PriceUpdateQueueService priceUpdateQueueService;

    public void initializeQueue() {
        log.debug("Initialize Queue");

        priceUpdateQueueService.clearQueue();

        int from = 0;

        log.debug("Updating Queue");
        updatePriceUpdateQueueService(from);
        log.debug("Updated Queue");
    }

    private void updatePriceUpdateQueueService(int from) {
        log.debug("Initialize async update Queue from: {}", from);
        CompletableFuture<Integer> futureResult = priceUpdateQueueService.initializeQueue(from);

        futureResult.thenRun(() -> {
            int newFrom = futureResult.join();
            log.debug("Next initialization from {}", newFrom);
            if (newFrom > 0) {
                updatePriceUpdateQueueService(newFrom);
            }
        });
    }
}
