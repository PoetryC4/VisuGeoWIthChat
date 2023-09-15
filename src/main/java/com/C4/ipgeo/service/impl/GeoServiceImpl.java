package com.C4.ipgeo.service.impl;

import com.C4.ipgeo.entity.Geo;
import com.C4.ipgeo.mapper.GeoMapper;
import com.C4.ipgeo.service.GeoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeoServiceImpl extends ServiceImpl<GeoMapper, Geo> implements GeoService {

    @Autowired
    GeoMapper geoMapper;


}
