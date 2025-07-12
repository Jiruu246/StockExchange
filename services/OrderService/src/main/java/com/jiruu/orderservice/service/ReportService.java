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

    public void recordOrder(double price, int volume, boolean isBuy) {
        if (price <= 0 || volume <= 0) {
            throw new IllegalArgumentException("Price and volume must be greater than 0");
        }

        Map<Double, Integer> volumes = isBuy ? orderBookLogger.bidVolumes : orderBookLogger.askVolumes;
        if (volumes.containsKey(price)) {
            volumes.put(price, volumes.get(price) + volume);
        } else {
            volumes.put(price, volume);
        }
    }

    public void recordTransaction(double limitPrice, double effectivePrice, int volume, boolean isBought) {
//        if (limitPrice != -100) {
        if (limitPrice <= 0 || effectivePrice <= 0 || volume <= 0) {
            throw new IllegalArgumentException("Price and volume must be greater than 0");
        }
        Map<Double, Integer> volumes = isBought ? orderBookLogger.bidVolumes : orderBookLogger.askVolumes;
        if (!volumes.containsKey(limitPrice)) {
            throw new IllegalStateException("Cannot find Total volume");
        }

        volumes.put(limitPrice, volumes.get(limitPrice) - volume);

        if (volumes.get(limitPrice) < 0) {
            throw new IllegalStateException("Total volume is less than transaction volume");
        }
        if (volumes.get(limitPrice) == 0) {
            volumes.remove(limitPrice);
        }
//        }

        //No transaction have ever happened in this period so far
        if (currentOpen == -1) {
            currentOpen = effectivePrice;
            currentHigh = effectivePrice;
            currentLow = effectivePrice;
            currentClose = effectivePrice;
            return;
        }

        if (effectivePrice > currentHigh) {
            currentHigh = effectivePrice;
        } else if (effectivePrice < currentLow) {
            currentLow = effectivePrice;
        }

        currentClose = effectivePrice;
    }

    @Scheduled(fixedRateString = "PT1M", initialDelayString = "PT1M")
    public void logging() {
        LOGGER.info("Finish period: " + LocalDateTime.ofInstant(
                Instant.ofEpochSecond(period),
                ZoneId.systemDefault()
        ));
        //No transaction have ever happened until this point
        if (priceHistories.isEmpty() && currentOpen == -1) {
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
