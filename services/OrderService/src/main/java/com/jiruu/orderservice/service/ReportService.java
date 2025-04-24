package com.jiruu.orderservice.service;

import com.jiruu.orderservice.dto.LimitDTO;
import com.jiruu.orderservice.dto.OrderBookDTO;
import com.jiruu.orderservice.dto.OrderDTO;
import com.jiruu.orderservice.dto.PriceHistoryDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import java.util.logging.Logger;

@Service
public class ReportService {
    private static class OrderBookLogger {
        public final TreeMap<Double, Integer> bidVolumes = new TreeMap<>();
        public final TreeMap<Double, Integer> askVolumes = new TreeMap<>();
        public final Map<UUID, OrderDTO> bidOrders = new HashMap<>();
        public final Map<UUID, OrderDTO> askOrders = new HashMap<>();
    }
    private final OrderBookLogger orderBookLogger = new OrderBookLogger();
    private final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    private final List<PriceHistoryDTO> priceHistories = new LinkedList<>();
    private long period;
    private double currentOpen;
    private double currentHigh;
    private double currentLow;
    private double currentClose;

    public ReportService() {
        this.period = Instant.now().getEpochSecond();
        this.currentOpen = -1;
        this.currentHigh = -1;
        this.currentLow = -1;
        this.currentClose = -1;
    }

    /**
     * This method returns the price slice in the form of OHLC recorded every 5 minutes
     */
    public PriceHistoryDTO[] getPriceHistories() {
        return priceHistories.toArray(new PriceHistoryDTO[0]);
    }

    /**
     * This method returns the latest update of the order book it has
     * .The bid and ask orders are sorted in descending order of price
     */
    public OrderBookDTO getOrderBookLogs() {
        LimitDTO[] bidOrders = new LimitDTO[orderBookLogger.bidVolumes.size()];
        LimitDTO[] askOrders = new LimitDTO[orderBookLogger.askVolumes.size()];

        int i = 0;
        for (Map.Entry<Double, Integer> entry : orderBookLogger.bidVolumes.descendingMap().entrySet()) {
            bidOrders[i++] = new LimitDTO(entry.getKey(), entry.getValue());
        }
        i = 0;
        for (Map.Entry<Double, Integer> entry : orderBookLogger.askVolumes.entrySet()) {
            askOrders[i++] = new LimitDTO(entry.getKey(), entry.getValue());
        }
        return new OrderBookDTO(bidOrders, askOrders);
    }

    public void recordOrder(UUID orderId, double price, int volume, boolean isBuy) {
        Map<Double, Integer> volumes = isBuy ? orderBookLogger.bidVolumes : orderBookLogger.askVolumes;
        Map<UUID, OrderDTO> orders = isBuy ? orderBookLogger.bidOrders : orderBookLogger.askOrders;
        if (volumes.containsKey(price)) {
            volumes.put(price, volumes.get(price) + volume);
        } else {
            volumes.put(price, volume);
        }
        orders.put(orderId, new OrderDTO(orderId.toString(), isBuy, volume, price));
    }

    public void recordTransaction(UUID orderId, double price, int volume, boolean isBought) {
        Map<UUID, OrderDTO> orders = isBought ? orderBookLogger.bidOrders : orderBookLogger.askOrders;
        assert orders.containsKey(orderId): "Order not found in order book";
        Map<Double, Integer> volumes = isBought ? orderBookLogger.bidVolumes : orderBookLogger.askVolumes;
        OrderDTO order = orders.get(orderId);
        assert order != null: "Order not found in order book";
        assert order.getUnit() >= volume: "Order volume is less than transaction volume";
        Double Limit = order.getLimit();
        order.setUnit(order.getUnit() - volume);
        if (order.getUnit() == 0) {
            orders.remove(orderId);
        }
        assert volumes.containsKey(Limit) && (volumes.get(Limit) >= volume):
                "Cannot find Total volume or Total volume is less than transaction volume";
        volumes.put(Limit, volumes.get(Limit) - volume);
        if (volumes.get(Limit) == 0) {
            volumes.remove(Limit);
        }

        //No transaction have ever happened in this period so far
        if (currentOpen == -1) {
            currentOpen = price;
            currentHigh = price;
            currentLow = price;
            currentClose = price;
            return;
        }

        if (price > currentHigh) {
            currentHigh = price;
        } else if (price < currentLow) {
            currentLow = price;
        }

        currentClose = price;
    }

    @Scheduled(fixedRateString = "PT1M", initialDelayString = "PT1M")
    private void logging() {
        LOGGER.info("Finish period: " + LocalDateTime.ofInstant(
                Instant.ofEpochSecond(period),
                ZoneId.systemDefault()
        ));
        //No transaction have ever happened
        if (priceHistories.isEmpty()) {
            priceHistories.add(new PriceHistoryDTO(period, 0, 0, 0, 0));
            period = Instant.now().getEpochSecond();
            return;
        }

        //No transaction have happened in this period
        //So we take the last price history and use it as the current open, high, low and close
        if (currentOpen == -1) {
            double lastClose = priceHistories.get(priceHistories.size() - 1).Close;
            priceHistories.add(new PriceHistoryDTO(period, lastClose, lastClose, lastClose, lastClose));
            period = Instant.now().getEpochSecond();
            return;
        }

        priceHistories.add(new PriceHistoryDTO(period, currentOpen, currentHigh, currentLow, currentClose));
        period = Instant.now().getEpochSecond();
        // Mark new period
        currentOpen = -1;
    }

}
