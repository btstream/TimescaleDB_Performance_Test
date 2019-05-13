package net.btstream.performance.test.runners;

import net.btstream.performance.test.db.bean.TbGps;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;

/**
 * 数据生成工具
 */
public class DataGeneratEventLoop extends EventLoop {

    /**
     * 数据队列
     */
    private final BlockingQueue<TbGps> DATA_QUEUE;

    private int idRangeMin = 0;
    private int idRangeMax = 20000;
    private int idPrefix = 100000;

    public DataGeneratEventLoop(BlockingQueue<TbGps> dataQueue, EventLoopGroup eventLoopGroup) {
        super(eventLoopGroup);
        this.DATA_QUEUE = dataQueue;
        eventLoopGroup.submit(this);
    }

    /**
     * 设置生成id的前缀
     *
     * @param prefix
     */
    public void setIdPrefix(int prefix) {
        this.idPrefix = prefix;
    }

    /**
     * 设置生成id的最大范围
     *
     * @param maxId
     */
    public void setIdRange(int minId, int maxId) {
        this.idRangeMin = minId;
        this.idRangeMax = maxId;
    }

    @Override
    public void execute() {

        // 生成数据
        for (int i = idRangeMin; i <= idRangeMax; i++) {
            TbGps gpsData = new TbGps();
            gpsData.setGpsId(String.format("%d", this.idPrefix * 10000 + i));
            // 生成GPS坐标数据
            gpsData.setLatitude(90 * Math.random());
            gpsData.setLongtitude(180 * Math.random());
            gpsData.setAltitude(1500 * Math.random());
            gpsData.setAzimuth(180 * Math.random());
            gpsData.setCellid(0);
            gpsData.setGpsnum((long) (24 * Math.random()));
            // 发送时间
            gpsData.setSendtime(Timestamp.valueOf(LocalDateTime.now()));

            try {
                DATA_QUEUE.put(gpsData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
