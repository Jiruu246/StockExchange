package com.jiruu.model;

public class Match {
    private final String bidId;
    private final String askId;
    private final double effectivePrice;
    private final int filledUnits;

    public Match(String bidId, String askId, double effectivePrice, int filledUnits) {
        this.bidId = bidId;
        this.askId = askId;
        this.effectivePrice = effectivePrice;
        this.filledUnits = filledUnits;
    }

    public String getBidId() {
        return bidId;
    }

    public String getAskId() {
        return askId;
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
                "bids=" + bidId +
                ", asks=" + askId +
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
        Match match = (Match) obj;
        return Double.compare(match.effectivePrice, effectivePrice) == 0 &&
                filledUnits == match.filledUnits &&
                bidId.equals(match.bidId) &&
                askId.equals(match.askId);
    }

    @Override
    public int hashCode() {
        int result = bidId.hashCode();
        result = 31 * result + askId.hashCode();
        result = 31 * result + (int) Double.doubleToLongBits(effectivePrice);
        result = 31 * result + filledUnits;
        return result;
    }
}
