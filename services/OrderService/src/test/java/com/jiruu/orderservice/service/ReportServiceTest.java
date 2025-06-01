package com.jiruu.orderservice.service;

import com.jiruu.orderservice.dto.LimitDTO;
import com.jiruu.orderservice.dto.OrderBookDTO;
import com.jiruu.orderservice.dto.PriceHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
    }

    private void assertOHLC(PriceHistoryDTO priceHistory, double open, double high, double low, double close) {
        assertEquals(open, priceHistory.Open);
        assertEquals(high, priceHistory.High);
        assertEquals(low, priceHistory.Low);
        assertEquals(close, priceHistory.Low);
    }

    @Test
    void recordOrder() {
        reportService.recordOrder(100.0, 10, true);
        reportService.recordOrder(110.0, 20, true);
        reportService.recordOrder(101.0, 5, false);
        reportService.recordOrder(99.0, 15, false);

        reportService.logging();

        assertOHLC(reportService.getPriceHistories()[0], 0, 0, 0, 0);
        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(110.0, 20), new LimitDTO(100.0, 10)},
                reportService.getOrderBookLogs().bidOrders());
        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(99.0, 15), new LimitDTO(101.0, 5)},
                reportService.getOrderBookLogs().askOrders());

        assertThrows(IllegalArgumentException.class, () -> reportService.recordOrder(0, 10, true));
        assertThrows(IllegalArgumentException.class, () -> reportService.recordOrder(10, 0, false));

    }

    @Test
    void recordTransaction() {
        reportService.recordOrder(100.0, 10, true);
        reportService.recordTransaction(100.0, 100.0, 10, true);
        reportService.logging();

        assertOHLC(reportService.getPriceHistories()[0], 100.0, 100.0, 100.0, 100.0);

        AssertionError error = assertThrows(
                AssertionError.class,
                () -> reportService.recordTransaction(100.0, 100.0, 0, true)
        );
        assertEquals("Cannot find Total volume or Total volume is less than transaction volume",
                error.getMessage());

        reportService.recordTransaction(110, 110.0, 20, true);
        reportService.recordTransaction(101, 101.0, 5, false);
        reportService.recordTransaction(99, 99.0, 15, false);

        reportService.logging();

        assertOHLC(reportService.getPriceHistories()[0], 100.0, 110.0, 99.0, 101.0);
        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(110.0, 20), new LimitDTO(100.0, 10)},
                reportService.getOrderBookLogs().bidOrders());
        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(99.0, 15), new LimitDTO(101.0, 5)},
                reportService.getOrderBookLogs().askOrders());

//        assertThrows(IllegalArgumentException.class, () -> reportService.recordTransaction(0, 10, true));
//        assertThrows(IllegalArgumentException.class, () -> reportService.recordTransaction(10, 0, false));
    }
}