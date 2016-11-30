package pro.grain.admin.service;

import pro.grain.admin.domain.Bid;
import pro.grain.admin.domain.BidPrice;
import pro.grain.admin.repository.BidRepository;
import pro.grain.admin.repository.search.BidSearchRepository;
import pro.grain.admin.service.dto.BidDTO;
import pro.grain.admin.service.dto.BidFullDTO;
import pro.grain.admin.service.dto.BidPriceDTO;
import pro.grain.admin.service.mapper.BidFullMapper;
import pro.grain.admin.service.mapper.BidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import pro.grain.admin.service.mapper.BidPriceMapper;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Bid.
 */
@Service
@Transactional
public class BidService {

    private final Logger log = LoggerFactory.getLogger(BidService.class);

    private final BidRepository bidRepository;

    private final BidMapper bidMapper;

    private final BidFullMapper bidFullMapper;

    private final BidPriceMapper bidPriceMapper;

    private final BidSearchRepository bidSearchRepository;

    @Inject
    public BidService(BidRepository bidRepository, BidMapper bidMapper, BidFullMapper bidFullMapper, BidPriceMapper bidPriceMapper, BidSearchRepository bidSearchRepository) {
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
        this.bidFullMapper = bidFullMapper;
        this.bidPriceMapper = bidPriceMapper;
        this.bidSearchRepository = bidSearchRepository;
    }

    /**
     * Save a bid.
     *
     * @param bidDTO the entity to save
     * @return the persisted entity
     */
    public BidDTO save(BidDTO bidDTO) {
        log.debug("Request to save Bid : {}", bidDTO);
        Bid bid = bidMapper.bidDTOToBid(bidDTO);
        bid = bidRepository.save(bid);
        BidDTO result = bidMapper.bidToBidDTO(bid);
        bidSearchRepository.save(bid);
        return result;
    }

    /**
     *  Get all the bids.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BidDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Bids");
        Page<Bid> result = bidRepository.findAll(pageable);
        return result.map(bidMapper::bidToBidDTO);
    }

    /**
     *  Get all not archived bids by partner.
     *
     *  @param partnerId the partner
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<BidFullDTO> findByPartnerNotArchived(Long partnerId) {
        log.debug("Request to get all Bids by partner");
        List<Bid> result = bidRepository.findAllNotArchivedWithEagerRelationshipsByPartner(partnerId);
        return bidFullMapper.bidsToBidFullDTOs(result);
    }

    /**
     *  Get all archived bids by partner.
     *
     *  @param partnerId the partner
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<BidFullDTO> findByPartnerArchived(Long partnerId) {
        log.debug("Request to get all Bids by partner");
        List<Bid> result = bidRepository.findAllArchivedWithEagerRelationshipsByPartner(partnerId);
        return bidFullMapper.bidsToBidFullDTOs(result);
    }

    /**
     *  Get one bid by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public BidDTO findOne(Long id) {
        log.debug("Request to get Bid : {}", id);
        Bid bid = bidRepository.findOneWithEagerRelationships(id);
        return bidMapper.bidToBidDTO(bid);
    }

    /**
     *  Delete the  bid by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Bid : {}", id);
        bidRepository.delete(id);
        bidSearchRepository.delete(id);
    }

    /**
     *  get all current bids for the station.
     *
     *  @param code station code
     */
    public List<BidPriceDTO> getAllCurrentBidsForStation(String code) {
        log.debug("Request to get all current Bids for station : {}", code);

        List<BidPrice> bids = bidRepository.findAllCurrentWithEagerRelationships(code);

        return enrichAndSortMarket(bidPriceMapper.bidPricesToBidPriceDTOs(bids), true);
    }

    /**
     *  get all current bids.
     *
     */
    public List<BidPriceDTO> getAllCurrentBids() {
        log.debug("Request to get all current Bids");

        List<BidPrice> bids = bidRepository.findAllCurrentWithEagerRelationships();

        return enrichAndSortMarket(bidPriceMapper.bidPricesToBidPriceDTOs(bids), false);
    }

    /**
     * Search for the bid corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<BidDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Bids for query {}", query);
        Page<Bid> result = bidSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(bidMapper::bidToBidDTO);
    }

    private Long getFCAPrice(BidPriceDTO bid) {
        Long selfPrice = bid.getPrice();
        Long loadPrice = 0L;

        if (bid.getElevator().getServicePrices() != null && bid.getElevator().getServicePrices().size() > 0) {
            loadPrice = bid.getElevator().getServicePrices().iterator().next().getPrice();
        }

        return selfPrice + loadPrice;
    }

    private Long getCPTPrice(BidPriceDTO bid) {
        Long transpPrice = 0L;

        if (bid.getTransportationPricePrice() != null) {
            transpPrice = bid.getTransportationPricePrice();
        }

        return getFCAPrice(bid) + transpPrice;
    }

    private List<BidPriceDTO> enrichAndSortMarket(List<BidPriceDTO> bids, boolean hasTransportationPrice) {
        if (bids == null) return null;
        return bids.stream()
            .map(bid -> {
                bid.setFcaPrice(getFCAPrice(bid));
                if (hasTransportationPrice) {
                    bid.setCptPrice(getCPTPrice(bid));
                }
                return bid;
            })
            .sorted((bid1, bid2) ->
                hasTransportationPrice ?
                    Long.compare(bid2.getCptPrice(), bid1.getCptPrice()) :
                    Long.compare(bid2.getFcaPrice(), bid1.getFcaPrice())
            )
            .collect(Collectors.toList());
    }
}
