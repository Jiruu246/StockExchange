package com.jiruu.orderservice.dto;

public class LimitDTO {
    public double price;
    public int quantity;

    public LimitDTO(double price, int quantity) {
        this.price = price;
        this.quantity = quantity;
    }
}
