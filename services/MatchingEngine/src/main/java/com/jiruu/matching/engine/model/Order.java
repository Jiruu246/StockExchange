package com.jiruu.matching.engine.model;

public class Order {
    private final String orderId;
    private final boolean isBuy;
    //FIXME: Bug prone as others can change the value without update the Limit total volume
    private int unit;
    private final Limit limit;
    private final long timestamp;

    public Order(String orderId, boolean isBuy, int quantity, Limit limit) {
        this(orderId, isBuy, quantity, limit, System.currentTimeMillis());
    }

    public Order(String orderId, boolean isBuy, int quantity, Limit limit, long timestamp) {
        this.orderId = orderId;
        this.isBuy = isBuy;
        this.unit = quantity;
        this.limit = limit;
        this.timestamp = timestamp;
    }

    public String getOrderId() {
        return orderId;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public Limit getLimit() {
        return limit;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', isBuy=%b, quantity=%d, limit=%.2f, timestamp=%d}",
                orderId, isBuy, unit, limit.getValue(), timestamp);
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
