package pro.grain.admin.domain;

public class BidPrice extends Bid {
    private TransportationPrice transportationPrice;

    public BidPrice(){

    }

    public BidPrice(Bid bid){
        this.setId(bid.getId());
        this.setAgent(bid.getAgent());
        this.setAgentContact(bid.getAgentContact());
        this.setArchiveDate(bid.getArchiveDate());
        this.setCreationDate(bid.getCreationDate());
        this.setElevator(bid.getElevator());
        this.setIsActive(bid.isIsActive());
        this.setNds(bid.getNds());
        this.setBidType(bid.getBidType());
        this.setPrice(bid.getPrice());
        this.setQualityClass(bid.getQualityClass());
        this.setQualityParameters(bid.getQualityParameters());
        this.setQualityPassports(bid.getQualityPassports());
        this.setVolume(bid.getVolume());
    }

    public BidPrice(Bid bid, TransportationPrice transportationPrice) {
        this(bid);
        this.transportationPrice = transportationPrice;
    }

    public TransportationPrice getTransportationPrice() {
        return transportationPrice;
    }

    public BidPrice setTransportationPrice(TransportationPrice transportationPrice) {
        this.transportationPrice = transportationPrice;
        return this;
    }
}
