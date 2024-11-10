package com.jiruu;

import com.jiruu.dto.OrderBookResponse;
import com.jiruu.dto.OrderRequest;
import com.jiruu.model.Order;
import com.jiruu.service.Exchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {

    private final Exchange exchange = new Exchange();

    @GetMapping("/register")
    public ResponseEntity<String> register() {
        return ResponseEntity.ok(exchange.registerUser());
    }

    @GetMapping("/market-price")
    public ResponseEntity<Map<String, Double>> getMarketPrice() {
        final double bid = exchange.getMarketPrice(true);
        final double ask = exchange.getMarketPrice(false);
        final Map<String, Double> response = new HashMap<>();
        response.put("bid", bid);
        response.put("ask", ask);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/limit-order")
    public ResponseEntity<Void> placeLimitOrder(@RequestBody OrderRequest orderRequest) {
         exchange.placeLimitOrder(
                 orderRequest.getUserId(),
                 orderRequest.isBuy(),
                 orderRequest.getUnit(),
                 orderRequest.getLimit());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/market-order")
    public ResponseEntity<Void> placeMarketOrder(@RequestBody OrderRequest orderRequest) {
        exchange.placeMarketOrder(
                orderRequest.getUserId(),
                orderRequest.isBuy(),
                orderRequest.getUnit());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order-book")
    public ResponseEntity<OrderBookResponse> getOrderBook(){
        final Order[] bidOrders = exchange.getBookOrders(true);
        final Order[] askOrders = exchange.getBookOrders(false);
        final OrderBookResponse response = new OrderBookResponse();
        response.setBidOrders(bidOrders);
        response.setAskOrders(askOrders);
        return ResponseEntity.ok(response);
    }
}
