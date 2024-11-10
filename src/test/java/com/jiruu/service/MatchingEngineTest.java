package com.jiruu.service;

import com.jiruu.model.Match;
import com.jiruu.model.Order;
import com.jiruu.model.OrderBook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    @Test
    void matchLimitOrder() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 110.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 120.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);
        orderBook.addOrder(sellOrder2);

        final Order buyOrder2 = new Order("3", "1", "1", true, 30, 120.0);
        final Match[] matches = MatchingEngine.MatchLimitOrder(buyOrder2, orderBook);

        assertArrayEquals(
                new Match[] {
                        new Match("3", "2", 115.0, 10),
                        new Match("3", "4", 120.0, 10)},
                matches);
        assertArrayEquals(
                new Order[] {
                        new Order("3", "1", "1", true, 10, 110.0),
                        buyOrder},
                orderBook.getBidOrders().toArray());
        assertEquals(2, orderBook.getBidOrders().size());
        assertEquals(0, orderBook.getAskOrders().size());
    }

    @Test
    void matchOrderWithNoMatchLimit() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 110.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Order buyOrder2 = new Order("3", "1", "1", true, 10, 90.0);
        final Match[] matches = MatchingEngine.MatchLimitOrder(buyOrder2, orderBook);

        assertEquals(0, matches.length);
        assertEquals(2, orderBook.getBidOrders().size());
        assertEquals(1, orderBook.getAskOrders().size());
        assertArrayEquals(
                new Order[] {buyOrder, buyOrder2},
                orderBook.getBidOrders().toArray());
    }

    @Test
    void matchOrderWithFullMatchLimit() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 110.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Order buyOrder2 = new Order("3", "1", "1", true, 10, 110.0);
        final Match[] matches = MatchingEngine.MatchLimitOrder(buyOrder2, orderBook);

        assertArrayEquals(
                new Match[] {new Match("3", "2", 110.0, 10)},
                matches);
        assertEquals(1, orderBook.getBidOrders().size());
        assertEquals(0, orderBook.getAskOrders().size());
    }

    @Test
    void exactMatchMarketOrder() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 110.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Order marketBuyOrder = new Order("3", "1", "1", true, 10, -100);
        Match[] matches = MatchingEngine.MatchMarketOrder(marketBuyOrder, orderBook);

        assertArrayEquals(
                new Match[] {new Match("3", "2", 110.0, 10)},
                matches);

        assertEquals(1, orderBook.getBidOrders().size());
        assertEquals(0, orderBook.getAskOrders().size());
    }

    @Test
    void partialMatchMarketOrder() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 110.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 120.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);
        orderBook.addOrder(sellOrder2);

        final Order limitBuyOrder = new Order("3", "1", "1", true, 30, -100);
        final Match[] matches = MatchingEngine.MatchMarketOrder(limitBuyOrder, orderBook);

        assertArrayEquals(
                new Match[] {
                        new Match("3", "2", 110.0, 10),
                        new Match("3", "4", 120.0, 10)},
                matches);
        assertEquals(1, orderBook.getBidOrders().size());
        assertEquals(0, orderBook.getAskOrders().size());
    }

    @Test
    void partialMatchMarketOrderOtherSide(){
        final Order buyOrder = new Order("1", "1", "1", true, 20, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 110.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Order limitSellOrder = new Order("3", "1", "1", false, 10, -100);
        final Match[] matches = MatchingEngine.MatchMarketOrder(limitSellOrder, orderBook);

        assertArrayEquals(
                new Match[] {new Match("1", "3", 100.0, 10)},
                matches);
        assertEquals(1, orderBook.getBidOrders().size());
        assertEquals(1, orderBook.getAskOrders().size());
        assertArrayEquals(
                new Order[] {new Order(
                        "1", "1", "1", true,
                        10, 100.0, buyOrder.getTimestamp())},
                orderBook.getBidOrders().toArray());
    }
}