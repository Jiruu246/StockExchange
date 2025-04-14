package com.jiruu.orderservice.dto;

public class OrderBookDTO {
    public LimitDTO[] bidOrders;
    public LimitDTO[] askOrders;

    public OrderBookDTO(LimitDTO[] bidOrders, LimitDTO[] askOrders) {
        this.bidOrders = bidOrders;
        this.askOrders = askOrders;
    }
}
