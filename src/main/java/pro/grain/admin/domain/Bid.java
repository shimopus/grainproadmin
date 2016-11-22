package pro.grain.admin.domain;

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

import pro.grain.admin.domain.enumeration.QualityClass;

import pro.grain.admin.domain.enumeration.NDS;

/**
 * A Bid.
 */
@Entity
@Table(name = "bid")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "bid")
public class Bid implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "quality_class", nullable = false)
    private QualityClass qualityClass;

    @NotNull
    @Column(name = "volume", nullable = false)
    private Integer volume;

    @NotNull
    @Column(name = "price", nullable = false)
    private Long price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "nds", nullable = false)
    private NDS nds;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "archive_date")
    private LocalDate archiveDate;

    @ManyToOne
    private Contact agentContact;

    @ManyToMany(cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "bid_quality_parameters",
               joinColumns = @JoinColumn(name="bids_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="quality_parameters_id", referencedColumnName="ID"))
    private Set<QualityValue> qualityParameters = new HashSet<>();

    @ManyToOne
    private Partner agent;

    @ManyToOne
    private Partner elevator;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "bid_quality_passports",
               joinColumns = @JoinColumn(name="bids_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="quality_passports_id", referencedColumnName="ID"))
    private Set<Passport> qualityPassports = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Bid creationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public QualityClass getQualityClass() {
        return qualityClass;
    }

    public Bid qualityClass(QualityClass qualityClass) {
        this.qualityClass = qualityClass;
        return this;
    }

    public void setQualityClass(QualityClass qualityClass) {
        this.qualityClass = qualityClass;
    }

    public Integer getVolume() {
        return volume;
    }

    public Bid volume(Integer volume) {
        this.volume = volume;
        return this;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Long getPrice() {
        return price;
    }

    public Bid price(Long price) {
        this.price = price;
        return this;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public NDS getNds() {
        return nds;
    }

    public Bid nds(NDS nds) {
        this.nds = nds;
        return this;
    }

    public void setNds(NDS nds) {
        this.nds = nds;
    }

    public Boolean isIsActive() {
        return isActive;
    }

    public Bid isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDate getArchiveDate() {
        return archiveDate;
    }

    public Bid archiveDate(LocalDate archiveDate) {
        this.archiveDate = archiveDate;
        return this;
    }

    public void setArchiveDate(LocalDate archiveDate) {
        this.archiveDate = archiveDate;
    }

    public Contact getAgentContact() {
        return agentContact;
    }

    public Bid agentContact(Contact contact) {
        this.agentContact = contact;
        return this;
    }

    public void setAgentContact(Contact contact) {
        this.agentContact = contact;
    }

    public Set<QualityValue> getQualityParameters() {
        return qualityParameters;
    }

    public Bid qualityParameters(Set<QualityValue> qualityValues) {
        this.qualityParameters = qualityValues;
        return this;
    }

    public Bid addQualityParameters(QualityValue qualityValue) {
        qualityParameters.add(qualityValue);
        return this;
    }

    public Bid removeQualityParameters(QualityValue qualityValue) {
        qualityParameters.remove(qualityValue);
        return this;
    }

    public void setQualityParameters(Set<QualityValue> qualityValues) {
        this.qualityParameters = qualityValues;
    }

    public Partner getAgent() {
        return agent;
    }

    public Bid agent(Partner partner) {
        this.agent = partner;
        return this;
    }

    public void setAgent(Partner partner) {
        this.agent = partner;
    }

    public Partner getElevator() {
        return elevator;
    }

    public Bid elevator(Partner partner) {
        this.elevator = partner;
        return this;
    }

    public void setElevator(Partner partner) {
        this.elevator = partner;
    }

    public Set<Passport> getQualityPassports() {
        return qualityPassports;
    }

    public Bid qualityPassports(Set<Passport> passports) {
        this.qualityPassports = passports;
        return this;
    }

    public Bid addQualityPassports(Passport passport) {
        qualityPassports.add(passport);
        return this;
    }

    public Bid removeQualityPassports(Passport passport) {
        qualityPassports.remove(passport);
        return this;
    }

    public void setQualityPassports(Set<Passport> passports) {
        this.qualityPassports = passports;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Bid bid = (Bid) o;
        if(bid.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, bid.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Bid{" +
            "id=" + id +
            ", creationDate='" + creationDate + "'" +
            ", qualityClass='" + qualityClass + "'" +
            ", volume='" + volume + "'" +
            ", price='" + price + "'" +
            ", nds='" + nds + "'" +
            ", isActive='" + isActive + "'" +
            ", archiveDate='" + archiveDate + "'" +
            '}';
    }
}
