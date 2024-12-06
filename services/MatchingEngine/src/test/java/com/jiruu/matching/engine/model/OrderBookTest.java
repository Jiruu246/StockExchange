package com.jiruu.matching.engine.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {

    @Test
    void addOrder() {
        final OrderBook ob = new OrderBook();
        final Limit bidLimit = ob.createNewLimit(true, 10);
        final Limit askLimit = ob.createNewLimit(false, 10);

        final Order order1 = new Order(UUID.randomUUID().toString(),true, 10, bidLimit);
        final Order order2 = new Order(UUID.randomUUID().toString(),true, 10, bidLimit);
        final Order order3 = new Order(UUID.randomUUID().toString(),false, 10, askLimit);
        assertTrue(ob.addOrder(order1));
        assertTrue(ob.addOrder(order2));
        assertTrue(ob.addOrder(order3));
    }

    @Test
    void removeOrder() {
        final OrderBook ob = new OrderBook();
        final Limit bidLimit = ob.createNewLimit(true, 10);
        final Limit askLimit = ob.createNewLimit(false, 10);

        final Order order1 = new Order(UUID.randomUUID().toString(), true, 10, bidLimit);
        final Order order2 = new Order(UUID.randomUUID().toString(), true, 10, bidLimit);
        final Order order3 = new Order(UUID.randomUUID().toString(), false, 10, askLimit);
        assertTrue(ob.addOrder(order1));
        assertTrue(ob.addOrder(order2));
        assertTrue(ob.addOrder(order3));

        assertTrue(ob.removeOrder(order1.getOrderId()));
        assertTrue(ob.removeOrder(order2.getOrderId()));
        assertFalse(ob.removeOrder("4"));
    }

    @Test
    void fillOrder() {
        final OrderBook ob = new OrderBook();
        final Limit bidLimit = ob.createNewLimit(true, 10);
        final Limit askLimit = ob.createNewLimit(false, 10);

        final Order order1 = new Order(UUID.randomUUID().toString(), true, 10, bidLimit);
        final Order order2 = new Order(UUID.randomUUID().toString(), true, 10, bidLimit);
        final Order order3 = new Order(UUID.randomUUID().toString(), false, 10, askLimit);
        assertTrue(ob.addOrder(order1));
        assertTrue(ob.addOrder(order2));
        assertTrue(ob.addOrder(order3));

        // Order book should be able to delete order when it is filled and the volume is updated correctly
        ob.fillOrder(order1.getOrderId(), 5);
        assertEquals(5, order1.getUnit());
        assertEquals(15, bidLimit.getVolume());
        ob.fillOrder(order2.getOrderId(), 10);
        assertNull(ob.getOrder(order2.getOrderId()));
        assertEquals(5, bidLimit.getVolume());
        assertEquals(10, askLimit.getVolume());

        // Order book should be able to delete Limit when no orders are left
        ob.fillOrder(order1.getOrderId(), 5);
        assertNull(ob.getOrder(order1.getOrderId()));
        assertNull(ob.getLimit(true, 10));
    }

    @Test
    void bestBidAsk() {
        final OrderBook ob = new OrderBook();
        final Limit bidLimit = ob.createNewLimit(true, 10);
        final Limit bestBid = ob.createNewLimit(true, 20);
        final Limit bestAsk = ob.createNewLimit(false, 10);
        final Limit askLimit = ob.createNewLimit(false, 20);

        final Order order1 = new Order(UUID.randomUUID().toString(), true, 10, bidLimit);
        final Order order2 = new Order(UUID.randomUUID().toString(), true, 10, bestBid);
        final Order order3 = new Order(UUID.randomUUID().toString(), false, 10, askLimit);
        final Order order4 = new Order(UUID.randomUUID().toString(), false, 10, bestAsk);
        assertTrue(ob.addOrder(order1));
        assertTrue(ob.addOrder(order2));
        assertTrue(ob.addOrder(order3));
        assertTrue(ob.addOrder(order4));

        assertEquals(order2, ob.getBestBid());
        assertEquals(order4, ob.getBestAsk());
    }
}