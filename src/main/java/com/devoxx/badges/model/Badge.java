package com.devoxx.badges.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class Badge {

    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SuppressWarnings("unused")
    public Badge() {
    }

    public Badge(String qr) {
        if (qr != null && ! qr.isEmpty() && qr.split("::").length == 5) {
            String[] split = qr.split("::");
            badgeId.set(split[0]);
            lastName.set(split[1]);
            firstName.set(split[2]);
            company.set(split[3]);
            email.set(split[4]);
        }
    }

    private final StringProperty badgeId = new SimpleStringProperty();
    public StringProperty badgeIdProperty() { return badgeId; }
    public String getBadgeId() { return badgeId.get(); }
    public void setBadgeId(String badgeId) { this.badgeId.set(badgeId); }

    private final StringProperty firstName = new SimpleStringProperty();
    public StringProperty firstNameProperty() { return firstName; }
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }

    private final StringProperty lastName = new SimpleStringProperty();
    public StringProperty lastNameProperty() { return lastName; }
    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }

    private final StringProperty company = new SimpleStringProperty();
    public StringProperty companyProperty() { return company; }
    public String getCompany() { return company.get(); }
    public void setCompany(String company) { this.company.set(company); }

    private final StringProperty email = new SimpleStringProperty();
    public StringProperty emailProperty() { return email; }
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }

    private final StringProperty details = new SimpleStringProperty();
    public StringProperty detailsProperty() { return details; }
    public String getDetails() { return details.get(); }
    public void setDetails(String details) { this.details.set(details); }

    // dateTime
    private final LongProperty dateTime = new SimpleLongProperty(this, "dateTime");
    public final LongProperty dateTimeProperty() {
        return dateTime;
    }
    public final long getDateTime() {
        return dateTime.get();
    }
    public final void setDateTime(long value) {
        dateTime.set(value);
    }

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(getDateTime()),
                ZoneId.systemDefault());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Badge badge = (Badge) o;
        return Objects.equals(getBadgeId(), badge.getBadgeId()) &&
                Objects.equals(getDateTime(), badge.getDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBadgeId(), getDateTime());
    }

    protected String safeStr(String s) {
        return s == null? "": s.trim();
    }

    public String toCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append(safeStr(getBadgeId()));
        csv.append(",").append(safeStr(getFirstName()));
        csv.append(",").append(safeStr(getLastName()));
        csv.append(",").append(safeStr(getCompany()));
        csv.append(",").append(safeStr(getEmail()));
        csv.append(",").append(safeStr(getDetails()));
        if (getDateTime() != 0L) {
            csv.append(",").append(safeStr(DATE_TIME_FORMATTER.format(new Timestamp(getDateTime()))));
        }
        return csv.toString();
    }
    
}
