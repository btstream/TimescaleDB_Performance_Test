package net.btstream.performance.test.utils;

import net.btstream.performance.test.db.bean.TbGps;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class PollUtils {
    public static List<TbGps> pollRecords(BlockingQueue<TbGps> queue, int num, long timeout, TimeUnit tu) throws InterruptedException {
        List<TbGps> result = new LinkedList<>();
        while (result.size() < num) {
            TbGps gps = queue.poll(timeout, tu);
            if (gps == null) {
                break;
            } else {
                gps.setCreatetime(Timestamp.valueOf(LocalDateTime.now()));
                result.add(gps);
            }
        }
        return result;
    }
}
