package com.jiruu.service;

import com.jiruu.model.Match;
import com.jiruu.model.Order;
import com.jiruu.model.OrderBook;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class Exchange {
    private final OrderBook orderBook;
    private final MarketMaker marketMaker;
    private final ArrayList<String> users;

    public Exchange() {
        this.orderBook = new OrderBook();
        this.marketMaker = new MarketMaker(20_000, UUID.randomUUID().toString());
        this.users = new ArrayList<>();
        marketMaking(orderBook);
    }

    private void marketMaking(OrderBook orderBook) {
        if (orderBook.getBidOrders().isEmpty() && orderBook.getAskOrders().isEmpty()) {
            //Assume the initial mid-price is 100, create liquidity for the exchange
            orderBook.addOrder(marketMaker.generateQuote(true, 100));
            orderBook.addOrder(marketMaker.generateQuote(false, 100));
        } else if (orderBook.getBidOrders().isEmpty() && !orderBook.getAskOrders().isEmpty()) {
            orderBook.addOrder(marketMaker.generateQuote(true, orderBook.peakOrder(false).getLimit()));
        } else if (orderBook.getAskOrders().isEmpty()) {
            orderBook.addOrder(marketMaker.generateQuote(false, orderBook.peakOrder(true).getLimit()));
        }
    }

    public String registerUser() {
        String userId = UUID.randomUUID().toString();
        users.add(userId);
        return userId;
    }

    public void placeLimitOrder(String userId, boolean isBuy, int unit, double limit) {
        Order order = new Order(UUID.randomUUID().toString(), "1", userId, isBuy, unit, limit);
        Match[] matches = MatchingEngine.MatchLimitOrder(order, orderBook);
        marketMaking(orderBook);
    }

    public void placeMarketOrder(String userId, boolean isBuy, int unit) {
        Order order = new Order(UUID.randomUUID().toString(), "1", userId, isBuy, unit, -100);
        Match[] matches = MatchingEngine.MatchMarketOrder(order, orderBook);
        int remainingUnits = unit - Arrays.stream(matches).mapToInt(Match::getFilledUnits).sum();
        assert remainingUnits >= 0;
        //ensure market order always get filled by asking the market maker to generate new quote
        if (remainingUnits > 0) {
            marketMaker.generateQuote(!isBuy, orderBook.peakOrder(isBuy).getLimit(), remainingUnits);
        }
        marketMaking(orderBook);
    }

    public double getMarketPrice(boolean isBuy) {
        return orderBook.peakOrder(isBuy).getLimit();
    }

    public Order[] getBookOrders(boolean isBuy) {
        return isBuy? orderBook.getBidOrders().toArray(new Order[0]): orderBook.getAskOrders().toArray(new Order[0]);
    }

}
