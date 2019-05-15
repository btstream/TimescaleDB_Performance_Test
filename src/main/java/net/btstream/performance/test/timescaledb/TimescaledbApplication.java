package net.btstream.performance.test.timescaledb;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("net.btstream.performance.test.db.mapper")
@Slf4j
public class TimescaledbApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TimescaledbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
