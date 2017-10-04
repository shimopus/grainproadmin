package pro.grain.admin.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.grain.admin.domain.BidPrice;
import pro.grain.admin.domain.Passport;
import pro.grain.admin.domain.TransportationPrice;
import pro.grain.admin.service.dto.BidPriceDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring", uses = {QualityValueMapper.class, PartnerMapper.class, PassportNoImageMapper.class})
public interface BidPriceMapper {
    @Mapping(target = "elevatorId", ignore = true)
    @Mapping(target = "elevatorName", ignore = true)
    @Mapping(source = "agentContact.id", target = "agentContactId")
    @Mapping(source = "agentContact.phone", target = "agentContactPhone")
    @Mapping(source = "agentContact.email.email", target = "agentContactEmail")
    @Mapping(source = "agent.id", target = "agentId")
    @Mapping(source = "agent.name", target = "agentName")
    @Mapping(source = "agent.shortName", target = "agentShortName")
    @Mapping(source = "agent.organisationType.type", target = "agentOrganisationType")
    @Mapping(source = "agent.card", target = "agentCard")
    @Mapping(source = "transportationPrice.id", target = "transportationPriceId")
    @Mapping(source = "transportationPrice.price", target = "transportationPricePrice")
    @Mapping(source = "transportationPrice.priceNds", target = "transportationPricePriceNds")
    @Mapping(source = "creationDate", target = "creationDateStr")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(target = "fcaPrice", ignore = true)
    @Mapping(target = "cptPrice", ignore = true)
    BidPriceDTO bidPriceToBidPriceDTO(BidPrice bid);

    List<BidPriceDTO> bidPricesToBidPriceDTOs(List<BidPrice> bids);

    @Mapping(source = "agentContactId", target = "agentContact")
    @Mapping(source = "agentId", target = "agent")
    @Mapping(target = "qualityPassports", ignore = true)
    @Mapping(source = "transportationPriceId", target = "transportationPrice")
    BidPrice bidPriceDTOToBidPrice(BidPriceDTO bidPriceDTO);

    List<BidPrice> bidPriceDTOsToBidPrices(List<BidPriceDTO> bidPriceDTOS);

    default TransportationPrice transportationPriceFromId(Long id) {
        if (id == null) {
            return null;
        }
        TransportationPrice transportationPrice = new TransportationPrice();
        transportationPrice.setId(id);
        return transportationPrice;
    }

    default String creationDateToCreationDateStr(LocalDateTime creationDate) {
        if (creationDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return creationDate.format(formatter);
    }
}
