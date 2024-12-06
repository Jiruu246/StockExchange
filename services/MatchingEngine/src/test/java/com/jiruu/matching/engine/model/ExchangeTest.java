package com.jiruu.matching.engine.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeTest {

    @Test
    void registerUser() {
    }

    @Test
    void placeLimitOrder() {
        final Exchange exchange = new Exchange();

        final Transaction[] transactions1 = exchange.placeLimitOrder("1", false, 10, 20);
        assertArrayEquals(new Transaction[0], transactions1);

        final Transaction[] transactions2 = exchange.placeLimitOrder("2", true, 10, 20);
        assertArrayEquals(new Transaction[]{
                new Transaction("2", "1", 20.0, 10)
        }, transactions2);

        final Limit[] buyLimits = exchange.getAllLimits(true);
        final Limit[] sellLimits = exchange.getAllLimits(false);
        assertEquals(0, buyLimits.length);
        assertEquals(0, sellLimits.length);
    }

    @Test
    void placeMarketOrder() {
        final Exchange exchange = new Exchange();

        final Transaction[] transactions1 = exchange.placeLimitOrder("1", true, 10, 20);
        assertArrayEquals(new Transaction[0], transactions1);
        final Transaction[] transactions2 = exchange.placeMarketOrder("2", false, 10);
        assertArrayEquals(new Transaction[] {
                new Transaction("1", "2", 20.0, 10)
        }, transactions2);

        final Limit[] buyLimits = exchange.getAllLimits(true);
        final Limit[] sellLimits = exchange.getAllLimits(false);
        assertEquals(0, buyLimits.length);
        assertEquals(0, sellLimits.length);
    }

    @Test
    void getMarketPrice() {
    }

    @Test
    void userExists() {
    }

    @Test
    void getAllLimits() {
    }
}