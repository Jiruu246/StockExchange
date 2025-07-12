package com.jiruu.orderservice;

import com.jiruu.orderservice.dto.OrdRqDTO;
import com.jiruu.orderservice.dto.OrderBookDTO;
import com.jiruu.orderservice.dto.OrderDTO;
import com.jiruu.orderservice.dto.PriceHistoryDTO;
import com.jiruu.orderservice.service.OrderService;
import com.jiruu.orderservice.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@CrossOrigin(
    origins = "http://localhost:5173",
    methods = {RequestMethod.GET, RequestMethod.POST},
    allowedHeaders = "*")
@RequestMapping("/api")
public class Controller {
    private final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private final OrderService orderService;
    private final ReportService reportService;

    @Autowired
    public Controller(OrderService orderService, ReportService reportService) {
        this.orderService = orderService;
        this.reportService = reportService;
    }

    @GetMapping("/register")
    public ResponseEntity<Map<String, String>> register() {
        final Map<String, String> response = new HashMap<>();
        response.put("userId", orderService.registerUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-verify/{userId}")
    public ResponseEntity<Map<String, Boolean>> verifyUser(@PathVariable String userId) {
        final Map<String, Boolean> response = new HashMap<>();
        response.put("userExists", orderService.userExists(userId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/limit-order")
    public ResponseEntity<OrderDTO> placeLimitOrder(@RequestBody OrdRqDTO orderRequest) {
        if (orderRequest.limit <= 0) {
            return ResponseEntity.badRequest().build();
        }

        OrderDTO order = orderService.placeLimitOrder(
                orderRequest.userId,
                orderRequest.isBuy,
                orderRequest.unit,
                orderRequest.limit
        );
        if (order == null) {
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orderId}")
                .buildAndExpand(order.getOrderId())
                .toUri();
        return ResponseEntity.created(location).body(order);
    }

    @PostMapping("/market-order")
    public ResponseEntity<Void> placeMarketOrder(@RequestBody OrdRqDTO orderRequest) {
        if (orderRequest.limit != 0) {
            return ResponseEntity.badRequest().build();
        }

        OrderDTO order = orderService.placeMarketOrder(
                orderRequest.userId,
                orderRequest.isBuy,
                orderRequest.unit
        );

        if (order == null) {
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orderId}")
                .buildAndExpand(order.getOrderId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/order-book/stream")
    public SseEmitter streamOrderBook() {
        SseEmitter emitter = new SseEmitter(0L);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                final OrderBookDTO orderBook = reportService.getOrderBookLogs();
                emitter.send(orderBook);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, 0, 5, TimeUnit.SECONDS);
        return emitter;
    }

    @GetMapping("/order-book/price-history")
    public SseEmitter streamPriceHistory() {
        SseEmitter emitter = new SseEmitter(0L);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                final PriceHistoryDTO[] priceHistories = reportService.getPriceHistories();
                emitter.send(priceHistories);
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }, 0, 1, TimeUnit.MINUTES);
        return emitter;
    }
}
