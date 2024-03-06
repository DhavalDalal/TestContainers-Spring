package com.tsys.tc_spike.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class TransactionReference {
    public static final TransactionReference EMPTY = new TransactionReference(null, null, "");

    public final UUID id;
    public final Instant date;
    public final String status;

    public TransactionReference(@JsonProperty("id") UUID id,
                                @JsonProperty("date") Instant date,
                                @JsonProperty("status") String status) {
        this.id = id;
        this.date = date;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionReference that = (TransactionReference) o;
        return id.equals(that.id) &&
                date.equals(that.date) &&
                status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, status);
    }

    @Override
    public String toString() {
        return "TransactionReference{" +
                "date=" + date +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
