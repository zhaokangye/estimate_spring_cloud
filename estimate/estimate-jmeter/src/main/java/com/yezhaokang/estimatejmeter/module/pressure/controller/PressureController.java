package com.yezhaokang.estimatejmeter.module.pressure.controller;

import com.yezhaokang.estimatejmeter.module.pressure.entity.PressureParams;
import com.yezhaokang.estimatejmeter.module.pressure.service.PressureService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("estimate-jmeter-0.0.1-SNAPSHOT/pressure")
public class PressureController {

    @Autowired
    private PressureService pressureService;

    /**
     * 启动压力测试
     * @param pressureParams
     * @return
     */
    @PostMapping("/init")
    public String initTest(@RequestBody PressureParams pressureParams){
        Subject currentSubject=SecurityUtils.getSubject();
        if (currentSubject.isAuthenticated()){
            return pressureService.initPressureTest(pressureParams);
        }else {
            return null;
        }
    }
}
