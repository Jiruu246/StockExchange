package com.jiruu.dto;

public class OrderBookResponse {
    private LimitResponse[] bidOrders;
    private LimitResponse[] askOrders;

    public OrderBookResponse(LimitResponse[] bidOrders, LimitResponse[] askOrders) {
        this.bidOrders = bidOrders;
        this.askOrders = askOrders;
    }

    public LimitResponse[] getBidOrders() {
        return bidOrders;
    }

    public void setBidOrders(LimitResponse[] bidOrders) {
        this.bidOrders = bidOrders;
    }

    public LimitResponse[] getAskOrders() {
        return askOrders;
    }

    public void setAskOrders(LimitResponse[] askOrders) {
        this.askOrders = askOrders;
    }
}
