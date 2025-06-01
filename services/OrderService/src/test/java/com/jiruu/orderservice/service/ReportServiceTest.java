package com.jiruu.orderservice.service;

import com.jiruu.orderservice.dto.PriceHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

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
        reportService.recordOrder(UUID.randomUUID(), 100.0, 10, true);
        reportService.recordOrder(UUID.randomUUID(), 101.0, 5, false);

        assertOHLC(reportService.getPriceHistories()[0], 100.0, 101.0, 100.0, 101.0);
    }

    @Test
    void recordTransaction() {
    }
}