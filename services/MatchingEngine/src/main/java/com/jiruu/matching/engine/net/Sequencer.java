package com.jiruu.matching.engine.net;

import java.util.concurrent.atomic.AtomicLong;

public class Sequencer {
    private static final Sequencer INSTANCE = new Sequencer();
    private final AtomicLong globalSequenceNumber = new AtomicLong(0);

    private Sequencer() {}

    public static Sequencer getInstance() {
        return INSTANCE;
    }

    public long getNextSequenceNumber() {
        return globalSequenceNumber.getAndIncrement();
    }


}
