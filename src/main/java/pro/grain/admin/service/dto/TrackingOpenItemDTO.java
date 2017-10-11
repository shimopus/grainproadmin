package pro.grain.admin.service.dto;

import java.time.LocalDate;
import java.util.Date;

public class TrackingOpenItemDTO {
    private Date mailDate;
    private int openCount;
    private int openFileCount;

    public TrackingOpenItemDTO(Date mailDate, int openCount, int openFileCount) {
        this.mailDate = mailDate;
        this.openCount = openCount;
        this.openFileCount = openFileCount;
    }

    public Date getMailDate() {
        return mailDate;
    }

    public void setMailDate(Date mailDate) {
        this.mailDate = mailDate;
    }

    public int getOpenCount() {
        return openCount;
    }

    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }

    public int getOpenFileCount() {
        return openFileCount;
    }

    public void setOpenFileCount(int openFileCount) {
        this.openFileCount = openFileCount;
    }
}
