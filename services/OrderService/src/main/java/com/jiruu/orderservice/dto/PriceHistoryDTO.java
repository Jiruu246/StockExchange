package com.jiruu.orderservice.dto;

public class PriceHistoryDTO {
    public final long Timestamp;
    public double Open;
    public double High;
    public double Low;
    public double Close;

    public PriceHistoryDTO(long timestamp, double open, double high, double low, double close) {
        Timestamp = timestamp;
        Open = open;
        High = high;
        Low = low;
        Close = close;
    }
}
