package net.btstream.performance.test.timescaledb;

import lombok.extern.slf4j.Slf4j;
import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.runners.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
@Slf4j
public class BasicConfig {

    private static BlockingQueue<TbGps> dataQueue = null;

    /**
     * Threads' number for consumers. A consumer is used to save datum to database.
     */
    @Value("${consumer.threads:4}")
    int customerCore;

    @Value("${consumer.batchSize:20}")
    int consumerBatchSize;

    @Value("${consumer.type:jdbcTemplate}")
    String consumerType;

    /**
     * Threads' number for generators. A generator is used to generate datum.
     */
    @Value("${generator.threads:4}")
    int genCore;

    @Value("${generator.deviceNum:20000}")
    int deviceNum;

    @Resource
    DataSource dataSource;

    @Resource
    JdbcTemplate jdbcTemplate;

    @Bean(name = "Generator")
    Executor generator() {
        int corePoolSize = customerCore;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setThreadNamePrefix("DG");
        return executor;
    }

    @Bean(name = "Consumer")
    Executor consumer() {
        int corePoolSize = genCore;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setThreadNamePrefix("DC");
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
        EventLoopGroup generatorMainLoop = new EventLoopGroup(generator());
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
        EventLoopGroup consumerMainLoop = new EventLoopGroup(consumer());
        int loopNumbers = customerCore * 2 + 1;
        for (int i = 0; i < loopNumbers; i++) {
            EventLoop loop = generateEventLoop(consumerMainLoop);
            if (loop == null) {
                log.error("Create consumer loop error!");
                System.exit(-6);
            }
            loop.start();
        }
        return consumerMainLoop;
    }

    private EventLoop generateEventLoop(EventLoopGroup eventLoopGroup) {
        if (consumerType.equals("jdbc")) {
            log.info("Consumers are configured with type: {}, batch size: {}", "jdbc", consumerBatchSize);
            JdbcConsumerEventLoop jdbcE = new JdbcConsumerEventLoop(eventLoopGroup, mkQueue());
            jdbcE.setDataSource(dataSource);
            jdbcE.setBatchSize(consumerBatchSize);
            return jdbcE;
        } else if (consumerType.equals("jdbcTemplate")) {
            log.info("Consumers are configured with type: {}, batch size: {}", "jdbcTemplate", consumerBatchSize);
            JdbcTemplateConsumerEventLoop jdbcTE = new JdbcTemplateConsumerEventLoop(eventLoopGroup, mkQueue());
            jdbcTE.setBatchSize(consumerBatchSize);
            jdbcTE.setJdbcTemplate(jdbcTemplate);
            return jdbcTE;
        } else {
            return null;
        }
    }
}
