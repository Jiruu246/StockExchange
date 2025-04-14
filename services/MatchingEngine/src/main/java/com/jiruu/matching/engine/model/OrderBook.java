package com.jiruu.matching.engine.model;

import com.jiruu.datastructure.RedBlackTree;

import java.util.HashMap;
import java.util.Map;

public class OrderBook {
    private final RedBlackTree<Limit> bidLimits;
    private final RedBlackTree<Limit> askLimits;

    private final Map<String, Order> ordersMap;
    private final Map<String, Limit> askMap;
    private final Map<String, Limit> bidMap;

    public OrderBook(){
        bidLimits = new RedBlackTree<>(false);
        askLimits = new RedBlackTree<>(true);
        ordersMap = new HashMap<>();
        askMap = new HashMap<>();
        bidMap = new HashMap<>();
    }

    public Order getOrder(String orderId) {
        return ordersMap.get(orderId);
    }

    public Limit getLimit(boolean isBuy, double price) {
//        final double RoundedPrice = roundPrice(price);
        return isBuy ? bidMap.get(String.valueOf(price)) : askMap.get(String.valueOf(price));
    }

    public Limit createNewLimit(boolean isBuy, double price) {
//        final double RoundedPrice = roundPrice(price);
        Limit limit = new Limit(price);
        if (isBuy) {
            assert !bidMap.containsKey(String.valueOf(price));
            bidMap.put(String.valueOf(price), limit);
            bidLimits.insert(String.valueOf(price), limit);
        } else {
            assert !askMap.containsKey(String.valueOf(price));
            askMap.put(String.valueOf(price), limit);
            askLimits.insert(String.valueOf(price), limit);
        }
        return limit;
    }

    public boolean removeLimit(boolean isBuy, double price) {
//        final double RoundedPrice = roundPrice(price);
        final Map<String, Limit> limitsMap = isBuy ? bidMap : askMap;
        if (limitsMap.remove(String.valueOf(price)) == null) {
            return false;
        }
        final RedBlackTree<Limit> limits = isBuy ? bidLimits : askLimits;
        return limits.delete(String.valueOf(price));
    }

    public Order getBestBid() {
        return bidLimits.isEmpty() ? null : bidLimits.getSignificantData().getBestOrder();
    }

    public Order getBestAsk() {
        return askLimits.isEmpty() ? null : askLimits.getSignificantData().getBestOrder();
    }

    public boolean addOrder(Order order) {
        if (order.getLimit() == null || order.getUnit() <= 0) {
            throw new IllegalArgumentException("Invalid limit price or quantity");
        }

        ordersMap.put(order.getOrderId(), order);
        final Map<String, Limit> limitsMap = order.isBuy() ? bidMap : askMap;
        assert limitsMap.containsKey(String.valueOf(order.getLimit().getValue())) : "Limit not found";

        return order.getLimit().addOrder(order);
    }

    public boolean removeOrder(String orderId) {
        Order order = ordersMap.get(orderId);
        if (order == null) {
            return false;
        }

        Limit limit = order.getLimit();

        if (!limit.removeOrder(orderId)) {
            return false;
        }

        ordersMap.remove(orderId);

        if (limit.getVolume() != 0) {
            return true;
        }
        return removeLimit(order.isBuy(), limit.getValue());
    }

    public void fillOrder(String orderId, int quantity) {
        Order order = ordersMap.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not exist in the book");
        }
        assert order.getUnit() >= quantity : "Cannot fill more than order quantity";

        final int newQuantity = order.getUnit() - quantity;
        Limit limit = order.getLimit();
        if (newQuantity == 0) {
            if (!removeOrder(orderId)) {
                throw new IllegalArgumentException("failed to remove order");
            }
        } else {
            limit.updateOrder(orderId, newQuantity);
        }
    }

//    private double roundPrice(double price) {
//        //FIXME: Not sure if this is the best pattern to handle decimal in this scenario
//        // This is used to reduce the number of Limit created per price
//        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).doubleValue();
//    }

    public Limit[] getAllLimits(boolean isBuy) {
        RedBlackTree<Limit> limits = isBuy ? bidLimits : askLimits;
        return limits.inorderTraversal().toArray(new Limit[0]);
    }
}
