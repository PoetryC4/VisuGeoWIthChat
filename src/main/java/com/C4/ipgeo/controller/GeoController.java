package com.C4.ipgeo.controller;


import com.C4.ipgeo.common.R;
import com.C4.ipgeo.entity.Geo;
import com.C4.ipgeo.service.GeoService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.C4.ipgeo.util.URLUtils.getContent;

@Slf4j
@RestController
@RequestMapping("/geo")
public class GeoController {

    private final String key_aiwen = "w0NVqjC5TRIqzh63gt3hEIxIdZbr8e4H7bV5zb81hx6VgHMi9KBY0a8Hj7FPnvM4";
    @Autowired
    private GeoService geoService;

    @GetMapping("/data")
    public R<Object> getIpData(String ipv4) throws IOException {

        LambdaQueryWrapper<Geo> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Geo::getIpv4,ipv4);

        String url_aiwen = "https://api.ipplus360.com/ip/geo/v1/city/?key=" + key_aiwen + "&ip=" + ipv4 + "&lang=&coordsys=WGS84";
        log.info(url_aiwen);
        String resStr = getContent(url_aiwen,"GET");
        if(Objects.equals(resStr, "E")) {
            return R.success(geoService.getOne(queryWrapper));
        } else {
            //log.info(resStr);
            Map map = JSON.parseObject(resStr, Map.class);

            map = (Map) map.get("data");

            Geo geo = new Geo();
            geo.setAccuracy((String) map.get("accuracy"));
            geo.setCity((String) map.get("city"));
            geo.setAddress((String) map.get("address"));
            geo.setConsistency((String) map.get("consistency"));
            geo.setContinent((String) map.get("continent"));
            geo.setCorrectness((String) map.get("correctness"));
            geo.setCountry((String) map.get("country"));
            geo.setIsp((String) map.get("isp"));
            geo.setAreaCode((String) map.get("areacode"));
            geo.setAsNumber((String) map.get("asnumber"));
            geo.setDistrict((String) map.get("district"));
            geo.setIpv4(ipv4);
            geo.setLat((String) map.get("lat"));
            geo.setLng((String) map.get("lng"));
            geo.setOwner((String) map.get("owner"));
            geo.setProv((String) map.get("prov"));
            geo.setRadius((String) map.get("radius"));
            geo.setTimezone((String) map.get("timezone"));
            geo.setZipcode((String) map.get("zipcode"));
            geo.setSource((String) map.get("source"));
            geo.setAdCode((String) map.get("adcode"));

            if(geoService.count(queryWrapper) == 0) {
                geoService.save(geo);
            } else {
                geo.setGeoId(geoService.getOne(queryWrapper).getGeoId());
                geoService.updateById(geo);
            }
            return R.success(geo);
        }

    }
}
