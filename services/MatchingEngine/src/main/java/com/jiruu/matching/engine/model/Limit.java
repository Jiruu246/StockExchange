package com.jiruu.matching.engine.model;

import com.jiruu.datastructure.LinkedList;

public class Limit implements Comparable<Limit> {
  private final double limit;
  private int volume;
  private final LinkedList<Order> orders;

  public Limit(double limit) {
    this.limit = limit;
    this.volume = 0;
    this.orders = new LinkedList<>();
  }

  @Override
  public int compareTo(Limit other) {
    return Double.compare(this.limit, other.limit);
  }

  @Override
  public String toString() {
    return "Limit{limit=" + limit + ", volume= " + volume + "}";
  }

  public double getValue() {
    return limit;
  }

  public int getVolume() {
    return volume;
  }

  public boolean addOrder(Order order) {
      assert order.getUnit() > 0 : "Order quantity should be greater than 0";
    if (order.getLimit() != this && orders.get(order.getOrderId()) != null) {
      return false;
    }

    volume += order.getUnit();
    return orders.add(order, order.getOrderId());
  }

  public boolean removeOrder(String orderId) {
    Order order = orders.remove(orderId);
    if (order == null) {
      return false;
    }
    volume -= order.getUnit();
    return true;
  }

  //    public void fillOrder(String key, int quantity) {
  //        Order order = orders.get(key);
  //        if (order == null) {
  //            return;
  //        }
  //        assert order.getUnit() >= quantity;
  //        order.setUnit(order.getUnit() - quantity);
  //        volume -= quantity;
  //
  //        if (order.getUnit() == 0) {
  //            orders.remove(key);
  //        }
  //    }

  /**
   * Update the order quantity
   *
   * @param key
   * @param newQuantity
   */
  public void updateOrder(String key, int newQuantity) {
    assert newQuantity > 0 : "Quantity should be greater than 0";
    Order order = orders.get(key);
    assert order != null : "Order not found in the limit";
    final int adjustment = newQuantity - order.getUnit();
    order.setUnit(newQuantity);
    volume += adjustment;
  }

  public Order getOrders(String key) {
    return orders.get(key);
  }

  public Order getBestOrder() {
    return orders.getHeadData();
  }
}
