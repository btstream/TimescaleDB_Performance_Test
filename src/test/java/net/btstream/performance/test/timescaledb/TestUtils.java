package net.btstream.performance.test.timescaledb;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TestUtils
 */
public class TestUtils {

    @Test
    public void testDuration() {
        Duration d = Duration.ofHours(5), d2 = Duration.ofHours(6);
        Assert.assertTrue(d2.compareTo(d) > 0);
    }

    @Test
    public void testDateFormat() {
        LocalDateTime d = LocalDateTime.now();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        System.out.println(f.format(d));
    }
}