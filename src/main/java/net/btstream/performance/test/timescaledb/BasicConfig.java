package net.btstream.performance.test.timescaledb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class BasicConfig {

    @Bean(name = "Generator")
    Executor generator() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 1;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        return executor;
    }

    @Bean(name = "Consumer")
    Executor consumer() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 1;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        return executor;
    }

}
