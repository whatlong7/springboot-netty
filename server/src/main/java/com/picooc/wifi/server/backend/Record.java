package com.picooc.wifiservice.backend;

/**
 * Created by Shawn Tien on 3/11/16.
 */
public class Record {
    /*
     * 即将进入队列的对象
     */
    private String mac;
    private Long time;
    private Double weight;
    private Integer resistance;
    private Integer is_normal;
    private String domain;

    public Record(String mac, Long time, Double weight, Integer resistance, Integer is_normal) {
        this.mac = mac;
        this.time = time;
        this.weight = weight;
        this.resistance = resistance;
        this.is_normal = is_normal;
    }
    
    public Record(String mac, Long time, Double weight, Integer resistance, Integer is_normal, String domain) {
        this.mac = mac;
        this.time = time;
        this.weight = weight;
        this.resistance = resistance;
        this.is_normal = is_normal;
        this.domain = domain;
    }

    public void revise() {
        // 当称重时间大于当前时间时, 修正为当前时间
        Long nowUnixTime = System.currentTimeMillis() / 1000L;

        if ((this.time > nowUnixTime || this.time == 0) && this.is_normal == 1) {
            this.time = nowUnixTime;
        }
    }

    public boolean isValid() {
        Long nowUnixTime = System.currentTimeMillis() / 1000L;

        // 历史数据「称重时间」大于当前时间: 舍弃
        if (this.is_normal == 0) {
            if (this.time > nowUnixTime || this.time == 0) {
                return false;
            }
        }

        return true;
    }

    public String getMac() {
        return mac;
    }

    public Long getTime() {
        return time;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getResistance() {
        return resistance;
    }

    public Integer getIsNormal() {
        return is_normal;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
    
    
}
