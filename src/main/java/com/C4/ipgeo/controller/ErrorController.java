package com.C4.ipgeo.controller;


import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class ErrorController extends BasicErrorController {

    private static Log log = LogFactory.getLog(ErrorController.class);

    public ErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    /**
     * 覆盖默认的Json响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        // 获取原始的错误信息
        //Map<String, Object> body = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
        HttpStatus status = getStatus(request);


        Map<String, Object> result = new HashMap<>();
        String code = null;
        String message = null;
        Object data = null;

        // 设置自定义的错误信息，或者从ThreadLocal等获取错误信息

        result.put("code", code);
        result.put("message", message);
        result.put("data", data);
        //cleanAllCache();
        return new ResponseEntity<Map<String, Object>>(result, status);
    }


}
