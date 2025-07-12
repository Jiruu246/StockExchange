package com.jiruu.net;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Request implements Serializable {

    public final ReqType RequestType;
    public final UUID OrderId;
    public final double Limit;
    public final double EffectivePrice;
    public final int Units;

    public final int BYTES = Integer.BYTES // ReqType;
            + 16 // UUID
            + Double.BYTES // Limit
            + Double.BYTES // effectivePrice
            + Integer.BYTES; // Units

    public Request(ReqType requestType, UUID orderId, double limit, int units) {
        this(requestType, orderId, limit, 0, units);
    }

    public Request(ReqType requestType, UUID orderId, double limit, double effectivePrice, int units) {
        this.RequestType = requestType;
        this.OrderId = orderId;
        this.Limit = limit;
        this.EffectivePrice = effectivePrice;
        this.Units = units;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(BYTES);
        buffer.putInt(RequestType.ordinal());
        buffer.putLong(OrderId.getMostSignificantBits());
        buffer.putLong(OrderId.getLeastSignificantBits());
        buffer.putDouble(Limit);
        buffer.putDouble(EffectivePrice);
        buffer.putInt(Units);
        return buffer.array();
    }

    public static Request fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ReqType requestType = ReqType.values()[buffer.getInt()];
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();
        UUID orderId = new UUID(mostSigBits, leastSigBits);
        double limit = buffer.getDouble();
        double effectivePrice = buffer.getDouble();
        int units = buffer.getInt();
        return new Request(requestType, orderId, limit, effectivePrice, units);
    }

    public String toString() {
        return "Request{" +
                "RequestType=" + RequestType +
                ", OrderId=" + OrderId +
                ", Limit=" + Limit +
                ", effectivePrice=" + EffectivePrice +
                ", Units=" + Units +
                '}';
    }
}
