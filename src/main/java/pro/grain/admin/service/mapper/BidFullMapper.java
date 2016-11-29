package pro.grain.admin.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pro.grain.admin.domain.Bid;
import pro.grain.admin.service.dto.BidFullDTO;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BidMapper.class, QualityValueMapper.class})
public interface BidFullMapper {
    @Mapping(target = "elevatorId", ignore = true)
    @Mapping(target = "elevatorName", ignore = true)
    @Mapping(target = "qualityPassports", ignore = true)
    @Mapping(source = "agentContact.id", target = "agentContactId")
    @Mapping(source = "agentContact.phone", target = "agentContactPhone")
    @Mapping(source = "agentContact.email.email", target = "agentContactEmail")
    @Mapping(source = "agent.id", target = "agentId")
    @Mapping(source = "agent.name", target = "agentName")
    @Mapping(source = "agent.organisationType.type", target = "agentOrganisationType")
    BidFullDTO bidToBidFullDTO(Bid bid);

    List<BidFullDTO> bidsToBidFullDTOs(List<Bid> bids);

    @Mapping(source = "agentContactId", target = "agentContact")
    @Mapping(source = "agentId", target = "agent")
    @Mapping(target = "qualityPassports", ignore = true)
    Bid bidFullDTOToBid(BidFullDTO bidFullDTO);

    List<Bid> bidFullDTOsToBids(List<BidFullDTO> bidFullDTOs);
}
