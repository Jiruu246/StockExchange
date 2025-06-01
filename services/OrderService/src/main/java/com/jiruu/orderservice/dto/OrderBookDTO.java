package com.jiruu.orderservice.dto;

public record OrderBookDTO (LimitDTO[] bidOrders, LimitDTO[] askOrders) {}
