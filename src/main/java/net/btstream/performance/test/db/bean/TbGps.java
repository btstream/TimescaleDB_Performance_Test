package net.btstream.performance.test.db.bean;


public class TbGps {

  private java.sql.Timestamp createtime;
  private java.sql.Timestamp sendtime;
  private String gpsId;
  private long version;
  private double latitude;
  private String latitudeflag;
  private double longtitude;
  private String longtitudeflag;
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


  public java.sql.Timestamp getCreatetime() {
    return createtime;
  }

  public void setCreatetime(java.sql.Timestamp createtime) {
    this.createtime = createtime;
  }


  public java.sql.Timestamp getSendtime() {
    return sendtime;
  }

  public void setSendtime(java.sql.Timestamp sendtime) {
    this.sendtime = sendtime;
  }


  public String getGpsId() {
    return gpsId;
  }

  public void setGpsId(String gpsId) {
    this.gpsId = gpsId;
  }


  public long getVersion() {
    return version;
  }

  public void setVersion(long version) {
    this.version = version;
  }


  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }


  public String getLatitudeflag() {
    return latitudeflag;
  }

  public void setLatitudeflag(String latitudeflag) {
    this.latitudeflag = latitudeflag;
  }


  public double getLongtitude() {
    return longtitude;
  }

  public void setLongtitude(double longtitude) {
    this.longtitude = longtitude;
  }


  public String getLongtitudeflag() {
    return longtitudeflag;
  }

  public void setLongtitudeflag(String longtitudeflag) {
    this.longtitudeflag = longtitudeflag;
  }


  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }


  public double getSpeed() {
    return speed;
  }

  public void setSpeed(double speed) {
    this.speed = speed;
  }


  public double getAzimuth() {
    return azimuth;
  }

  public void setAzimuth(double azimuth) {
    this.azimuth = azimuth;
  }


  public double getTotalmileage() {
    return totalmileage;
  }

  public void setTotalmileage(double totalmileage) {
    this.totalmileage = totalmileage;
  }


  public double getSinglemileage() {
    return singlemileage;
  }

  public void setSinglemileage(double singlemileage) {
    this.singlemileage = singlemileage;
  }


  public long getCellid() {
    return cellid;
  }

  public void setCellid(long cellid) {
    this.cellid = cellid;
  }


  public long getLac() {
    return lac;
  }

  public void setLac(long lac) {
    this.lac = lac;
  }


  public long getMcc() {
    return mcc;
  }

  public void setMcc(long mcc) {
    this.mcc = mcc;
  }


  public long getMnc() {
    return mnc;
  }

  public void setMnc(long mnc) {
    this.mnc = mnc;
  }


  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
  }


  public double getExtpower() {
    return extpower;
  }

  public void setExtpower(double extpower) {
    this.extpower = extpower;
  }


  public long getGpsnum() {
    return gpsnum;
  }

  public void setGpsnum(long gpsnum) {
    this.gpsnum = gpsnum;
  }

}
