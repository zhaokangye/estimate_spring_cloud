package com.yezhaokang.estimatejmeter.module.pressure.service.impl;

import com.yezhaokang.estimatejmeter.module.pressure.entity.PressureParams;
import com.yezhaokang.estimatejmeter.module.pressure.service.PressureService;
import com.yezhaokang.estimatejmeter.util.jmeterUtil.JmeterLauncher;
import com.yezhaokang.estimatejmeter.util.redisUtil.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PressureServiceImpl implements PressureService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String initPressureTest(PressureParams pressureParams) {
        // 删除重复key
        redisUtil.del(pressureParams.getIdentifyCode());
        // 启动压力测试
        JmeterLauncher jmeterLauncher=new JmeterLauncher();
        jmeterLauncher.presureTest(redisUtil,pressureParams);
        return pressureParams.getIdentifyCode();
    }
}
