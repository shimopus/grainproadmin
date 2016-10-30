package pro.grain.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pro.grain.admin.domain.enumeration.NDS;

/**
 * A Partner.
 */
@Entity
@Table(name = "partner")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "partner")
public class Partner implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "inn")
    private String inn;

    @Enumerated(EnumType.STRING)
    @Column(name = "nds")
    private NDS nds;

    @Column(name = "card")
    private String card;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @OneToMany(mappedBy = "agent")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Bid> agentBids = new HashSet<>();

    @OneToMany(mappedBy = "elevator")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Bid> elevatorBids = new HashSet<>();

    @OneToMany(mappedBy = "ownerFor")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONE)
    private Set<Partner> ownedBies = new HashSet<>();

    @ManyToOne
    private OrganisationType organisationType;

    @ManyToOne
    private District district;

    @ManyToOne
    private Region region;

    @ManyToOne
    private Locality locality;

    @ManyToOne
    private Station station;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "partner_contacts",
               joinColumns = @JoinColumn(name="partners_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="contacts_id", referencedColumnName="ID"))
    @Field( type = FieldType.Nested)
    private Set<Contact> contacts = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "partner_service_prices",
               joinColumns = @JoinColumn(name="partners_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="service_prices_id", referencedColumnName="ID"))
    private Set<ServicePrice> servicePrices = new HashSet<>();

    @ManyToOne
    private Partner ownerFor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Partner name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public Partner shortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getInn() {
        return inn;
    }

    public Partner inn(String inn) {
        this.inn = inn;
        return this;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public NDS getNds() {
        return nds;
    }

    public Partner nds(NDS nds) {
        this.nds = nds;
        return this;
    }

    public void setNds(NDS nds) {
        this.nds = nds;
    }

    public String getCard() {
        return card;
    }

    public Partner card(String card) {
        this.card = card;
        return this;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public Partner lastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Set<Bid> getAgentBids() {
        return agentBids;
    }

    public Partner agentBids(Set<Bid> bids) {
        this.agentBids = bids;
        return this;
    }

    public Partner addAgentBids(Bid bid) {
        agentBids.add(bid);
        bid.setAgent(this);
        return this;
    }

    public Partner removeAgentBids(Bid bid) {
        agentBids.remove(bid);
        bid.setAgent(null);
        return this;
    }

    public void setAgentBids(Set<Bid> bids) {
        this.agentBids = bids;
    }

    public Set<Bid> getElevatorBids() {
        return elevatorBids;
    }

    public Partner elevatorBids(Set<Bid> bids) {
        this.elevatorBids = bids;
        return this;
    }

    public Partner addElevatorBids(Bid bid) {
        elevatorBids.add(bid);
        bid.setElevator(this);
        return this;
    }

    public Partner removeElevatorBids(Bid bid) {
        elevatorBids.remove(bid);
        bid.setElevator(null);
        return this;
    }

    public void setElevatorBids(Set<Bid> bids) {
        this.elevatorBids = bids;
    }

    public Set<Partner> getOwnedBies() {
        return ownedBies;
    }

    public Partner ownedBies(Set<Partner> partners) {
        this.ownedBies = partners;
        return this;
    }

    public Partner addOwnedBy(Partner partner) {
        ownedBies.add(partner);
        partner.setOwnerFor(this);
        return this;
    }

    public Partner removeOwnedBy(Partner partner) {
        ownedBies.remove(partner);
        partner.setOwnerFor(null);
        return this;
    }

    public void setOwnedBies(Set<Partner> partners) {
        this.ownedBies = partners;
    }

    public OrganisationType getOrganisationType() {
        return organisationType;
    }

    public Partner organisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
        return this;
    }

    public void setOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
    }

    public District getDistrict() {
        return district;
    }

    public Partner district(District district) {
        this.district = district;
        return this;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Region getRegion() {
        return region;
    }

    public Partner region(Region region) {
        this.region = region;
        return this;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Locality getLocality() {
        return locality;
    }

    public Partner locality(Locality locality) {
        this.locality = locality;
        return this;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Station getStation() {
        return station;
    }

    public Partner station(Station station) {
        this.station = station;
        return this;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Set<Contact> getContacts() {
        return contacts;
    }

    public Partner contacts(Set<Contact> contacts) {
        this.contacts = contacts;
        return this;
    }

    public Partner addContacts(Contact contact) {
        contacts.add(contact);
        return this;
    }

    public Partner removeContacts(Contact contact) {
        contacts.remove(contact);
        return this;
    }

    public void setContacts(Set<Contact> contacts) {
        this.contacts = contacts;
    }

    public Set<ServicePrice> getServicePrices() {
        return servicePrices;
    }

    public Partner servicePrices(Set<ServicePrice> servicePrices) {
        this.servicePrices = servicePrices;
        return this;
    }

    public Partner addServicePrices(ServicePrice servicePrice) {
        servicePrices.add(servicePrice);
        return this;
    }

    public Partner removeServicePrices(ServicePrice servicePrice) {
        servicePrices.remove(servicePrice);
        return this;
    }

    public void setServicePrices(Set<ServicePrice> servicePrices) {
        this.servicePrices = servicePrices;
    }

    public Partner getOwnerFor() {
        return ownerFor;
    }

    public Partner ownerFor(Partner partner) {
        this.ownerFor = partner;
        return this;
    }

    public void setOwnerFor(Partner partner) {
        this.ownerFor = partner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Partner partner = (Partner) o;
        if(partner.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, partner.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Partner{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", shortName='" + shortName + "'" +
            ", inn='" + inn + "'" +
            ", nds='" + nds + "'" +
            ", card='" + card + "'" +
            ", lastUpdate='" + lastUpdate + "'" +
            '}';
    }
}
