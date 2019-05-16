package net.btstream.performance.test.runners;

public abstract class EventLoop implements Runnable {

    private EventLoopGroup eventLoopGroup;

    private boolean shutdown = false;

    public EventLoop(EventLoopGroup group) {
        this.eventLoopGroup = group;
    }

    public void start() {
        this.eventLoopGroup.submit(this);
    }

    @Override
    public void run() {
        this.execute();
        if (!shutdown) {
            this.nextLoop();
        }
    }

    private void nextLoop() {
        this.eventLoopGroup.submit(this);
    }

    public void shutdownGracefully() {
        this.shutdown = true;
    }

    public abstract void execute();
}
