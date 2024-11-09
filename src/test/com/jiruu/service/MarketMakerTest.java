package com.jiruu.service;

import com.jiruu.model.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketMakerTest {

    @Test
    void generateQuote() {
        MarketMaker marketMaker = new MarketMaker(20_000, "1");
        Order bid = marketMaker.generateQuote(true, 100);
        Order ask = marketMaker.generateQuote(false, 100);

       assertTrue(bid.isBuy());
       assertFalse(ask.isBuy());
       assertTrue(bid.getLimit() < ask.getLimit());
    }
}