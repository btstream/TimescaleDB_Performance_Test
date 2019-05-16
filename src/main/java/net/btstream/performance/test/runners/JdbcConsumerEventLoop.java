package net.btstream.performance.test.runners;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.utils.PollUtils;
import net.btstream.performance.test.utils.StatUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JdbcConsumerEventLoop extends EventLoop {

    private final BlockingQueue<TbGps> DATA_QUEUE;
    private final StatUtils STA = new StatUtils();

    @Setter
    private DataSource dataSource;

    @Setter
    private int batchSize = 20;

    @Setter
    private long pollTimeout = 200;

    public JdbcConsumerEventLoop(EventLoopGroup eventLoopGroup, BlockingQueue<TbGps> queue) {
        super(eventLoopGroup);
        this.DATA_QUEUE = queue;
    }

    @Override
    public void execute() {
        try {
            List<TbGps> records = PollUtils.pollRecords(DATA_QUEUE, batchSize, pollTimeout, TimeUnit.MILLISECONDS);
            if (records.size() > 0) {
                save(records);
            }
        } catch (InterruptedException e) {
            log.info("{}", e.getMessage());
        } catch (SQLException e) {
            log.error("{}", e.getMessage(), e);
        }
    }

    public void save(List<TbGps> record) throws SQLException {
        Connection con = null;
        PreparedStatement preparedStatement = null;
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("insert into " +
                    "tb_gps " +
                    "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            con.setAutoCommit(false);
            for (TbGps data : record) {
                preparedStatement.setTimestamp(1, data.getCreatetime());
                preparedStatement.setTimestamp(2, data.getSendtime());
                preparedStatement.setString(3, data.getGpsId());
                preparedStatement.setLong(4, data.getVersion());
                preparedStatement.setDouble(5, data.getLatitude());
                preparedStatement.setLong(6, data.getLatitudeflag());
                preparedStatement.setDouble(7, data.getLongtitude());
                preparedStatement.setLong(8, data.getLongtitudeflag());
                preparedStatement.setDouble(9, data.getAltitude());
                preparedStatement.setDouble(10, data.getSpeed());
                preparedStatement.setDouble(11, data.getAzimuth());
                preparedStatement.setDouble(12, data.getTotalmileage());
                preparedStatement.setDouble(13, data.getSinglemileage());
                preparedStatement.setLong(14, data.getCellid());
                preparedStatement.setLong(15, data.getLac());
                preparedStatement.setLong(16, data.getMcc());
                preparedStatement.setLong(17, data.getMnc());
                preparedStatement.setDouble(18, data.getPower());
                preparedStatement.setDouble(19, data.getExtpower());
                preparedStatement.setDouble(20, data.getGpsnum());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
            con.commit();
            STA.increament(record.size());
        } catch (Exception e) {
            e.printStackTrace();
            if (con != null) {
                con.rollback();
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

}
