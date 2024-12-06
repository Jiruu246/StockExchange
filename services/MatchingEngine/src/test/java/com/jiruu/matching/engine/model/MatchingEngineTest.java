package com.jiruu.matching.engine.model;

import com.jiruu.matching.engine.model.Limit;
import com.jiruu.matching.engine.model.Transaction;
import com.jiruu.matching.engine.model.Order;
import com.jiruu.matching.engine.model.OrderBook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    @Test
    void onlyMatchAPortionOfLimitOrder() {
        final OrderBook orderBook = new OrderBook();
        final Limit buyLimit100 = orderBook.createNewLimit(true, 100);
        final Limit buyLimit120 = orderBook.createNewLimit(true, 120);
        final Limit sellLimit110 = orderBook.createNewLimit(false, 110);
        final Limit sellLimit120 = orderBook.createNewLimit(false, 120);

        final Order buyOrder = new Order("1", true, 10, buyLimit100);
        final Order sellOrder = new Order("2", false, 10, sellLimit110);
        final Order sellOrder2 = new Order("4", false, 10, sellLimit120);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);
        orderBook.addOrder(sellOrder2);

        final Order buyOrder2 = new Order("3", true, 30, buyLimit120);
        final Transaction[] transactions = MatchingEngine.MatchOrder(buyOrder2, orderBook);

        assertArrayEquals(
                new Transaction[] {
                        new Transaction("3", "2", 115.0, 10),
                        new Transaction("3", "4", 120.0, 10)},
                transactions);
        assertNotNull(orderBook.getOrder("1"));
        assertNotNull(orderBook.getOrder("3"));
        assertEquals(10, orderBook.getOrder("1").getUnit());
        assertEquals(10, orderBook.getOrder("3").getUnit());
        assertNull(orderBook.getOrder("2"));
        assertNull(orderBook.getOrder("4"));
    }

    @Test
    void noLimitOrderCanBeMatched() {
        final OrderBook orderBook = new OrderBook();
        final Limit buyLimit100 = orderBook.createNewLimit(true, 100);
        final Limit sellLimit110 = orderBook.createNewLimit(false, 110);

        final Order buyOrder = new Order("1", true, 10, buyLimit100);
        final Order sellOrder = new Order("2", false, 10, sellLimit110);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Limit buyLimit90 = orderBook.createNewLimit(true, 90);
        final Order buyOrder2 = new Order("3", true, 10, buyLimit90);
        final Transaction[] transactions = MatchingEngine.MatchOrder(buyOrder2, orderBook);

        assertEquals(0, transactions.length);
        assertNotNull(orderBook.getOrder("1"));
        assertNotNull(orderBook.getOrder("2"));
        assertNotNull(orderBook.getOrder("3"));
    }

    @Test
    void matchEntireLimitOrder() {
        final OrderBook orderBook = new OrderBook();
        final Limit buyLimit100 = orderBook.createNewLimit(true, 100);
        final Limit sellLimit110 = orderBook.createNewLimit(false, 110);

        final Order buyOrder = new Order("1", true, 10, buyLimit100);
        final Order sellOrder = new Order("2", false, 10, sellLimit110);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Limit buyLimit110 = orderBook.createNewLimit(true, 110);
        final Order buyOrder2 = new Order("3", true, 10, buyLimit110);
        final Transaction[] transactions = MatchingEngine.MatchOrder(buyOrder2, orderBook);

        assertArrayEquals(
                new Transaction[] {new Transaction("3", "2", 110.0, 10)},
                transactions);
        assertNotNull(orderBook.getOrder("1"));
        assertNull(orderBook.getOrder("2"));
        assertNull(orderBook.getOrder("3"));
    }

    @Test
    void matchEntireMarketOrder() {
        final OrderBook orderBook = new OrderBook();
        final Limit buyLimit100 = orderBook.createNewLimit(true, 100);
        final Limit sellLimit110 = orderBook.createNewLimit(false, 110);

        final Order buyOrder = new Order("1", true, 10, buyLimit100);
        final Order sellOrder = new Order("2", false, 10, sellLimit110);

        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Order marketBuyOrder = new Order("3", true, 10, null);
        Transaction[] transactions = MatchingEngine.MatchOrder(marketBuyOrder, orderBook);

        assertArrayEquals(
                new Transaction[] {new Transaction("3", "2", 110.0, 10)},
                transactions);

        assertNotNull(orderBook.getOrder("1"));
        assertNull(orderBook.getOrder("2"));
        assertNull(orderBook.getOrder("3"));
    }

    @Test
    void partialMatchMarketOrder() {
        final OrderBook orderBook = new OrderBook();
        final Limit buyLimit100 = orderBook.createNewLimit(true, 100);
        final Limit sellLimit110 = orderBook.createNewLimit(false, 110);
        final Limit sellLimit120 = orderBook.createNewLimit(false, 120);

        final Order buyOrder = new Order("1", true, 10, buyLimit100);
        final Order sellOrder = new Order("2", false, 10, sellLimit110);
        final Order sellOrder2 = new Order("4", false, 10, sellLimit120);


        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);
        orderBook.addOrder(sellOrder2);

        final Order limitBuyOrder = new Order("3", true, 20, null);
        final Transaction[] transactions = MatchingEngine.MatchOrder(limitBuyOrder, orderBook);

        assertArrayEquals(
                new Transaction[] {
                        new Transaction("3", "2", 110.0, 10),
                        new Transaction("3", "4", 120.0, 10)},
                transactions);
        assertNotNull(orderBook.getOrder("1"));
        assertEquals(10, orderBook.getOrder("1").getUnit());
        assertNull(orderBook.getOrder("3"));
        assertNull(orderBook.getOrder("2"));
        assertNull(orderBook.getOrder("4"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            final Order limitBuyOrder2 = new Order("5", true, 20, null);
            MatchingEngine.MatchOrder(limitBuyOrder2, orderBook);
        });

        assertEquals("Not enough liquidity", exception.getMessage());
    }

    @Test
    void partialMatchMarketOrderOtherSide(){
        final OrderBook orderBook = new OrderBook();
        final Limit buyLimit100 = orderBook.createNewLimit(true, 100);
        final Limit sellLimit110 = orderBook.createNewLimit(false, 110);

        final Order buyOrder = new Order("1", true, 20, buyLimit100);
        final Order sellOrder = new Order("2", false, 10, sellLimit110);


        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        final Order marketSell = new Order("3", false, 10, null);
        final Transaction[] transactions = MatchingEngine.MatchOrder(marketSell, orderBook);

        assertArrayEquals(
                new Transaction[] {new Transaction("1", "3", 100.0, 10)},
                transactions);
        assertNotNull(orderBook.getOrder("1"));
        assertEquals(10, orderBook.getOrder("1").getUnit());
        assertNull(orderBook.getOrder("3"));
        assertNotNull(orderBook.getOrder("2"));
    }
}