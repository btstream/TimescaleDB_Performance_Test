package net.btstream.performance.test.timescaledb;

import lombok.extern.slf4j.Slf4j;
import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.db.mapper.GpsMapper;
import net.btstream.performance.test.runners.DataConsumerEventLoop;
import net.btstream.performance.test.runners.DataGeneratEventLoop;
import net.btstream.performance.test.runners.EventLoopGroup;
import net.btstream.performance.test.utils.Statistics;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
@MapperScan("net.btstream.performance.test.db.mapper")
@Slf4j
public class TimescaledbApplication implements CommandLineRunner {

    @Resource(name = "Generator")
    Executor generator;

    @Resource(name = "Consumer")
    Executor consumer;

    @Resource
    GpsMapper gpsMapper;

    public static void main(String[] args) {
        SpringApplication.run(TimescaledbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ((ThreadPoolTaskExecutor) generator).shutdown();
            ((ThreadPoolTaskExecutor) consumer).shutdown();
        }));

        Statistics sta = new Statistics();


        EventLoopGroup generatorEventLoop = new EventLoopGroup(generator),
                consumerEventLoop = new EventLoopGroup(consumer);

        BlockingQueue<TbGps> queue = new LinkedBlockingQueue<>(20000);

        for (int i = 0; i < 4; i++) {
            DataGeneratEventLoop g = new DataGeneratEventLoop(queue, generatorEventLoop);
            g.start();
        }

        for (int i = 0; i < 4; i++) {
            DataConsumerEventLoop c = new DataConsumerEventLoop(queue, consumerEventLoop);
            c.setGpsMapper(gpsMapper);
            c.start();
        }

        int lastRecord = 0;
        while (true) {
            Thread.sleep(1000);
            int k = sta.get();
            log.info(String.format("%d", k - lastRecord));
            lastRecord = k;
        }
    }
}
