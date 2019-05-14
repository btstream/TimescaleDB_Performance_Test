package net.btstream.performance.test.db.bean;

import lombok.Data;

@Data
public class TbGps {

  private java.sql.Timestamp createtime;
  private java.sql.Timestamp sendtime;
  private String gpsId;
  private long version;
  private double latitude;
  private long latitudeflag;
  private double longtitude;
  private long longtitudeflag;
  private double altitude;
  private double speed;
  private double azimuth;
  private double totalmileage;
  private double singlemileage;
  private long cellid;
  private long lac;
  private long mcc;
  private long mnc;
  private double power;
  private double extpower;
  private long gpsnum;
}
