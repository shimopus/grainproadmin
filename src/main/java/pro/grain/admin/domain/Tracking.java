package pro.grain.admin.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import pro.grain.admin.domain.enumeration.MailOpenType;

/**
 * A Tracking.
 */
@Entity
@Table(name = "tracking")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tracking")
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

    @Column(name = "open_count")
    private Integer openCount;

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

    public Integer getOpenCount() {
        return openCount;
    }

    public Tracking openCount(Integer openCount) {
        this.openCount = openCount;
        return this;
    }

    public void setOpenCount(Integer openCount) {
        this.openCount = openCount;
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
        if(tracking.id == null || id == null) {
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
            ", openCount='" + openCount + "'" +
            '}';
    }
}
