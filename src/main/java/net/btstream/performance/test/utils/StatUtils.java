package net.btstream.performance.test.utils;

import java.util.concurrent.atomic.AtomicLong;

public class StatUtils {
    private static AtomicLong throughput = new AtomicLong(0);

    public void increament(int incr) {
        throughput.addAndGet(incr);
    }

    public void increament(long incr) {
        throughput.addAndGet(incr);
    }

    public long get() {
        return throughput.get();
    }

}
