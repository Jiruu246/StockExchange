package com.jiruu.orderservice.service;

import com.jiruu.orderservice.config.ServiceConfig;
import com.jiruu.orderservice.net.ClientConnection;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class TaskRunnerService {
    private final Logger LOGGER = Logger.getLogger(TaskRunnerService.class.getName());

    private final OrderService orderService;
    private final ClientConnection clientConnection;
    private final ServiceConfig serviceConfig;

    public TaskRunnerService(
            OrderService orderService,
            ClientConnection clientConnection,
            ServiceConfig serviceConfig) {
        this.orderService = orderService;
        this.clientConnection = clientConnection;
        this.serviceConfig = serviceConfig;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onAppReady() {
        try {
            clientConnection.joinGroup(
                    new InetSocketAddress(
                            InetAddress.getByName(serviceConfig.getMulticast().getGroup()),
                            serviceConfig.getMulticast().getPort()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error joining multicast group: " + e.getMessage());
        }
        orderService.startListening();
    }
}
