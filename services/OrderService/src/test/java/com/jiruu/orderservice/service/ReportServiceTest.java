package com.jiruu.orderservice.service;

import com.jiruu.orderservice.dto.LimitDTO;
import com.jiruu.orderservice.dto.PriceHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService();
    }

    private void assertOHLC(PriceHistoryDTO priceHistory, double open, double high, double low, double close) {
        assertEquals(open, priceHistory.Open, "Open price mismatch");
        assertEquals(high, priceHistory.High, "High price mismatch");
        assertEquals(low, priceHistory.Low, "Low price mismatch");
        assertEquals(close, priceHistory.Close, "Close price mismatch");
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
    void recordTransactionErrors() {
        final String GREATER_THAN_ZERO = "Price and volume must be greater than 0";
        final String CANNOT_FIND_TOTAL_VOLUME_ERROR = "Cannot find Total volume";

        IllegalStateException illegalStateException = assertThrows(
                IllegalStateException.class,
                () -> reportService.recordTransaction(100.0, 100.0, 100, true)
        );
        assertEquals(CANNOT_FIND_TOTAL_VOLUME_ERROR, illegalStateException.getMessage());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> reportService.recordTransaction(0, 10, 10, true));
        assertEquals(GREATER_THAN_ZERO, exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> reportService.recordTransaction(10, 0, 10, false));
        assertEquals(GREATER_THAN_ZERO, exception.getMessage());
        exception = assertThrows(IllegalArgumentException.class, () -> reportService.recordTransaction(10, 10, 0, true));
        assertEquals(GREATER_THAN_ZERO, exception.getMessage());
    }

    @Test
    void recordTransactionOHLC() {
        //No transaction has been recorded yet
        reportService.logging();
        assertOHLC(reportService.getPriceHistories()[0], 0, 0, 0, 0);
        reportService.logging();
        assertOHLC(reportService.getPriceHistories()[1], 0, 0, 0, 0);

        //one transaction has been recorded
        reportService.recordOrder(100.0, 10, true);
        reportService.recordTransaction(100.0, 100.0, 10, true);
        reportService.logging();
        assertOHLC(reportService.getPriceHistories()[2], 100.0, 100.0, 100.0, 100.0);

        //No transaction recorded between these two logging calls
        reportService.logging();
        assertOHLC(reportService.getPriceHistories()[3], 100.0, 100.0, 100.0, 100.0);

        //multiple transactions have been recorded
        reportService.recordOrder(110, 20, true);
        reportService.recordTransaction(110, 110.0, 20, true);
        reportService.recordOrder(101, 5, false);
        reportService.recordTransaction(101, 101.0, 5, false);
        reportService.recordOrder(99, 15, false);
        reportService.recordTransaction(99, 99.0, 15, false);

        reportService.logging();

        assertOHLC(reportService.getPriceHistories()[4], 110.0, 110.0, 99.0, 99.0);

        //new price greater than current high
        reportService.recordOrder(120, 10, true);
        reportService.recordTransaction(120, 120.0, 10, true);
        reportService.recordOrder(130, 5, true);
        reportService.recordTransaction(130, 130.0, 5, true);

        reportService.logging();
        assertOHLC(reportService.getPriceHistories()[5], 120.0, 130.0, 120.0, 130.0);

    }

    @Test
    void recordTransactionOrderBook() {
        reportService.recordOrder(110.0, 20, true);
        reportService.recordOrder(100.0, 10, true);
        reportService.recordOrder(101.0, 5, false);
        reportService.recordOrder(99.0, 15, false);

        reportService.logging();

        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(110.0, 20), new LimitDTO(100.0, 10)},
                reportService.getOrderBookLogs().bidOrders());
        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(99.0, 15), new LimitDTO(101.0, 5)},
                reportService.getOrderBookLogs().askOrders());
    }
    @Test
    void recordTransactionAdditionalVolumeOnSameLimit() {
        reportService.recordOrder(130.0, 10, true);
        reportService.recordOrder(130.0, 5, true);
        reportService.recordOrder(110.0, 20, false);
        reportService.recordOrder(110.0, 10, false);
        reportService.logging();
        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(130.0, 15)},
                reportService.getOrderBookLogs().bidOrders());

        assertArrayEquals(
                new LimitDTO[]{new LimitDTO(110.0, 30)},
                reportService.getOrderBookLogs().askOrders());
    }
}