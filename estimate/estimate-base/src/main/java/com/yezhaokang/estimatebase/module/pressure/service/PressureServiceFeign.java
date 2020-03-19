package com.yezhaokang.estimatebase.module.pressure.service;

import com.yezhaokang.estimatebase.module.pressure.entity.PressureParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 叶兆康
 */
@FeignClient(value = "estimate-jmeter-feign")
public interface PressureServiceFeign {

    @PostMapping("/pressure/init")
    String initPressureTest(@RequestBody PressureParams pressureParams);

}
