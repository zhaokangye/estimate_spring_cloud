package com.yezhaokang.estimatejmeter.module.pressure.service;

import com.yezhaokang.estimatejmeter.module.pressure.entity.PressureParams;

public interface PressureService {

    /**
     * 启动压力测试
     * @param pressureParams
     * @return
     */
    String initPressureTest(PressureParams pressureParams);
}
