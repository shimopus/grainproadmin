package pro.grain.admin.service.dto;

public class BidPriceDTO extends BidFullDTO {
    private Long transportationPricePrice;
    private Long transportationPriceId;

    public Long getTransportationPricePrice() {
        return transportationPricePrice;
    }

    public BidPriceDTO setTransportationPricePrice(Long transportationPricePrice) {
        this.transportationPricePrice = transportationPricePrice;
        return this;
    }

    public Long getTransportationPriceId() {
        return transportationPriceId;
    }

    public BidPriceDTO setTransportationPriceId(Long transportationPriceId) {
        this.transportationPriceId = transportationPriceId;
        return this;
    }
}
