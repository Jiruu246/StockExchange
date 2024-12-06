package com.jiruu.orderservice.dto;

public class OrderDTO {
    private String orderId;
    private boolean isBuy;
    private int unit;
    private double limit;

    public OrderDTO(String orderId, boolean isBuy, int units, double price) {
        this.orderId = orderId;
        this.isBuy = isBuy;
        this.unit = units;
        this.limit = price;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }
}
