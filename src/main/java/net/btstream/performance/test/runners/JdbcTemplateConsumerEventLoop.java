package net.btstream.performance.test.runners;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.utils.PollUtils;
import net.btstream.performance.test.utils.StatUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JdbcTemplateConsumerEventLoop extends EventLoop {

    private final BlockingQueue<TbGps> DATA_QUEUE;
    private final StatUtils STA = new StatUtils();

    @Setter
    private int batchSize = 20;

    @Setter
    private long pollTimeout = 200;

    @Setter
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateConsumerEventLoop(EventLoopGroup eventLoopGroup, BlockingQueue<TbGps> queue) {
        super(eventLoopGroup);
        this.DATA_QUEUE = queue;
    }

    @Override
    public void execute() {
        try {
            List<TbGps> records = PollUtils.pollRecords(DATA_QUEUE, batchSize, pollTimeout, TimeUnit.MILLISECONDS);
            if (records.size() > 0) {
                saveDataWithJdbcTemplate(records);
            }
        } catch (InterruptedException e) {
            log.info("{}", e.getMessage());
        }
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
            throw e;
        }
        STA.increament(data.size());
    }
}
