package com.jiruu.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Collection;
import java.util.stream.Collectors;

public class OrderBook {
    private static class BuyOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            assert o1.isBuy() && o2.isBuy() : "Both orders must be buy orders";

            final int result = Double.compare(o2.getLimit(), o1.getLimit());
            if (result == 0) {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
            return result;
        }
    }

    private static class SellOrderComparator implements Comparator<Order> {
        @Override
        public int compare(Order o1, Order o2) {
            assert !o1.isBuy() && !o2.isBuy() : "Both orders must be sell orders";

            final int result = Double.compare(o1.getLimit(), o2.getLimit());
            if (result == 0) {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
            return result;
        }
    }

    private final PriorityQueue<Order> bidOrders = new PriorityQueue<>(new BuyOrderComparator());
    private final PriorityQueue<Order> askOrders = new PriorityQueue<>(new SellOrderComparator());

    public Collection<Order> getBidOrders() {
        return bidOrders.stream()
                .sorted(new BuyOrderComparator())
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public Collection<Order> getAskOrders() {
        return askOrders.stream()
                .sorted(new SellOrderComparator())
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public void addOrder(Order order) {
        if (order.isBuy()) {
            bidOrders.add(order);
        } else {
            askOrders.add(order);
        }
    }

    public boolean removeOrder(Order order) {
        final PriorityQueue<Order> orderList = order.isBuy() ? bidOrders : askOrders;
        return orderList.remove(order);
    }

    public Order peakOrder(boolean isBuy) {
        return isBuy ? bidOrders.peek() : askOrders.peek();
    }

    public Order removeTopOrder(boolean isBuy) {
        return isBuy ? bidOrders.poll() : askOrders.poll();
    }
}
