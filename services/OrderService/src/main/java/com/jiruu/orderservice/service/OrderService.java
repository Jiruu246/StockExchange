package com.jiruu.orderservice.service;

import com.jiruu.net.Message;
import com.jiruu.net.MsgFlag;
import com.jiruu.net.ReqType;
import com.jiruu.net.Request;
import com.jiruu.orderservice.config.ServiceConfig;
import com.jiruu.orderservice.dto.OrderDTO;
import com.jiruu.orderservice.net.ClientConnection;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class OrderService {
    private final Logger LOGGER = Logger.getLogger(OrderService.class.getName());
    private final ClientConnection connection;
    private final ReportService reportService;
    private final ServiceConfig serviceConfig;

    private final List<String> users = new ArrayList<>();

    private final AtomicBoolean running = new AtomicBoolean(false);

    public OrderService(
            ClientConnection clientConnection,
            ReportService reportService,
            ServiceConfig serviceConfig) {
        this.connection = clientConnection;
        this.reportService = reportService;
        this.serviceConfig = serviceConfig;
    }

    @PreDestroy
    public void close() {
        running.set(false);
        connection.close();
    }

    public String registerUser() {
        String userId = UUID.randomUUID().toString();
        users.add(userId);
        return userId;
    }

    public OrderDTO placeMarketOrder(String userId, boolean isBuy, int units) {
        return placeLimitOrder(userId, isBuy, units, 0);
    }

    public OrderDTO placeLimitOrder(String userId, boolean isBuy, int units, double price) {
        if (!users.contains(userId)) {
            return null;
        }
        UUID orderId = UUID.randomUUID();
        ReqType reqType = isBuy ? ReqType.BUY : ReqType.SEL;
        Request request = new Request(reqType, orderId, price, units);
        Message message = new Message(MsgFlag.PSH, 1, UUID.randomUUID(), -1L, 0L, request.toBytes());
        try {
            connection.send(
                    message,
                    InetAddress.getByName(serviceConfig.getMatchingEngine().getIp()),
                    serviceConfig.getMatchingEngine().getPort());
            return new OrderDTO(orderId.toString(), isBuy, units, price);
        } catch (Exception e) {
            LOGGER.severe("Error occurred: " + e.getMessage());
            return null;
        }
    }

    @Async
    public void startListening() {
        running.set(true);
        try {
            while (running.get()) {
                Message message = connection.receive();
                Request request = Request.fromBytes(message.payload);
                System.out.println("Received message: " + request);

                switch (request.RequestType) {
                    case BUY:
                    case SEL:
                        reportService.recordOrder(request.Limit, request.Units, request.RequestType == ReqType.BUY);
                        break;
                    case BOT:
                    case SLD:
                        reportService.recordTransaction(request.Limit, request.Units, request.RequestType == ReqType.BOT);
                        break;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error in listen Multicast: " + e.getMessage());
        }
    }

    public Boolean userExists(String userId) {
        return users.contains(userId);
    }
}
