package com.jiruu.matching.engine.model;

public class Transaction {
    private final String toId;
    private final String fromId;
    private final double effectivePrice;
    private final int filledUnits;

    public Transaction(String toId, String fromId, double effectivePrice, int filledUnits) {
        this.toId = toId;
        this.fromId = fromId;
        this.effectivePrice = effectivePrice;
        this.filledUnits = filledUnits;
    }

    public String getToId() {
        return toId;
    }

    public String getFromId() {
        return fromId;
    }

    public double getEffectivePrice() {
        return effectivePrice;
    }

    public int getFilledUnits() {
        return filledUnits;
    }

    @Override
    public String toString() {
        return "Match{" +
                "toId=" + toId +
                ", fromId=" + fromId +
                ", effectivePrice=" + effectivePrice +
                ", filledUnits=" + filledUnits +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) obj;
        return Double.compare(transaction.effectivePrice, effectivePrice) == 0 &&
                filledUnits == transaction.filledUnits &&
                toId.equals(transaction.toId) &&
                fromId.equals(transaction.fromId);
    }

    @Override
    public int hashCode() {
        int result = toId.hashCode();
        result = 31 * result + fromId.hashCode();
        result = 31 * result + (int) Double.doubleToLongBits(effectivePrice);
        result = 31 * result + filledUnits;
        return result;
    }
}
