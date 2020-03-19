package com.yezhaokang.estimatebase.module.monitor.controller;

import com.yezhaokang.estimatebase.core.response.CommonReturnType;
import com.yezhaokang.estimatebase.module.monitor.service.impl.MonitorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 叶兆康
 */
@RestController()
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorServiceImpl monitorService;

    @PostMapping("/isInstall")
    public CommonReturnType isInstall(@RequestParam String host){
        return CommonReturnType.create(monitorService.isInstall(host));
    }

    @PostMapping("/installMonitor")
    public CommonReturnType installMonitor(@RequestParam String host){
        return CommonReturnType.create(monitorService.installMonitor(host));
    }

    @PostMapping("/uninstallMonitor")
    public CommonReturnType uninstallMonitor(@RequestParam String host){
        return CommonReturnType.create(monitorService.uninstallMonitor(host));
    }

    @PostMapping("/updateStats")
    public CommonReturnType updateStats(@RequestParam String host){
        return CommonReturnType.create(monitorService.updateStats(host));
    }

}
