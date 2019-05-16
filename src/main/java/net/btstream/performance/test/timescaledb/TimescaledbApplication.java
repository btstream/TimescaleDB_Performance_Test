package net.btstream.performance.test.timescaledb;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@MapperScan("net.btstream.performance.test.db.mapper")
@Slf4j
public class TimescaledbApplication implements CommandLineRunner {

    @Value("${result.file:result.txt}")
    String resultFile;

    @Value("${run.time:5h}")
    String runTime;

    public static void main(String[] args) {
        SpringApplication.run(TimescaledbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // 处理测试时间参数，默认跑5小时
        String inputedTu = runTime.substring(runTime.length() - 1);
        String runTimeNum = runTime.substring(0, runTime.length());
        Duration rd = null;
        try {
            int rt = Integer.parseInt(runTimeNum);
            if (inputedTu.toLowerCase().equals("h")) {
                rd = Duration.ofHours(rt);
            } else if (inputedTu.toLowerCase().equals("m")) {
                rd = Duration.ofMinutes(rt);
            } else if (inputedTu.toLowerCase().equals("s")) {
                rd = Duration.ofSeconds(rt);
            } else {
                rd = Duration.ofHours(5);
            }
        } catch (NumberFormatException e) {
            log.error("Inputed time format is wrong", e);
            System.exit(-5);
        }

        assert rd != null;

        try (BufferedOutputStream resultOut = new BufferedOutputStream(new FileOutputStream(resultFile));) {

            LocalDateTime startTime = LocalDateTime.now();
            while (true) {

                // TODO: 实现日志输出

                if (Duration.between(startTime, LocalDateTime.now()).compareTo(rd) > 0) {
                    break;
                }
            }
        }

    }
}
