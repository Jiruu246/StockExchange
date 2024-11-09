package com.jiruu.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class OrderBookTest {

    @Test
    void bidOrdersOnlyContainBuyOrders() {
        final Order buyOrder1 = new Order("1", "1", "1", true, 10, 100.0);
        final Order buyOrder2 = new Order("2", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("3", "1", "1", false, 10, 100.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(sellOrder);

        assertArrayEquals(new Order[] {buyOrder1, buyOrder2}, orderBook.getBidOrders().toArray());
    }

    @Test
    void askOrdersOnlyContainSellOrders() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder1 = new Order("2", "1", "1", false, 10, 100.0);
        final Order sellOrder2 = new Order("3", "1", "1", false, 10, 100.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);

        assertArrayEquals(new Order[] {sellOrder1, sellOrder2}, orderBook.getAskOrders().toArray());
    }

    @Test
    void addOrder() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 100.0);
        final Order buyOrder2 = new Order("3", "1", "1", true, 10, 100.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 100.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(sellOrder);
        orderBook.addOrder(sellOrder2);

        assertArrayEquals(new Order[] {buyOrder, buyOrder2}, orderBook.getBidOrders().toArray());
        assertArrayEquals(new Order[] {sellOrder, sellOrder2}, orderBook.getAskOrders().toArray());
    }

    @Test
    void orderBookCanSortOrder() {
        final Order buyOrder1 = new Order("1", "1", "1", true, 10, 100.0);
        final Order buyOrder2 = new Order("2", "1", "1", true, 10, 200.0);
        final Order buyOrder3 = new Order("3", "1", "1", true, 10, 150.0);
        final Order sellOrder1 = new Order("3", "1", "1", false, 10, 100.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 200.0);
        final Order sellOrder3 = new Order("5", "1", "1", false, 10, 150.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(buyOrder3);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);
        orderBook.addOrder(sellOrder3);

        // Bid orders should be sorted by price in descending order
        assertArrayEquals(new Order[] {buyOrder2, buyOrder3, buyOrder1}, orderBook.getBidOrders().toArray());
        // Ask orders should be sorted by price in ascending order
        assertArrayEquals(new Order[] {sellOrder1, sellOrder3, sellOrder2}, orderBook.getAskOrders().toArray());
    }

    @Test
    void ordersListAreImmutable() {
        final Order buyOrder = new Order("1", "1", "1", true, 10, 100.0);
        final Order sellOrder = new Order("2", "1", "1", false, 10, 100.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder);
        orderBook.addOrder(sellOrder);

        assertThrows(UnsupportedOperationException.class, () -> orderBook.getBidOrders().add(buyOrder));
        assertThrows(UnsupportedOperationException.class, () -> orderBook.getAskOrders().add(sellOrder));
    }

    @Test
    void peakOrderTest() {
        final Order buyOrder1 = new Order("1", "1", "1", true, 10, 100.0);
        final Order buyOrder2 = new Order("2", "1", "1", true, 10, 200.0);
        final Order sellOrder1 = new Order("3", "1", "1", false, 10, 100.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 200.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);

        assertEquals(buyOrder2, orderBook.peakOrder(true));
        assertEquals(sellOrder1, orderBook.peakOrder(false));
        assertArrayEquals(new Order[] {buyOrder2, buyOrder1}, orderBook.getBidOrders().toArray());
        assertArrayEquals(new Order[] {sellOrder1, sellOrder2}, orderBook.getAskOrders().toArray());
    }

    @Test
    void removeTopOrderTest() {
        final Order buyOrder1 = new Order("1", "1", "1", true, 10, 100.0);
        final Order buyOrder2 = new Order("2", "1", "1", true, 10, 200.0);
        final Order sellOrder1 = new Order("3", "1", "1", false, 10, 100.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 200.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);

        assertEquals(buyOrder2, orderBook.removeTopOrder(true));
        assertEquals(sellOrder1, orderBook.removeTopOrder(false));
        assertArrayEquals(new Order[] {buyOrder1}, orderBook.getBidOrders().toArray());
        assertArrayEquals(new Order[] {sellOrder2}, orderBook.getAskOrders().toArray());
    }

    @Test
    void removeOrderTest() {
        final Order buyOrder1 = new Order("1", "1", "1", true, 10, 100.0);
        final Order buyOrder2 = new Order("2", "1", "1", true, 10, 200.0);
        final Order sellOrder1 = new Order("3", "1", "1", false, 10, 100.0);
        final Order sellOrder2 = new Order("4", "1", "1", false, 10, 200.0);

        final OrderBook orderBook = new OrderBook();
        orderBook.addOrder(buyOrder1);
        orderBook.addOrder(buyOrder2);
        orderBook.addOrder(sellOrder1);
        orderBook.addOrder(sellOrder2);

        assertTrue(orderBook.removeOrder(buyOrder1));
        assertTrue(orderBook.removeOrder(sellOrder2));
        assertArrayEquals(new Order[] {buyOrder2}, orderBook.getBidOrders().toArray());
        assertArrayEquals(new Order[] {sellOrder1}, orderBook.getAskOrders().toArray());

        // Try to remove an order that does not exist
        final  Order buyOrder3 = new Order("5", "1", "1", true, 10, 300.0);
        assertFalse(orderBook.removeOrder(buyOrder3));
        final Order sellOrder3 = new Order("6", "1", "1", false, 10, 300.0);
        assertFalse(orderBook.removeOrder(sellOrder3));
    }
}