package pro.grain.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grain.admin.config.GrainProAdminProperties;
import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.repository.TransportationPriceRepository;
import pro.grain.admin.service.dto.PriceUpdateQueueDTO;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class PriceDownloadService {
    private final Logger log = LoggerFactory.getLogger(PriceDownloadService.class);

    @Inject
    private PriceUpdateQueueService priceUpdateQueueService;

    @Inject
    private GrainProAdminProperties grainProAdminProperties;

    @Inject
    private TransportationPriceRepository transportationPriceRepository;

    public void initializeQueue() {
        log.debug("Initialize Queue");

        priceUpdateQueueService.clearQueue();

        int from = 0;

        log.debug("Updating Queue");
        updatePriceUpdateQueueService(from);
        log.debug("Updated Queue");
    }

    public synchronized List<String> getNextStations() {
        PriceUpdateQueueDTO priceUpdateQueue = priceUpdateQueueService.findNextAvailable();

        if (priceUpdateQueue == null) return null;

        priceUpdateQueueService.markAsUnavailable(priceUpdateQueue.getId());

        List<String> result = new ArrayList<>(2);
        result.add(priceUpdateQueue.getStationFromName());
        result.add(priceUpdateQueue.getStationToName());

        return result;
    }

    public void addNewPrice(TransportationPrice transportationPrice) {
        transportationPrice.setVersionNumber(grainProAdminProperties.getPrice().getCurrentVersionNumber() + 1);
        transportationPrice.setLoadingDate(LocalDate.now());

        transportationPriceRepository.save(transportationPrice);
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
