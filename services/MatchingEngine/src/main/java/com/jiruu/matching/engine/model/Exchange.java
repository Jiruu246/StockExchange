package com.jiruu.matching.engine.model;

import java.util.UUID;

public class Exchange {
    private final OrderBook orderBook;
//    private double open;
//    private double close;
//    private double high;
//    private double low;

    public long Timestamp;

    public Exchange() {
        this.orderBook = new OrderBook();
//        this.users = new ArrayList<>();
//        this.priceHistories = new LinkedList<>();
    }

    //This should be responsibility of the Order service
//    public String registerUser() {
//        String userId = UUID.randomUUID().toString();
//        users.add(userId);
//        return userId;
//    }

    public Transaction[] placeLimitOrder(String orderId, boolean isBuy, int unit, double price) {
//        assert users.contains(userId);
        Limit limit = orderBook.getLimit(isBuy, price);
        if (limit == null) {
            limit = orderBook.createNewLimit(isBuy, price);
        }
        Order order = new Order(orderId, isBuy, unit, limit);
        final Transaction[] transactions = MatchingEngine.MatchOrder(order, orderBook);
        if (limit.getVolume() == 0) {
            orderBook.removeLimit(isBuy, price);
        }
        return transactions;
//        if (transactions.length != 0) {
//            updateOHLC(transactions[transactions.length - 1].getEffectivePrice());
//        }
    }

    public Transaction[] placeMarketOrder(String orderId, boolean isBuy, int unit) {
        Order order = new Order(orderId, isBuy, unit, null);
        return MatchingEngine.MatchOrder(order, orderBook);
//        if (transactions.length != 0) {
//            updateOHLC(transactions[transactions.length - 1].getEffectivePrice());
//        }
    }

    public double getMarketPrice(boolean isBuy) {
        return isBuy ? orderBook.getBestAsk().getLimit().getValue() : orderBook.getBestBid().getLimit().getValue();
    }

    public Limit[] getAllLimits(boolean isBuy) {
        return orderBook.getAllLimits(isBuy);
    }

//    public void updateOHLC(double lastPrice) {
//        this.close = lastPrice;
//
//        // haven't received any price yet
//        if (this.open == -1) {
//            this.open = lastPrice;
//            this.high = lastPrice;
//            this.low = lastPrice;
//        }
//
//        high = Math.max(high, lastPrice);
//        low = Math.min(low, lastPrice);
//    }

    // FIXME: Can use subscriber pattern for this
//    public PriceHistory ReportPriceHistory(long timestamp, double lastPrice) {
//        final PriceHistory previousPriceHistory = new PriceHistory(this.Timestamp);
//        //No transaction happened
//        if (open == -1) {
//            previousPriceHistory.Open = lastPrice;
//            previousPriceHistory.High = lastPrice;
//            previousPriceHistory.Low = lastPrice;
//            previousPriceHistory.Close = lastPrice;
//        } else {
//            previousPriceHistory.Open = open;
//            previousPriceHistory.High = high;
//            previousPriceHistory.Low = low;
//            previousPriceHistory.Close = close;
//        }
//        this.Timestamp = timestamp;
//        open = -1;
//        close = -1;
//        high = -1;
//        low = -1;
//
//        return previousPriceHistory;
//    }
}