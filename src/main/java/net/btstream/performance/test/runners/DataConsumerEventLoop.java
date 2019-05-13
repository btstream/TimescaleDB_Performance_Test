package net.btstream.performance.test.runners;

import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.db.mapper.GpsMapper;
import net.btstream.performance.test.utils.Statistics;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class DataConsumerEventLoop extends EventLoop {

    private final BlockingQueue<TbGps> DATA_QUEUE;
    private final Statistics STA = new Statistics();

    private int batchSize = 20;
    private long pollTimeout = 200;


    private GpsMapper gpsMapper;

    public DataConsumerEventLoop(BlockingQueue<TbGps> queue, EventLoopGroup eventLoopGroup) {
        super(eventLoopGroup);
        this.DATA_QUEUE = queue;
    }

    public void setGpsMapper(GpsMapper gpsMapper) {
        this.gpsMapper = gpsMapper;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setPollTimeout(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    @Override
    public void execute() {
        try {
            List<TbGps> record = new LinkedList<>();
            while (record.size() < batchSize) {
                // 超时之后直接跳出，直接入库
                TbGps data = DATA_QUEUE.poll(pollTimeout, TimeUnit.MICROSECONDS);
                if (data == null) {
                    break;
                } else {
                    data.setCreatetime(Timestamp.valueOf(LocalDateTime.now()));
                    record.add(data);
                }
            }
            if (record.size() > 0) {
                saveData(record);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveData(List<TbGps> data) {
        for (TbGps gps : data) {
            this.gpsMapper.insert(gps);
        }
        STA.increament(data.size());
    }

}
