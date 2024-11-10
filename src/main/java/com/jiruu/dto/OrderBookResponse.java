package com.jiruu.dto;

import com.jiruu.model.Order;

public class OrderBookResponse {
    private Order[] bidOrders;
    private Order[] askOrders;

    public Order[] getBidOrders() {
        return bidOrders;
    }

    public void setBidOrders(Order[] bidOrders) {
        this.bidOrders = bidOrders;
    }

    public Order[] getAskOrders() {
        return askOrders;
    }

    public void setAskOrders(Order[] askOrders) {
        this.askOrders = askOrders;
    }
}
