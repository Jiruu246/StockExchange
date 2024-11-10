package com.jiruu.service;

import com.jiruu.model.Match;
import com.jiruu.model.Order;
import com.jiruu.model.OrderBook;

import java.util.ArrayList;

public class MatchingEngine {


    public static Match[] MatchLimitOrder(Order order, OrderBook orderBook){
        assert order.getLimit() > 0;

        int differenceUnits;
        ArrayList<Match> matches = new ArrayList<>();

        do {
            Order otherSide = orderBook.peakOrder(!order.isBuy());

            if (otherSide == null || !CanMatched(order, otherSide)){
                orderBook.addOrder(order);
                break;
            }

            otherSide = orderBook.removeTopOrder(!order.isBuy());

            //Using Mid-price matching rule
            float effectivePrice = (float) ((order.getLimit() + otherSide.getLimit()) / 2);
            differenceUnits = order.getUnit() - otherSide.getUnit();

            matches.add(new Match(
                    order.isBuy()? order.getOrderId() : otherSide.getOrderId(),
                    otherSide.isBuy()? order.getOrderId() : otherSide.getOrderId(),
                    effectivePrice,
                    Math.min(order.getUnit(), otherSide.getUnit())));

            if (differenceUnits < 0) {
                orderBook.addOrder(new Order(
                        otherSide.getOrderId(),
                        otherSide.getProductId(),
                        otherSide.getUserId(),
                        otherSide.isBuy(),
                        -differenceUnits,
                        otherSide.getLimit(),
                        otherSide.getTimestamp() //preserve the timestamp
                ));
                break;
            } else if (differenceUnits > 0) {
                order = new Order(
                        order.getOrderId(),
                        order.getProductId(),
                        order.getUserId(),
                        order.isBuy(),
                        differenceUnits,
                        order.getLimit(),
                        order.getTimestamp() //preserve the timestamp
                );
            }

        } while (differenceUnits != 0);

        return matches.toArray(new Match[0]);
    }

    private static boolean CanMatched(Order order, Order otherSide){
        return order.isBuy() ? order.getLimit() >= otherSide.getLimit() : order.getLimit() <= otherSide.getLimit();
    }

    public static Match[] MatchMarketOrder(Order order, OrderBook orderBook){
        //indicating market order
        assert order.getLimit() == -100;

        int differenceUnits = 0;
        ArrayList<Match> matches = new ArrayList<>();

        do {
            Order otherSide = orderBook.removeTopOrder(!order.isBuy());

            if (otherSide == null){;
                break;
            }

            differenceUnits = order.getUnit() - otherSide.getUnit();
            double effectivePrice = otherSide.getLimit();

            matches.add(new Match(
                    order.isBuy() ? order.getOrderId() : otherSide.getOrderId(),
                    otherSide.isBuy() ? order.getOrderId() : otherSide.getOrderId(),
                    effectivePrice,
                    Math.min(order.getUnit(), otherSide.getUnit())));

            if (differenceUnits < 0) {
                orderBook.addOrder(new Order(
                        otherSide.getOrderId(),
                        otherSide.getProductId(),
                        otherSide.getUserId(),
                        otherSide.isBuy(),
                        -differenceUnits,
                        otherSide.getLimit(),
                        otherSide.getTimestamp() //preserve the timestamp
                ));
                break;
            } else if (differenceUnits > 0) {
                order = new Order(
                        order.getOrderId(),
                        order.getProductId(),
                        order.getUserId(),
                        order.isBuy(),
                        differenceUnits,
                        order.getLimit(),
                        order.getTimestamp() //preserve the timestamp
                );
            }
        } while (differenceUnits != 0);

        return matches.toArray(new Match[0]);
    }
}
