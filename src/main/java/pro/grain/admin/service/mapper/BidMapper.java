package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.BidDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Bid and its DTO BidDTO.
 */
@Mapper(componentModel = "spring", uses = {QualityValueMapper.class, })
public interface BidMapper {

    @Mapping(source = "agentContact.id", target = "agentContactId")
    @Mapping(source = "agent.id", target = "agentId")
    @Mapping(source = "agent.name", target = "agentName")
    @Mapping(source = "elevator.id", target = "elevatorId")
    @Mapping(source = "elevator.name", target = "elevatorName")
    BidDTO bidToBidDTO(Bid bid);

    List<BidDTO> bidsToBidDTOs(List<Bid> bids);

    @Mapping(source = "agentContactId", target = "agentContact")
    @Mapping(source = "agentId", target = "agent")
    @Mapping(source = "elevatorId", target = "elevator")
    Bid bidDTOToBid(BidDTO bidDTO);

    List<Bid> bidDTOsToBids(List<BidDTO> bidDTOs);

    default Contact contactFromId(Long id) {
        if (id == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setId(id);
        return contact;
    }

    default QualityValue qualityValueFromId(Long id) {
        if (id == null) {
            return null;
        }
        QualityValue qualityValue = new QualityValue();
        qualityValue.setId(id);
        return qualityValue;
    }

    default Partner partnerFromId(Long id) {
        if (id == null) {
            return null;
        }
        Partner partner = new Partner();
        partner.setId(id);
        return partner;
    }
}
