package com.C4.ipgeo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "geo_data")
public class Geo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "geo_id", type = IdType.AUTO)
    private Integer geoId;
    private String ipv4;
    private String address;
    private String lat;
    private String lng;
    private String radius;
    private String prov;
    private String city;
    private String district;
    private String continent;
    private String country;
    private String consistency;
    private String correctness;
    private String owner;
    private String isp;
    private String zipcode;
    private String timezone;
    private String accuracy;
    private String source;
    private String areaCode;
    private String asNumber;
    private String adCode;
}
