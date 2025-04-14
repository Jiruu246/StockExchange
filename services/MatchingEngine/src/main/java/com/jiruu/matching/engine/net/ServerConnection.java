package com.jiruu.matching.engine.net;

import com.jiruu.net.Message;
import com.jiruu.net.MsgFlag;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServerConnection is a class that handles the server-side connection for receiving and sending
 * multicast messages that encompass some of the drawbacks of UDP traffic.
 *
 * <p>Currently it only supports PSH and NAK message
 *
 * <p>Currently it doesn't have the ability to handle NAK
 */
public class ServerConnection implements AutoCloseable {

  private final DatagramSocket socket;
  // TODO: This implementation is quite coupled
  private final String multicastGroup;
  private final int multicastPort;

  private final ConcurrentHashMap<Long, Message> messageCache = new ConcurrentHashMap<>();

  private static final List<MsgFlag> SUPPORTED_FLAGS = Arrays.asList(MsgFlag.PSH, MsgFlag.NAK);

  public ServerConnection(int port, String multicastGroup, int multicastPort) throws IOException {
    this.socket = new DatagramSocket(port);
    this.multicastGroup = multicastGroup;
    this.multicastPort = multicastPort;
    System.out.println("ServerConnection created");
  }

  public Message receive() throws IOException {
    final int BUFFER_SIZE = 1024;
    byte[] buffer = new byte[BUFFER_SIZE];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    while (true) {
      socket.receive(packet);
      try {
        Message message = Message.fromBytes(packet.getData());

        // We ignore messages that are not supported or are already in the cache
        if (!SUPPORTED_FLAGS.contains(message.flag)
            || messageCache.containsKey(message.globalSequenceNumber)) {
          continue;
        }

        // TODO: Known bug, if the sequence number is not used after generated, there will be a gap
        // in
        // the seq no
        // Other services works on the premise that sequence number is continuous which can cause
        // some
        // issues
        // We assign a global sequence number to the message regardless of its flag
        long globalSeqNum = Sequencer.getInstance().getNextSequenceNumber();
        System.out.println(
            "Assign global sequence number "
                + globalSeqNum
                + " to message from "
                + message.publisherId);
        messageCache.put(globalSeqNum, message);

        final Message messageClone =
            new Message(
                message.flag,
                message.publisherId,
                message.topicId,
                globalSeqNum,
                message.topicSequenceNumber,
                message.payload);
        Thread receiveThread = new Thread(() -> handleReceive(messageClone));
        receiveThread.setDaemon(true);
        receiveThread.start();
        return messageClone;

      } catch (Exception e) {
        throw new IOException("Unrecognised data received");
      }
    }
  }

  private void handleReceive(Message message) {
    try {
      switch (message.flag) {
        case PSH:
          sendAck(message);
          break;
        case NAK:
          System.out.println("Received NAK message");
          throw new UnsupportedOperationException("NAK message handling is not supported");
        default:
          System.out.println("Unsupported message flag: " + message.flag);
          break;
      }
    } catch (Exception e) {
      System.err.println("Error processing received message: " + e.getMessage());
    }
  }

  private void sendAck(Message PshMessage) throws IOException {
    assert PshMessage.flag == MsgFlag.PSH : "Message flag should be PSH";

    Message ack =
        new Message(
            MsgFlag.ACK,
            PshMessage.publisherId,
            PshMessage.topicId,
            PshMessage.globalSequenceNumber,
            PshMessage.topicSequenceNumber,
            PshMessage.payload,
            InetAddress.getByName(multicastGroup),
            multicastPort);

    sendMulticast(ack);
  }

  public void sendMulticast(Message message) throws IOException {
    InetSocketAddress address = new InetSocketAddress(message.address, message.port);
    byte[] buffer = message.toBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address);
    socket.send(packet);
  }

  public void close() {
    socket.close();
  }
}
