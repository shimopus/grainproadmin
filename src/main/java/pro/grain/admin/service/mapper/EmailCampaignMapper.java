package pro.grain.admin.service.mapper;

import pro.grain.admin.domain.*;
import pro.grain.admin.service.dto.EmailCampaignDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity EmailCampaign and its DTO EmailCampaignDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface EmailCampaignMapper {

    EmailCampaignDTO emailCampaignToEmailCampaignDTO(EmailCampaign emailCampaign);

    List<EmailCampaignDTO> emailCampaignsToEmailCampaignDTOs(List<EmailCampaign> emailCampaigns);

    EmailCampaign emailCampaignDTOToEmailCampaign(EmailCampaignDTO emailCampaignDTO);

    List<EmailCampaign> emailCampaignDTOsToEmailCampaigns(List<EmailCampaignDTO> emailCampaignDTOs);
}
