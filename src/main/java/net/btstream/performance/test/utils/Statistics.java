package net.btstream.performance.test.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
    private static AtomicInteger throughput = new AtomicInteger(0);

    public void increament(int incr) {
        throughput.addAndGet(incr);
    }

    public int get() {
        return throughput.get();
    }

}
