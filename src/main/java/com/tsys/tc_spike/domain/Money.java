package com.tsys.tc_spike.domain;

import javax.persistence.Embeddable;
import java.util.Currency;
import java.util.Locale;

// Embeddable classes provides a convenient mapping for Value Objects.
@Embeddable
public class Money {
    public static final Money ZERO = new Money();
    public final Currency currency;
    public final Double amount;

    @Deprecated
    private Money() {
        this(Currency.getInstance(Locale.getDefault()), 0d);
    }

    public Money(Currency currency, Double amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Money add(Money other) {

        if (ZERO.equals(other))
            return this;

        if (this.equals(ZERO))
            return other;

        if (currency != other.currency)
            throw new IllegalArgumentException("For addition the currencies must be same!");

        return new Money(currency, amount + other.amount);
    }

    public String toString() {
        return String.format("%s %.2f", currency.getSymbol(), amount);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;

        if (other.getClass() != Money.class)
            return false;

        if (this == other)
            return true;

        var zero = Double.valueOf(0.0d);
        if (this.amount.equals(zero) && ((Money) other).amount.equals(zero))
            return true;

        Money that = (Money) other;
        return currency.equals(that.currency)
                && amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + currency.hashCode();
        hash = 97 * hash + amount.hashCode();
        return hash;
    }

    public boolean lessThan(Money other) {
        if (!other.currency.equals(currency))
            throw new IllegalArgumentException(String.format("Two currencies for comparison are => %s and %s\n. They must be same for comparison!", currency, other.currency));

        return amount < other.amount;
    }

    public Money multiply(Double factor) {
        return new Money(currency, amount * factor);
    }
}