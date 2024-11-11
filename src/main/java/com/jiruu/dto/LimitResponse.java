package com.jiruu.dto;

public class LimitResponse {
    private double limit;
    private int volume;

    public LimitResponse(double limit, int volume) {
        this.limit = limit;
        this.volume = volume;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
