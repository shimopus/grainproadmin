package pro.grain.admin.service.dto;

import javax.validation.constraints.NotNull;

public class BidPriceDTO extends BidFullDTO {
    private String creationDateStr;
    private String agentCard;
    private String agentShortName;
    private Long transportationPricePrice;
    private Long transportationPricePriceNds;
    private Long transportationPriceId;
    private Long fcaPrice;
    private Long cptPrice;

    public String getAgentCard() {
        return agentCard;
    }

    public BidPriceDTO setAgentCard(String agentCard) {
        this.agentCard = agentCard;
        return this;
    }

    public String getAgentShortName() {
        return agentShortName;
    }

    public void setAgentShortName(String agentShortName) {
        this.agentShortName = agentShortName;
    }

    public Long getTransportationPricePrice() {
        return transportationPricePrice;
    }

    public BidPriceDTO setTransportationPricePrice(Long transportationPricePrice) {
        this.transportationPricePrice = transportationPricePrice;
        return this;
    }

    public Long getTransportationPricePriceNds() {
        return transportationPricePriceNds;
    }

    public void setTransportationPricePriceNds(Long transportationPricePriceNds) {
        this.transportationPricePriceNds = transportationPricePriceNds;
    }

    public Long getTransportationPriceId() {
        return transportationPriceId;
    }

    public BidPriceDTO setTransportationPriceId(Long transportationPriceId) {
        this.transportationPriceId = transportationPriceId;
        return this;
    }

    public Long getFcaPrice() {
        return fcaPrice;
    }

    public BidPriceDTO setFcaPrice(Long fcaPrice) {
        this.fcaPrice = fcaPrice;
        return this;
    }

    public Long getCptPrice() {
        return cptPrice;
    }

    public BidPriceDTO setCptPrice(Long cptPrice) {
        this.cptPrice = cptPrice;
        return this;
    }

    public String getCreationDateStr() {
        return creationDateStr;
    }

    public void setCreationDateStr(String creationDateStr) {
        this.creationDateStr = creationDateStr;
    }
}
