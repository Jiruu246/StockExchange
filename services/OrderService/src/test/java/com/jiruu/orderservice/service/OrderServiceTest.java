package com.jiruu.orderservice.service;

import com.jiruu.orderservice.config.ServiceConfig;
import com.jiruu.orderservice.dto.OrderDTO;
import com.jiruu.orderservice.net.ClientConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ClientConnection clientConnection;

    @Mock
    private ReportService reportService;

    @Mock
    private ServiceConfig serviceConfig;

    @InjectMocks
    private OrderService orderService;

    @BeforeAll
    static void setUp() {
    }

    @Test
    void registerUser() {
        String userId = orderService.registerUser();
        assertNotNull(userId);
        assertTrue(orderService.userExists(userId));
        assertFalse(orderService.userExists("randomUserId"));
    }

    @Test
    void placeMarketOrder() {
        when(serviceConfig.getMatchingEngine()).thenReturn(mock(ServiceConfig.Service.class));
        when(serviceConfig.getMatchingEngine().getIp()).thenReturn("DummyIP");
        when(serviceConfig.getMatchingEngine().getPort()).thenReturn(-1111);

        OrderDTO order = orderService.placeMarketOrder(orderService.registerUser(), true, 10);
        assertEquals(0, order.getLimit());
        assertEquals(10, order.getUnit());
        assertTrue(order.isBuy());
    }

    @Test
    void placeLimitOrder() {
        when(serviceConfig.getMatchingEngine()).thenReturn(mock(ServiceConfig.Service.class));
        when(serviceConfig.getMatchingEngine().getIp()).thenReturn("DummyIP");
        when(serviceConfig.getMatchingEngine().getPort()).thenReturn(-1111);

        OrderDTO order = orderService.placeLimitOrder(orderService.registerUser(), true, 10, 100.0);
        assertEquals(100.0, order.getLimit());
        assertEquals(10, order.getUnit());
        assertTrue(order.isBuy());
    }
}