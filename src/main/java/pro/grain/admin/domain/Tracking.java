package pro.grain.admin.domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;

import pro.grain.admin.domain.enumeration.MailOpenType;

/**
 * A Tracking.
 */
@Entity
@Table(name = "tracking")
@Cache(usage = CacheConcurrencyStrategy.NONE)
@Document(indexName = "tracking")

@NamedNativeQueries(
    {
        @NamedNativeQuery(name = "Tracking.findAllByPartner",
            query = "select open_type, mail_date, count(*) as open_count " +
                "from tracking " +
                "where " +
                "   partner_id = :partnerId " +
                "group by open_type, mail_date"
        ),
        @NamedNativeQuery(name = "Tracking.findAllCombined",
            query = "select open_type, mail_date, count(*) as open_count " +
                "from tracking " +
                "group by open_type, mail_date"
        ),
    }
)

public class Tracking implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "mail_date")
    private LocalDate mailDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "open_type")
    private MailOpenType openType;

    @Column(name = "event_date")
    private ZonedDateTime eventDate;

    @ManyToOne
    private Partner partner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getMailDate() {
        return mailDate;
    }

    public Tracking mailDate(LocalDate mailDate) {
        this.mailDate = mailDate;
        return this;
    }

    public void setMailDate(LocalDate mailDate) {
        this.mailDate = mailDate;
    }

    public MailOpenType getOpenType() {
        return openType;
    }

    public Tracking openType(MailOpenType openType) {
        this.openType = openType;
        return this;
    }

    public void setOpenType(MailOpenType openType) {
        this.openType = openType;
    }

    public ZonedDateTime getEventDate() {
        return eventDate;
    }

    public Tracking eventDate(ZonedDateTime eventDate) {
        this.eventDate = eventDate;
        return this;
    }

    public void setEventDate(ZonedDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public Partner getPartner() {
        return partner;
    }

    public Tracking partner(Partner partner) {
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
        Tracking tracking = (Tracking) o;
        if (tracking.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tracking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tracking{" +
            "id=" + id +
            ", mailDate='" + mailDate + "'" +
            ", openType='" + openType + "'" +
            ", eventDate='" + eventDate + "'" +
            '}';
    }
}
