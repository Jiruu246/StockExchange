package com.jiruu.orderservice.net;

import com.jiruu.net.Message;
import com.jiruu.net.MsgFlag;
import com.jiruu.orderservice.config.ServiceConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientConnection is responsible for managing the connection to a multicast group and sending/receiving messages.
 *
 * <p>Currently only message of type ACK is supported.</p>
 * <p>Currently it doesn't able to handle packet that are out of order</p>
 */
@Service
public class ClientConnection implements AutoCloseable {

    private final MulticastSocket multicastSocket;
    private final DatagramSocket datagramSocket;

    private final ConcurrentHashMap<Long, Message> messageCache = new ConcurrentHashMap<>();

    public ClientConnection(ServiceConfig serviceConfig) throws IOException {
        this.multicastSocket = new MulticastSocket(serviceConfig.getMulticast().getPort());
        this.datagramSocket = new DatagramSocket(serviceConfig.getUniCast().getPort());
    }

    public void joinGroup(SocketAddress mCastAddr) throws IOException {
        multicastSocket.joinGroup(mCastAddr, null);
    }

    public void send(Message message, InetAddress address, int port) throws IOException {
        byte[] messageBytes = message.toBytes();
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address, port);
        datagramSocket.send(packet);

        //TODO: Need to implement a failure handling mechanism
        // basically this need to await for the ACK message to be received to be considered as success
        // else it should throw an exception
    }

    public Message receive() throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            multicastSocket.receive(packet);
            try {
                Message message = Message.fromBytes(packet.getData());

                // We ignore messages that are not ACK or are already in the cache
                if (message.flag != MsgFlag.ACK || messageCache.containsKey(message.globalSequenceNumber)) {
                    continue;
                }

                assert message.globalSequenceNumber != -1L : "Global sequence number for ACK cannot be -1";

                messageCache.put(message.globalSequenceNumber, message);
                return message;
            } catch (Exception e) {
                throw new IOException("Unrecognized data received");
            }
        }
    }

    @Override
    public void close() {
        multicastSocket.close();
        datagramSocket.close();
    }
}
