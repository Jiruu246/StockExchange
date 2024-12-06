package com.jiruu.matching.engine.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class LimitTest {

    @Test
    void compareTo() {
        final Limit limit1 = new Limit(10);
        final Limit limit2 = new Limit(20);
        assertEquals(-1, limit1.compareTo(limit2));
    }

    @Test
    void testToString() {
        final Limit limit = new Limit(10);
        assertEquals("Limit{limit=10.0, volume= 0}", limit.toString());
    }

    @Test
    void getValue() {
        final Limit limit = new Limit(10);
        assertEquals(10, limit.getValue());
    }

    @Test
    void getVolume() {
        final Limit limit = new Limit(10);
        final Order order1 = new Order("1", true, 10, limit);
        final Order order2 = new Order("2", true, 10, limit);
        final Order order3 = new Order("3", true, 10, limit);

        limit.addOrder(order1);
        limit.addOrder(order2);
        limit.addOrder(order3);
        assertEquals(30, limit.getVolume());
    }

    @Test
    void addOrder() {
        final Limit limit = new Limit(10);
        final Order order1 = new Order("1", true, 10, limit);
        final Order order2 = new Order("2", true, 10, limit);
        final Order order3 = new Order("3", true, 10, limit);

        limit.addOrder(order1);
        limit.addOrder(order2);
        limit.addOrder(order3);
        assertEquals(30, limit.getVolume());
    }

    @Test
    void removeOrder() {
        final Limit limit = new Limit(10);
        final Order order1 = new Order("1", true, 10, limit);
        final Order order2 = new Order("2", true, 10, limit);
        final Order order3 = new Order("3", true, 10, limit);

        limit.addOrder(order1);
        limit.addOrder(order2);
        limit.addOrder(order3);
        assertEquals(30, limit.getVolume());

        limit.removeOrder("1");
        assertEquals(20, limit.getVolume());
        assertNull(limit.getOrders("1"));
        assertFalse(limit.removeOrder("4"));
    }

    @Test
    void updateOrder() {
        final Limit limit = new Limit(10);
        final Order order1 = new Order("1", true, 10, limit);
        final Order order2 = new Order("2", true, 10, limit);
        final Order order3 = new Order("3", true, 10, limit);

        limit.addOrder(order1);
        limit.addOrder(order2);
        limit.addOrder(order3);
        assertEquals(30, limit.getVolume());

        limit.updateOrder("1", 5);
        assertEquals(5, order1.getUnit());
        assertEquals(25, limit.getVolume());

        limit.updateOrder("1", 15);
        assertEquals(15, order1.getUnit());
        assertEquals(35, limit.getVolume());

        try {
            limit.updateOrder("4", 5);
        } catch (AssertionError e) {
            assertEquals("Order not found in the limit", e.getMessage());
        }

        try {
            limit.updateOrder("1", -5);
        } catch (AssertionError e) {
            assertEquals("Quantity should be greater than 0", e.getMessage());
        }

        try {
            limit.updateOrder("1", 0);
        } catch (AssertionError e) {
            assertEquals("Quantity should be greater than 0", e.getMessage());
        }

    }
}