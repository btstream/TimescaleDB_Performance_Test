package net.btstream.performance.test.timescaledb;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.runners.DataGeneratEventLoop;
import net.btstream.performance.test.runners.EventLoop;
import net.btstream.performance.test.runners.EventLoopGroup;
import net.btstream.performance.test.runners.JdbcConsumerEventLoop;
import net.btstream.performance.test.runners.JdbcTemplateConsumerEventLoop;

@Configuration
public class BasicConfig {

    private static BlockingQueue<TbGps> dataQueue = null;

    /**
     * Threads' number for consumers. A consumer is used to save datum to database.
     */
    @Value("${consumer.core:4}")
    int customerCore;

    /**
     * Threads' number for generators. A generator is used to generate datum.
     */
    @Value("${generator.core:4}")
    int genCore;

    @Value("${generator.deviceNum:20000}")
    int deviceNum;

    @Value("${dbcon.type:jdbcTemplate}")
    String dbConType;

    @Bean(name = "Generator")
    Executor generator() {
        int corePoolSize = customerCore;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        return executor;
    }

    @Bean(name = "Consumer")
    Executor consumer() {
        int corePoolSize = genCore;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        return executor;
    }

    @Bean
    BlockingQueue<TbGps> mkQueue() {
        if (dataQueue == null) {
            dataQueue = new LinkedBlockingQueue<>(deviceNum);
        }
        return dataQueue;
    }

    @Bean(name = "GeneratorMainLoop")
    EventLoopGroup genratorMainLoop() {
        EventLoopGroup generatorMainLoop = new EventLoopGroup();
        int loopNumbers = genCore;

        int devicePerEventLoop = deviceNum / loopNumbers;

        for (int i = 0; i < loopNumbers; i++) {
            DataGeneratEventLoop dgEventLoop = new DataGeneratEventLoop(mkQueue(), generatorMainLoop);
            // allocate devices to different eventloop.
            if (i + 1 < loopNumbers) {
                dgEventLoop.setIdRange(devicePerEventLoop * i, devicePerEventLoop * (i + 1) - 1);
            } else {
                dgEventLoop.setIdRange(devicePerEventLoop * i, deviceNum);
            }
            dgEventLoop.start();
        }
        return generatorMainLoop;
    }

    @Bean(name = "ConsumerMainLoop")
    EventLoopGroup consumerMainLoop() {
        EventLoopGroup consumerMainLoop = new EventLoopGroup();
        int loopNumbers = customerCore * 2 + 1;
        for (int i = 0; i < loopNumbers; i++) {
            EventLoop loop = generateEventLoop(consumerMainLoop);
            loop.start();
        }
        return consumerMainLoop;
    }

    private EventLoop generateEventLoop(EventLoopGroup eventLoopGroup) {
        if (dbConType.equals("jdbcTemplate")) {
            return new JdbcConsumerEventLoop(eventLoopGroup, mkQueue());
        } else if (dbConType.equals("jdbc")) {
            return new JdbcTemplateConsumerEventLoop(eventLoopGroup, mkQueue());
        } else {
            return null;
        }
    }

}
