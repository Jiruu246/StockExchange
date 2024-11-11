package com.jiruu;

import com.jiruu.dto.LimitResponse;
import com.jiruu.dto.OrderBookResponse;
import com.jiruu.dto.OrderRequest;
import com.jiruu.model.Order;
import com.jiruu.service.Exchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Console;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(
        origins = "http://localhost:5173",
        methods = {RequestMethod.GET, RequestMethod.POST},
        allowedHeaders = "*")
@RequestMapping("/api")
public class Controller {

    private final Exchange exchange = new Exchange();

    @GetMapping("/register")
    public ResponseEntity<Map<String, String>> register() {
        final Map<String, String> response = new HashMap<>();
        response.put("userId", exchange.registerUser());
        return ResponseEntity.ok(response);
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

    @GetMapping("/order-book/stream")
    public SseEmitter streamOrderBook(){
        final SseEmitter emitter = new SseEmitter(0L);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                final LimitResponse[] bidOrders = Arrays.stream(exchange.getBookOrders(true))
                        .collect(Collectors.groupingBy(Order::getLimit, Collectors.summingInt(Order::getUnit)))
                        .entrySet().stream()
                        .map(entry -> new LimitResponse(entry.getKey(), entry.getValue()))
                        .toArray(LimitResponse[]::new);

                final LimitResponse[] askOrders = Arrays.stream(exchange.getBookOrders(false))
                        .collect(Collectors.groupingBy(Order::getLimit, Collectors.summingInt(Order::getUnit)))
                        .entrySet().stream()
                        .map(entry -> new LimitResponse(entry.getKey(), entry.getValue()))
                        .toArray(LimitResponse[]::new);

                final OrderBookResponse response = new OrderBookResponse(bidOrders, askOrders);
                emitter.send(response);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
        return emitter;
    }

    @GetMapping("/user-verify/{userId}")
    public ResponseEntity<Map<String, Boolean>> verifyUser(@PathVariable String userId) {
        final Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exchange.userExists(userId));
        return ResponseEntity.ok(response);
    }
}