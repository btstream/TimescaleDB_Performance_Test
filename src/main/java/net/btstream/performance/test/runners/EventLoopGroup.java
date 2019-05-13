package net.btstream.performance.test.runners;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class EventLoopGroup {

    private Executor executor;

    public EventLoopGroup() {
        this(null);
    }

    public EventLoopGroup(Executor e) {
        if (e == null) {
            this.executor = Executors.newSingleThreadExecutor();
        } else {
            this.executor = e;
        }
    }

    public void submit(EventLoop event) {
        executor.execute(event);
    }
}
