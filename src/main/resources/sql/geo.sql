create table geo_data(
    geo_id int(11) primary key AUTO_INCREMENT,
    ipv4 varchar(20) not null ,
    address varchar(50) null ,
    lat varchar(20) null ,
    lng varchar(20) null ,
    radius varchar(20) null ,
    prov varchar(20) null ,
    city varchar(20) null ,
    district varchar(20) null ,
    continent varchar(16) null ,
    country varchar(20) null ,
    consistency varchar(3) null ,
    correctness varchar(3) null,
    owner varchar(50) null,
    isp varchar(100) null,
    zipcode varchar(8) null,
    timezone varchar(20) null,
    accuracy varchar(10) null,
    `source` varchar(20) null,
    area_code varchar(5) null,
    as_number varchar(8) null,
    ad_code varchar(6) null
);

insert into geo_data values (1,'110.242.68.66',null,'38.879992','115.481957','121.5205','河北省','保定市',null,'亚洲','中国',null,null,'中国联通','中国联通'
,'071000','UTC+8','城市','数据挖掘','CN','4837','130600');