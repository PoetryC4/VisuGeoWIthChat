package com.C4.ipgeo.controller;


import com.C4.ipgeo.common.R;
import com.C4.ipgeo.entity.Geo;
import com.C4.ipgeo.service.GeoService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.C4.ipgeo.util.URLUtils.*;

@Slf4j
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    Boolean isPromptAdded = false;
    List<List<String>> history = new ArrayList<>();

    @GetMapping("/close")
    public R<Object> closeMonitor() {
        String interfaceUrl = "http://127.0.0.1:6066/close";
        String resStr = getContent(interfaceUrl,"GET");
        if(Objects.equals(resStr, "E")) {
            return R.error("错误");
        } else {
            return R.success("ok");
        }
    }

    @GetMapping("/open")
    public R<Object> startMonitor(String url) {
        log.info(url);
        //http://127.0.0.1:6066/url=www.baidu.com
        if(StringUtils.isBlank(url)) {
            return R.error("非法输入");
        }
        //String regex = "(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\&%\\+\\$#_=]*)?";
        /*String regex = "[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\'\\/\\\\&%\\+\\$#_=]*)?";

        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(url.trim());
        boolean result = mat.matches();
        if(!result) {
            return R.error("非法输入");
        }*/
        String interfaceUrl = "http://127.0.0.1:6066/url="+url;
        String resStr = getContent(interfaceUrl,"GET");
        if(Objects.equals(resStr, "E")) {
            return R.error("错误");
        } else {
            return R.success("成功");
        }
    }

    @GetMapping("/info/ips")
    public R<Object> getContentIPs() throws IOException {
        String interfaceUrl = "http://127.0.0.1:6066/info/hosts";
        String resStr = getContent(interfaceUrl,"GET");
        if(Objects.equals(resStr, "E")) {
            return R.error("错误");
        } else if (Objects.equals(resStr, "\"N\"\n")) {
            return R.success("N");
        } else {
            Map map = JSON.parseObject(resStr, Map.class);
            Integer count = (Integer) map.get("count");
            Map<String, Object> dict = (Map<String, Object>) map.get("result");
            if(dict == null) return R.success("N");
            List<String> urls = new ArrayList<>();
            List<String> ids = new ArrayList<>();

            for (String key : dict.keySet()) {
                urls.add((String) dict.get(key));
                ids.add(key);
            }
            HashMap<String, Object> res = new HashMap<>();
            List<String> Ips = new ArrayList<>();
            List<String> Hosts = new ArrayList<>();
            for (int i = 0; i < urls.size(); i++) {
                Ips.add(((List<String>)(getIps(urls.get(i)).get("ips"))).get(0));
                Hosts.add(((List<String>)(getIps(urls.get(i)).get("hosts"))).get(0));
            }
            res.put("ips",Ips);
            res.put("hosts", Hosts);
            res.put("ids", ids);
            return R.success(res);
        }
    }/*
    @GetMapping("/info/sec_judge")
    public R<Object> securityJudge(Integer id) throws IOException {
        //http://127.0.0.1:6066/info=1
        String interfaceUrl = "http://127.0.0.1:6066/info/sec_judge/id="+id;
        String resStr = getContent(interfaceUrl,"GET");
        if(Objects.equals(resStr, "E")) {
            return R.error("错误");
        } else if (Objects.equals(resStr, "\"N\"\n")) {
            return R.success("N");
        } else {
            return R.success(resStr);
        }
    }*/

    @GetMapping("/info/sec_judge")
    public R<Object> securityJudge(Integer id, String lct, String isp) throws IOException {
        //http://127.0.0.1:6066/info=1
        String interfaceUrl = "http://127.0.0.1:6066/info/get_by_id/id="+id;
        String resStr = getContent(interfaceUrl,"GET");
        if(Objects.equals(resStr, "E")) {
            return R.error("错误");
        } else if (Objects.equals(resStr, "\"N\"\n")) {
            return R.success("N");
        } else {
            Map map = JSON.parseObject(resStr, Map.class);

            String question = "现有一个<a>标签，其href属性为"+map.get("href")+",经过查询大概得知该链接IP所在地址为"+ lct +",其运营商为"+ isp +"，其文本内容为"+map.get("txt")+",请问该href链接是否危险?请将回答尽量限制在100字以内";
            //String question = "现有一个<a>标签，其href属性为"+map.get("href")+",其文本内容为"+map.get("txt")+",请问该href链接是否危险?请将回答尽量限制在100字以内";

            List<String> names = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            names.add("question");
            names.add("history");
            names.add("streaming");
            values.add(question);

            if(!this.isPromptAdded) {
                this.isPromptAdded = true;

                List<String> hist = new ArrayList<>();
                hist.add("现在已知大陆境外的网站大多都是不安全网站，尤其是在东南亚地区。包含成人内容或赌博内容的网站都是不安全的。在国内的网站，如百度，大多是安全的。在链接中含有gov,edu等后缀的链接是安全的。官方网站如中国教育网所运营的网站是安全的。如下是几个例子:");
                hist.add("好的。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为http://v.baidu.com/v?ct=301989888&rn=20&pn=0&db=0&s=25&ie=utf-8,经过查询大概得知该链接IP所在地址为中国北京市北京市,其运营商为百度，其文本内容为视频,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该href链接是安全的，因为该链接指向一个 Baidu 视频页面，而 Baidu 是一个知名的中文搜索引擎，其内容是安全的。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为http://www.baidu.com/more/,经过查询大概得知该链接IP所在地址为中国北京市北京市,其运营商为中国电信，其文本内容为更多,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该href链接是安全的，因为该链接指向一个百度网盘页面，而百度网盘是一个知名的网盘服务，其内容是安全的。但是，请注意在使用该链接时，请确保您已经成年，并遵守当地的法律法规。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为//account.bilibili.com/big,经过查询大概得知该链接IP所在地址为中国成都市四川省,其运营商为中国电信，其文本内容为大会员可畅享各种专属内容，还有游戏礼包、个性装扮等你来拿~,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该href链接是安全的，因为该链接指向一个 Bilibili 大会员专属页面，而 Bilibili 是一个知名的视频分享网站，其内容是安全的。但是，请注意在使用该链接时，请确保您已经成年，并遵守当地的法律法规。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为//member.bilibili.com/platform/upload/text/apply,经过查询大概得知该链接IP所在地址为中国重庆市重庆市,其运营商为中国电信，其文本内容为专栏投稿,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该href链接是安全的，因为该链接指向一个 Bilibili 专栏投稿页面，而 Bilibili 是一个知名的视频分享网站，其内容是安全的。但是，请注意在使用该链接时，请确保您已经成年，并遵守当地的法律法规。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为http://23.225.52.51:4466/vip100.html,经过查询大概得知该链接IP所在地址为中国中国香港中国香港,其运营商为BGP Consultancy Pte Ltd，其文本内容为,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该链接IP所在地址为中国中国香港，但无法确定其是否安全，因为安全与否取决于网站本身和用户访问的网站内容。建议谨慎访问此类网站，并使用安全的浏览器和网络连接。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为http://103.143.28.78:8898/840uu.html,经过查询大概得知该链接IP所在地址为中国中国香港中国香港,其运营商为OCEAN NETWORK LIMITED，其文本内容为,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该链接IP地址为中国中国香港，文本内容末知，无法确定其安全性。如果链接中包含敏感信息或存在恶意代码，则有可能存在安全风险。建议谨慎使用并检查链接内容。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为javascript:;,经过查询大概得知该链接IP所在地址为印度尼西亚雅加达雅加达null,其运营商为Facebook, Inc.，其文本内容为\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        ",请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该链接IP地址为美国达拉斯德克萨斯州，文本内容末知，无法确定其安全性。如果链接中包含敏感信息或存在恶意代码，则有可能存在安全风险。建议谨慎访问此类网站，并使用安全的浏览器和网络连接。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为http://grin.oops.jp/,经过查询大概得知该链接IP所在地址为美国旧金山加利福尼亚州null,其文本内容为,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该链接IP地址为美国旧金山加利福尼亚州，无法确定其是否安全，因为安全与否取决于网站本身和用户访问的网站内容。建议谨慎访问此类网站，并使用安全的浏览器和网络连接。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为http://hq.uestc.edu.cn/,经过查询大概得知该链接IP所在地址为中国成都市四川省null,其文本内容为,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该href链接是安全的，因为域名uestc.edu.cn是中国电子科技大学的域名");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为,经过查询大概得知该链接IP所在地址为中国中国香港中国香港null,其文本内容为\n" +
                        "      今日最新域名\n" +
                        "https://a153tom.com\n" +
                        "\n" +
                        "      今日备用域名\n" +
                        "      \n" +
                        "https://a153tom.com\n" +
                        "\n" +
                        "      提示：请复制域名去手机浏览器中打开，请勿在微信和QQ中打开，因为包含成人内容。尽量使用谷歌浏览器，火狐浏览器，或者苹果安卓系统自带浏览器访问～\n" +
                        "      \n" +
                        "      本站由于经常被屏蔽域名，请务必多保存地址，以防丢失本站。 \n" +
                        "      如果您觉得本站还不错，可以粘贴本站链接发送给好友，感谢～\n" +
                        "    ,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该链接IP地址为中国中国香港，文本内容包含成人内容，且网站常被屏蔽域名，因此可能存在安全风险。建议谨慎使用并检查链接内容。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("现有一个<a>标签，其href属性为javascript:void(0),经过查询大概得知该链接IP所在地址为中国天津市天津市,其文本内容为 ,请问该href链接是否危险?请将回答尽量限制在100字以内");
                hist.add("该href链接是安全的，其链接中含有gov字眼，对应政府网站。");
                this.history.add(hist);

                hist = new ArrayList<>();
                hist.add("请不要过度依赖链接IP所在地址对安全性进行判断。");
                hist.add("好的。");
                this.history.add(hist);
            }

            values.add(this.history);
            values.add(false);

            String output = null;
            Map map2 = null;

            try {
                output = RequestWithBody("http","127.0.0.1","7891","chat",names,values);
                map2 = JSON.parseObject(output, Map.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return R.success(map2.get("response"));
        }
    }
}
