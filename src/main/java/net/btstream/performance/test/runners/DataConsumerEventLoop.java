package net.btstream.performance.test.runners;

import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.db.mapper.GpsMapper;
import net.btstream.performance.test.utils.Statistics;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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


    // mybatis mapper
    private GpsMapper gpsMapper;

    // jdbc template
    private JdbcTemplate jdbcTemplate;

    private boolean useJdbcTemplate = false;

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

    public void setUseJdbcTemplate() {
        this.useJdbcTemplate = true;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
                if (!useJdbcTemplate) {
                    saveData(record);
                } else {
                    saveDataWithJdbcTemplate(record);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    void saveData(List<TbGps> data) {
        for (Object gps : data) {
            gpsMapper.insert((TbGps) gps);
        }
        STA.increament(data.size());
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    void saveDataWithJdbcTemplate(List<TbGps> data) {
        try {
            jdbcTemplate.batchUpdate("insert into tb_gps values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    preparedStatement.setTimestamp(1, data.get(i).getCreatetime());
                    preparedStatement.setTimestamp(2, data.get(i).getSendtime());
                    preparedStatement.setString(3, data.get(i).getGpsId());
                    preparedStatement.setLong(4, data.get(i).getVersion());
                    preparedStatement.setDouble(5, data.get(i).getLatitude());
                    preparedStatement.setLong(6, data.get(i).getLatitudeflag());
                    preparedStatement.setDouble(7, data.get(i).getLongtitude());
                    preparedStatement.setLong(8, data.get(i).getLongtitudeflag());
                    preparedStatement.setDouble(9, data.get(i).getAltitude());
                    preparedStatement.setDouble(10, data.get(i).getSpeed());
                    preparedStatement.setDouble(11, data.get(i).getAzimuth());
                    preparedStatement.setDouble(12, data.get(i).getTotalmileage());
                    preparedStatement.setDouble(13, data.get(i).getSinglemileage());
                    preparedStatement.setLong(14, data.get(i).getCellid());
                    preparedStatement.setLong(15, data.get(i).getLac());
                    preparedStatement.setLong(16, data.get(i).getMcc());
                    preparedStatement.setLong(17, data.get(i).getMnc());
                    preparedStatement.setDouble(18, data.get(i).getPower());
                    preparedStatement.setDouble(19, data.get(i).getExtpower());
                    preparedStatement.setDouble(20, data.get(i).getGpsnum());
                }

                @Override
                public int getBatchSize() {
                    return data.size();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        STA.increament(data.size());
    }

}
