package com.tsys.tc_spike.domain;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Currency;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Tag("UnitTest")
class TransactionSpecs {

    @Test
    public void createsTransactionReference() {
        // Given
        final Money inr_100_253 = new Money(Currency.getInstance("INR"), 100.253);
        final UUID id = UUID.nameUUIDFromBytes("TEST-ID".getBytes());
        final String orderId = "TEST-ORDER-ID";
        final Instant date = Instant.now();
        final String status = "accepted";
        final var transaction = new Transaction(id, date, status, orderId, inr_100_253);

        // When
        final var reference = transaction.reference();

        // Then
        var expected = new TransactionReference(id, date, status);
        assertThat(reference, is(expected));
    }

}