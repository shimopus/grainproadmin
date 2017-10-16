package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import pro.grain.admin.domain.enumeration.SubscriptionType;

/**
 * A SubscriptionConfig.
 */
@Entity
@Table(name = "subscription_config")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "subscriptionconfig")
public class SubscriptionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private SubscriptionType subscriptionType;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "creation_date")
    private ZonedDateTime creationDate;

    @Column(name = "last_update_date")
    private ZonedDateTime lastUpdateDate;

    @OneToOne
    @JoinColumn()
    private Contact contact;

    @OneToOne
    @JoinColumn()
    private Station station;

    @OneToOne
    @JoinColumn()
    private Partner partner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public SubscriptionConfig subscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
        return this;
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Boolean isIsActive() {
        return isActive;
    }

    public SubscriptionConfig isActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public SubscriptionConfig creationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ZonedDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public SubscriptionConfig lastUpdateDate(ZonedDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Contact getContact() {
        return contact;
    }

    public SubscriptionConfig contact(Contact contact) {
        this.contact = contact;
        return this;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public Station getStation() {
        return station;
    }

    public SubscriptionConfig station(Station station) {
        this.station = station;
        return this;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Partner getPartner() {
        return partner;
    }

    public SubscriptionConfig partner(Partner partner) {
        this.partner = partner;
        return this;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubscriptionConfig subscriptionConfig = (SubscriptionConfig) o;
        if(subscriptionConfig.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, subscriptionConfig.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SubscriptionConfig{" +
            "id=" + id +
            ", subscriptionType='" + subscriptionType + "'" +
            ", isActive='" + isActive + "'" +
            ", creationDate='" + creationDate + "'" +
            ", lastUpdateDate='" + lastUpdateDate + "'" +
            '}';
    }
}
