package com.jiruu.dto;

public class OrderRequest {
    private String userId;
    private boolean isBuy;
    private int unit;
    private double limit;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean isBuy) {
        this.isBuy = isBuy;
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
