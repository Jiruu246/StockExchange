package com.jiruu.service;

import com.jiruu.model.Order;

public class MarketMaker {
    private static final float SIGMA = 0.15f; // Market Volatility
    private static final float GAMMA = 0.05f; // Risk Aversion
    private static final float K = 0.5f; // Liquidity Parameter

    private final String marketMakerId;

    private int inventory;

    public MarketMaker(int inventory, String marketMakerId) {
        this.inventory = inventory;
        this.marketMakerId = marketMakerId;
    }

    private double calculateReservationPrice(double midPrice) {
        return midPrice - this.inventory * GAMMA * SIGMA * SIGMA;
    }

    private double calculateSpread() {
        return (float) ((2/GAMMA) * Math.log(1 + GAMMA/K));
    }

    private double calculateQuotePrice(boolean isBid, double midPrice) {
        double reservationPrice = calculateReservationPrice(midPrice);
        double spread = calculateSpread()/2;

        return isBid ? reservationPrice - spread : reservationPrice + spread;
    }

    public Order generateQuote(boolean isBid, double midPrice, int volume) {
        double quotePrice = calculateQuotePrice(isBid, midPrice);
        inventory += isBid ? volume : -volume;
        return new Order(
                String.valueOf(System.currentTimeMillis()),
                "1",
                marketMakerId,
                isBid,
                volume,
                quotePrice
        );
    }

    public Order generateQuote(boolean isBid, double midPrice) {
        int orderSize = 10;
        return generateQuote(isBid, midPrice, orderSize);
    }

}
