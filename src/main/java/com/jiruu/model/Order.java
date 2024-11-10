package com.jiruu.model;

public class Order {
    private final String orderId;
    private final String productId;
    private final String userId;
    private final boolean isBuy;
    private final int unit;
    private final double limit;
    private final long timestamp;

    public Order(String orderId, String productId, String userId, boolean isBuy, int quantity, double limit) {
        this.orderId = orderId;
        this.productId = productId;
        this.userId = userId;
        this.isBuy = isBuy;
        this.unit = quantity;
        this.limit = limit;
        this.timestamp = System.currentTimeMillis();
    }

    public Order(String orderId, String productId, String userId, boolean isBuy, int quantity, double limit, long timestamp) {
        this.orderId = orderId;
        this.productId = productId;
        this.userId = userId;
        this.isBuy = isBuy;
        this.unit = quantity;
        this.limit = limit;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public int getUnit() {
        return unit;
    }

    public double getLimit() {
        return limit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', productId='%s', userId='%s', isBuy=%b, quantity=%d, limit=%.2f, timestamp=%d}",
                orderId, productId, userId, isBuy, unit, limit, timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
}
