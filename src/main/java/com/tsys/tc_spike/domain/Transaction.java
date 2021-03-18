package com.tsys.tc_spike.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    public final UUID id;
    public final Date date;
    public final String status;
    public final String orderId;
    public final Money value;

    @Deprecated
    Transaction() {
        this(null, null, "", "", null);
    }

    public Transaction(UUID id, Date date, String status, String orderId, Money value) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.orderId = orderId;
        this.value = value;
    }

    public TransactionReference reference() {
        return new TransactionReference(id, date, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Transaction that = (Transaction) o;
        return id.equals(that.id) &&
                date.equals(that.date) &&
                status.equals(that.status) &&
                orderId.equals(that.orderId) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, status, orderId, value);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", orderId='" + orderId + '\'' +
                ", value=" + value +
                '}';
    }
}
