package net.btstream.performance.test.timescaledb;

import lombok.extern.slf4j.Slf4j;
import net.btstream.performance.test.utils.StatUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;

@SpringBootApplication
@MapperScan("net.btstream.performance.test.db.mapper")
@Slf4j
public class TimescaledbApplication implements CommandLineRunner {

    @Value("${result.file:result.txt}")
    String resultFile;

    @Value("${run.time:5h}")
    String runTime;

    @Value("${sample.time:10}")
    int sampleTime;

    @Resource(name = "Generator")
    Executor generator;

    @Resource(name = "Consumer")
    Executor consumer;

    private static final String RECORD_TEMPLATE = "%s,%d,%d\n";
    private static StatUtils statUtils = new StatUtils();
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        SpringApplication.run(TimescaledbApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // 处理测试时间参数，默认跑5小时
        String inputedTu = runTime.substring(runTime.length() - 1);
        String runTimeNum = runTime.substring(0, runTime.length() - 1);
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

        try (BufferedWriter resultOut = new BufferedWriter(new FileWriter(resultFile))) {
            LocalDateTime startTime = LocalDateTime.now();
            long startNum = 0;
            while (true) {
                Thread.sleep(sampleTime * 1000);
                long curNum = statUtils.get();
                long tp = (curNum - startNum) / sampleTime;
                String record = String.format(RECORD_TEMPLATE, df.format(LocalDateTime.now()), tp, curNum);
                log.info("{}", tp);
                resultOut.write(record);
                resultOut.flush();
                startNum = curNum;
                if (Duration.between(startTime, LocalDateTime.now()).compareTo(rd) > 0) {
                    break;
                }
            }
        }
//        ((ThreadPoolTaskExecutor) generator).shutdown();
//        ((ThreadPoolTaskExecutor) consumer).shutdown();
        System.exit(0);
    }
}
