package com.C4.ipgeo;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@MapperScan("com.C4.ipgeo.mapper")
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class IPGeoApplication {
    public static void main(String[] args) {
        SpringApplication.run(IPGeoApplication.class,args);
        log.info("项目启动成功...端口8016");
    }
}