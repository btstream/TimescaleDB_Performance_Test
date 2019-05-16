package net.btstream.performance.test.timescaledb;

import java.time.Duration;

import org.junit.Test;

import junit.framework.Assert;

/**
 * TestUtils
 */
public class TestUtils {

    @Test
    public void testDuration() {
        Duration d = Duration.ofHours(5), d2 = Duration.ofHours(6);
        Assert.assertTrue(d2.compareTo(d) > 0);

    }
}