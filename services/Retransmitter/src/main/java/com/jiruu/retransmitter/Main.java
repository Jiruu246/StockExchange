package com.jiruu.retransmitter;

import com.jiruu.net.Message;
import com.jiruu.net.MsgFlag;

import java.io.IOException;
import java.net.*;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
  private static final int THREAD_POOL_SIZE = 10;

  private static final String MULTICAST_GROUP = "239.255.255.250";
  private static final int PORT_SERVER = 4446;
  private static final int PORT_RT = 4447;
  private static final String IP_SERVER = "127.0.0.1";
  private static final UUID sourceId = UUID.randomUUID();
  private static final int BUFFER_SIZE = 1024;

  private static final InetSocketAddress MULTICAST_ADDER =
      new InetSocketAddress(MULTICAST_GROUP, PORT_RT);
  private static final InetSocketAddress SERVER_ADDER =
      new InetSocketAddress(IP_SERVER, PORT_SERVER);

  private static final ConcurrentHashMap<Long, Message> messageCache = new ConcurrentHashMap<>();

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    executor.submit(Main::listenMulticast);
    executor.submit(Main::listenUniCast);
  }

  @SuppressWarnings("InfiniteLoopStatement") // Temporary until we find a better solution
  private static void listenMulticast() {
    try (MulticastSocket socket = new MulticastSocket(PORT_RT)) {
      socket.joinGroup(InetAddress.getByName(MULTICAST_GROUP));
      while (true) {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        Message message = Message.fromBytes(packet.getData());

        if (Objects.requireNonNull(message.flag) == MsgFlag.ACK) {
          System.out.println("Received ACK message");
          Thread receiveThread = new Thread(() -> handleReceiveAck(message));
          receiveThread.setDaemon(true);
          receiveThread.start();
        } else {
          // Only ACK is supported from multicast traffic
          LOGGER.info("Message type not supported");
        }
      }
    } catch (Exception e) {
      LOGGER.severe("Error in listen Multicast: " + e.getMessage());
    }
  }

  @SuppressWarnings("InfiniteLoopStatement") // Temporary until we find a better solution
  private static void listenUniCast() {
    try (DatagramSocket socket = new DatagramSocket(PORT_RT)) {
      while (true) {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        Message message = Message.fromBytes(packet.getData());

        if (message.flag == MsgFlag.NAK) {
          System.out.println("Received NAK message");
          Thread receiveThread =
              new Thread(
                  () ->
                      handleReceiveNak(
                          socket,
                          new InetSocketAddress(packet.getAddress(), packet.getPort()),
                          message));
          receiveThread.setDaemon(true);
          receiveThread.start();
        } else {
          // Only NAK is supported from uni cast traffic
          LOGGER.info("Message type not supported");
        }
      }
    } catch (IOException e) {
      LOGGER.severe("Error in listen UniCast: " + e.getMessage());
    }
  }

  private static void sendMessage(DatagramSocket socket, String message) {
    try {
      byte[] buffer =
          new Message(MsgFlag.PSH, 1, UUID.randomUUID(), 0, 1, message.getBytes()).toBytes();

      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, SERVER_ADDER);
      socket.send(packet);
      System.out.println("Message sent");
    } catch (Exception e) {
      LOGGER.severe("Error occurred: " + e.getMessage());
    }
  }

  private static void handleReceiveAck(Message message) {
    assert message.flag == MsgFlag.ACK : "Message flag should be ACK";

    if (messageCache.containsKey(message.globalSequenceNumber)) {
      return;
    }

    messageCache.put(message.globalSequenceNumber, message);
  }

  private static void handleReceiveNak(
      DatagramSocket socket, InetSocketAddress clientAdder, Message message) {
    assert message.flag == MsgFlag.NAK : "Message flag should be NAK";

    if (messageCache.containsKey(message.globalSequenceNumber)) {
      byte[] buffer = messageCache.get(message.globalSequenceNumber).toBytes();
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAdder);
      try {
        socket.send(packet);
        System.out.println("Send back missing message to client");
      } catch (IOException e) {
        LOGGER.severe("Error sending message: " + e.getMessage());
      }
    }
    // TODO: Implement a fallback mechanism to forward the message if it is not found in the cache
    else {
      System.out.println("Message not found in cache");
    }
  }
}
