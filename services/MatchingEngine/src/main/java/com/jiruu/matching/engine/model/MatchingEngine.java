package com.jiruu.matching.engine.model;

import com.jiruu.matching.engine.model.Order;
import com.jiruu.matching.engine.model.OrderBook;
import com.jiruu.matching.engine.model.Transaction;

import java.util.ArrayList;

public class MatchingEngine {


    public static Transaction[] MatchOrder(Order order, OrderBook orderBook){
        ArrayList<Transaction> transactions = new ArrayList<>();

        while (order.getUnit() > 0) {
            final Order otherSide = order.isBuy() ? orderBook.getBestAsk() : orderBook.getBestBid();
            if (otherSide == null || !CanMatched(order, otherSide)) {
                if (order.getLimit() == null) {
                    // FIXME: Do more research to see what should be done in this case
                    throw new RuntimeException("Not enough liquidity");
                }

                orderBook.addOrder(order);
                break;
            }

            final int filledUnits = Math.min(order.getUnit(), otherSide.getUnit());
            final double effectivePrice = calculateEffectivePrice(order, otherSide);
            order.setUnit(order.getUnit() - filledUnits);
            orderBook.fillOrder(otherSide.getOrderId(), filledUnits);
            transactions.add(new Transaction(
                    order.isBuy() ? order.getOrderId() : otherSide.getOrderId(),
                    otherSide.isBuy() ? order.getOrderId() : otherSide.getOrderId(),
                    effectivePrice,
                    filledUnits));

        }
        return transactions.toArray(new Transaction[0]);
    }

    private static boolean CanMatched(Order order, Order otherSide){
        // if either side is market order, it can be matched
        if (order.getLimit() == null ^ otherSide.getLimit() == null) {
            return true;
        }
        return order.isBuy() ?
                order.getLimit().compareTo(otherSide.getLimit()) >= 0 :
                order.getLimit().compareTo(otherSide.getLimit()) <= 0;
    }

    private static double calculateEffectivePrice(Order order, Order otherSide){
        if (order.getLimit() == null) {
            return otherSide.getLimit().getValue();
        }
        // using the average price as the effective price
        return (order.getLimit().getValue() + otherSide.getLimit().getValue()) / 2;
    }
}
