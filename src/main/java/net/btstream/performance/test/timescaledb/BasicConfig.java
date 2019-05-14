package net.btstream.performance.test.timescaledb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class BasicConfig {

    @Value("${save.core:4}")
    int customerCore;

    @Value("${gen.core:4}")
    int genCore;

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

}
