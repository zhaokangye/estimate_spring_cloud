package com.yezhaokang.estimatebase.module.pressure.controller;

import com.alibaba.fastjson.JSON;
import com.yezhaokang.estimatebase.core.base.controller.BaseController;
import com.yezhaokang.estimatebase.core.error.BussinessException;
import com.yezhaokang.estimatebase.core.error.EmBussinessError;
import com.yezhaokang.estimatebase.core.response.CommonReturnType;
import com.yezhaokang.estimatebase.module.management.entity.PageParam;
import com.yezhaokang.estimatebase.module.pressure.entity.PressureParams;
import com.yezhaokang.estimatebase.module.pressure.service.Impl.PressureServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * @author 叶兆康
 */
@RestController
@RequestMapping("/pressure")
public class PressureController extends BaseController {

    @Autowired
    private PressureServiceImpl pressureServiceImpl;

    /**
     * 压力测试计划
     * @return
     */
    @GetMapping("/plan/list")
    public CommonReturnType obtainPressurePlan(){
        return CommonReturnType.create(pressureServiceImpl.obtainPressurePlan());
    }

    /**
     * 保存
     * @param bindingResult
     * @return
     */
    @PostMapping("/plan")
    public CommonReturnType savePlan(@Valid PressureParams pressureParams, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }
        return CommonReturnType.create(pressureServiceImpl.savePlan(pressureParams));
    }

    /**
     * 删除
     * @param param
     * @return
     */
    @DeleteMapping("/plan")
    public CommonReturnType deletePlan(@RequestBody String param){
        Integer id=Integer.parseInt(JSON.parseObject(param).get("planId").toString());
        return CommonReturnType.create(pressureServiceImpl.deletePlan(id));
    }

    /**
     * 修改
     * @param pressureParams
     * @return
     */
    @PutMapping("/plan")
    public CommonReturnType editPlan(@Valid @RequestBody PressureParams pressureParams, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new BussinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }
        return CommonReturnType.create(pressureServiceImpl.editPlan(pressureParams));
    }

    /**
     * 查询
     * @param planId
     * @return
     */
    @GetMapping("/plan/{planId}")
    public CommonReturnType planDetail(@PathVariable Integer planId){
        return CommonReturnType.create(pressureServiceImpl.planDetail(planId));
    }

    /**
     * 启动压力测试
     * @param planId
     * @return
     */
    @GetMapping("/init/{planId}")
    public CommonReturnType initTest(@PathVariable Integer planId){
        return CommonReturnType.create(pressureServiceImpl.initPressureTest(planId));
    }

    /**
     * 压力测试结果
     * @param pageParam
     * @return
     */
    @PostMapping("/result")
    public CommonReturnType obtainPressureTestResult(@RequestBody PageParam pageParam){
        return CommonReturnType.create(pressureServiceImpl.obtainPressureTestResult(pageParam));
    }
}
