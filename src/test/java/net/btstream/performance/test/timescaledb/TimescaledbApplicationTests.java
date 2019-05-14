package net.btstream.performance.test.timescaledb;

import net.btstream.performance.test.db.bean.TbGps;
import net.btstream.performance.test.db.mapper.GpsMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TimescaledbApplicationTests {

//    @Test
//    public void contextLoads() {
//    }

    @Autowired
    GpsMapper gpsMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testSelectGps() {
        Integer totalNumber = jdbcTemplate.queryForObject("select count(*) from tb_gps", Integer.class);
        System.out.println(totalNumber);
    }

    @Test
    public void getMembers() throws IllegalAccessException {
        Field[] s = TbGps.class.getDeclaredFields();
        TbGps t = new TbGps();
        for (Field f : s) {
            f.setAccessible(true);
            System.out.println(f.getName() + ":" + f.get(t));
        }
    }
}
