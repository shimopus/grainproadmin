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
    @Mapping(source = "agentContact.id", target = "agentContactId")
    @Mapping(source = "agent.id", target = "agentId")
    @Mapping(source = "agent.name", target = "agentName")
    BidFullDTO bidToBidFullDTO(Bid bid);

    List<BidFullDTO> bidsToBidFullDTOs(List<Bid> bids);

    @Mapping(source = "agentContactId", target = "agentContact")
    @Mapping(source = "agentId", target = "agent")
    Bid bidFullDTOToBid(BidFullDTO bidFullDTO);

    List<Bid> bidFullDTOsToBids(List<BidFullDTO> bidFullDTOs);
}