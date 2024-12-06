package com.jiruu.net;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.UUID;

/** The Message class is the container for the data that is sent in a UDP network. */
public class Message implements Serializable {
  public final MsgFlag flag;
  public final int publisherId;
  public final UUID topicId;
  public final long globalSequenceNumber;
  public final long topicSequenceNumber;
  public final byte[] payload;

  public final InetAddress address;
  public final int port;

  private final int FIXED_HEADER_SIZE =
      Integer.BYTES // MsgFlag
          + Integer.BYTES // publisherId
          + 16 // UUID
          + Long.BYTES // globalSequenceNumber
          + Long.BYTES // topicSequenceNumber
          + Integer.BYTES // payload length
          + Integer.BYTES // address length
          + Integer.BYTES; // port

  public Message(
      MsgFlag flag, int publisherId, UUID topicId, long gloSeqNo, long topicSeqNo, byte[] payload) {
    this(flag, publisherId, topicId, gloSeqNo, topicSeqNo, payload, null, -1);
  }

  public Message(
      MsgFlag flag,
      int publisherId,
      UUID topicId,
      long globalSequenceNumber,
      long topicSequenceNumber,
      byte[] payload,
      InetAddress address,
      int port) {
    assert payload != null : "Payload cannot be null";
    byte[] addressBytes = (address != null) ? address.getAddress() : new byte[0];

    // To avoid fragmentation, the message size should be less than 1024 bytes.
    final int messageSize = payload.length + addressBytes.length + FIXED_HEADER_SIZE;
    final int MAX_MESSAGE_SIZE = 1024;
    assert messageSize > MAX_MESSAGE_SIZE : "Message size exceeds the maximum limit";

    this.flag = flag;
    this.publisherId = publisherId;
    this.topicId = topicId;
    this.globalSequenceNumber = globalSequenceNumber;
    this.topicSequenceNumber = topicSequenceNumber;
    this.payload = payload;
    this.address = address;
    this.port = port;
  }

  public byte[] toBytes() {
    assert payload != null : "Payload cannot be null";
    byte[] addressBytes = (address != null) ? address.getAddress() : new byte[0];

    ByteBuffer buffer =
        ByteBuffer.allocate(FIXED_HEADER_SIZE + payload.length + addressBytes.length);
    buffer.putInt(flag.ordinal());
    buffer.putInt(publisherId);
    buffer.putLong(topicId.getMostSignificantBits());
    buffer.putLong(topicId.getLeastSignificantBits());
    buffer.putLong(globalSequenceNumber);
    buffer.putLong(topicSequenceNumber);
    buffer.putInt(payload.length);
    buffer.put(payload);
    buffer.putInt(addressBytes.length);
    buffer.put(addressBytes);
    buffer.putInt(port);

    return buffer.array();
  }

  public static Message fromBytes(byte[] bytes) throws UnknownHostException {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    MsgFlag flag = MsgFlag.values()[buffer.getInt()];
    int publisherId = buffer.getInt();
    UUID topicId = new UUID(buffer.getLong(), buffer.getLong());
    long globalSequenceNumber = buffer.getLong();
    long topicSequenceNumber = buffer.getLong();
    int payloadLength = buffer.getInt();
    byte[] payload = new byte[payloadLength];
    buffer.get(payload);
    int addressLength = buffer.getInt();
    InetAddress address = null;
    if (addressLength > 0) {
      byte[] addressBytes = new byte[addressLength];
      buffer.get(addressBytes);
      address = InetAddress.getByAddress(addressBytes);
    }
    int port = buffer.getInt();

    return new Message(
        flag,
        publisherId,
        topicId,
        globalSequenceNumber,
        topicSequenceNumber,
        payload,
        address,
        port);
  }
}
