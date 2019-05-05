package net.btstream.performance.test.timescaledb;

import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.db.mapper.GpsMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimescaledbApplicationTests {

//    @Test
//    public void contextLoads() {
//    }

    @Autowired
    GpsMapper gpsMapper;

    @Test
    public void testSelectGps() {
        List<TbGps> allGpsData = gpsMapper.selectList(null);
        System.out.println(allGpsData.size());
    }

}
