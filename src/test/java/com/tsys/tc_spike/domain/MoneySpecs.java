package com.tsys.tc_spike.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Currency;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
public class MoneySpecs {
    private final Money inr_100_253 = new Money(Currency.getInstance("INR"), 100.253);
    private final Money usd5 = new Money(Currency.getInstance("USD"), 5d);

    @Test
    public void stringRepresentedBySymbolWithAmount2HavingPlacesOfDecimal() {
        assertThat(inr_100_253.toString(), is("₹ 100.25"));
    }

    @Test
    public void twoZeroMoniesInDifferentCurrenciesAreEqual() {
        assertEquals(new Money(Currency.getInstance("INR"), 0.0d), new Money(Currency.getInstance("USD"), 0.0d));
    }

    @Test
    public void zeroMoneyActsAsIdentity() {
        assertEquals(inr_100_253, inr_100_253.add(Money.ZERO));
        assertEquals(usd5, Money.ZERO.add(usd5));
    }

    @Test
    public void addsTwoValuesHavingSameCurrency() {
        assertThat(inr_100_253.add(inr_100_253), is(new Money(Currency.getInstance("INR"), 200.506)));
    }

    @Test
    public void shoutsWhenAddingDifferentCurrencies() {
        assertThrows(IllegalArgumentException.class,
                () -> inr_100_253.add(usd5),
                "For addition the currencies must be same!");
    }

    @Test
    public void equality() {
        assertThat(inr_100_253.equals(inr_100_253), is(true));
        assertThat(inr_100_253.equals(new Money(Currency.getInstance("INR"), 100.253)), is(true));
        assertThat(inr_100_253.equals(null), is(false));
        assertThat(inr_100_253.equals(usd5), is(false));
    }

    @Test
    public void lessThanComparison() {
        Money inr10 = new Money(Currency.getInstance("INR"), 10d);
        assertThat(inr_100_253.lessThan(inr10), is(false));
        assertThat(inr_100_253.lessThan(inr_100_253), is(false));
        assertThat(inr10.lessThan(inr_100_253), is(true));
    }

    @Test
    public void multipliesAmountByAFactor() {
        assertThat(inr_100_253.multiply(2d), is(new Money(Currency.getInstance("INR"), 200.506)));
    }
}
