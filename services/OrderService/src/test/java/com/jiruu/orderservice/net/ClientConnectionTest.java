package com.jiruu.orderservice.net;

import com.jiruu.net.Message;
import com.jiruu.net.MsgFlag;
import com.jiruu.net.ReqType;
import com.jiruu.net.Request;
import com.jiruu.orderservice.config.ServiceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "services.multicast.port=4447",
        "services.uniCast.port=4445",
})
@ContextConfiguration(classes = ClientConnectionTest.TestServiceConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClientConnectionTest {

    @TestConfiguration
    @EnableConfigurationProperties(ServiceConfig.class)
    @ComponentScan(basePackageClasses = ClientConnection.class)
    static class TestServiceConfig {}

    @Autowired
    private ClientConnection clientConnection;

    @AfterEach
    public void tearDown() throws IOException {
        clientConnection.close();
    }

    @Test
    void send() throws IOException {
        try (DatagramSocket receiverSocket = new DatagramSocket(0)) {
            int port = receiverSocket.getLocalPort();

            Request req = new Request(ReqType.BOT, UUID.randomUUID(), 10, 10);
            Message msg = new Message(MsgFlag.ACK, 0, UUID.randomUUID(), 0, 0, req.toBytes());
            clientConnection.send(msg, InetAddress.getLocalHost().getHostAddress(), port);

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            receiverSocket.receive(packet);

            assertArrayEquals(msg.toBytes(), Arrays.copyOf(packet.getData(), packet.getLength()));
        }
    }

    @Test
    void receive() throws IOException {
        final String expectedMulticastAddress = "230.0.0.1";
        final int expectedMulticastPort = 4447;

        clientConnection.joinGroup(
                new InetSocketAddress(
                        InetAddress.getByName(expectedMulticastAddress),
                        expectedMulticastPort));

        try (DatagramSocket senderSocket = new DatagramSocket()) {
            Request req = new Request(ReqType.BOT, UUID.randomUUID(), 10, 10);
            Message msg = new Message(MsgFlag.ACK, 0, UUID.randomUUID(), 0, 0, req.toBytes());
            byte[] messageBytes = msg.toBytes();
            DatagramPacket packet = new DatagramPacket(
                    messageBytes, messageBytes.length,
                    InetAddress.getByName(expectedMulticastAddress), expectedMulticastPort);
            senderSocket.send(packet);

            Message receivedMessage = clientConnection.receive();
            assertNotNull(receivedMessage);
            assertEquals(msg.flag, receivedMessage.flag);
            assertArrayEquals(msg.toBytes(), receivedMessage.toBytes());
        }
    }
}