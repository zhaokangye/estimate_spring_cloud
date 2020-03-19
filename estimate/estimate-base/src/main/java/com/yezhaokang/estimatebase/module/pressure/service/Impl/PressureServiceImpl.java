package com.yezhaokang.estimatebase.module.pressure.service.Impl;

import com.alibaba.fastjson.JSONObject;

import com.yezhaokang.estimatebase.core.redis.RedisUtil;
import com.yezhaokang.estimatebase.core.shiro.ShiroKit;
import com.yezhaokang.estimatebase.module.management.entity.PageParam;
import com.yezhaokang.estimatebase.module.pressure.dao.ArgumentMapper;
import com.yezhaokang.estimatebase.module.pressure.dao.HeaderMapper;
import com.yezhaokang.estimatebase.module.pressure.dao.PressurePlanMapper;
import com.yezhaokang.estimatebase.module.pressure.entity.*;
import com.yezhaokang.estimatebase.module.pressure.service.PressureServiceFeign;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author 叶兆康
 * @date 2019-11-26 08:54:17
 */
@Service
public class PressureServiceImpl {

    @Autowired
    private ShiroKit shiroKit;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private PressurePlanMapper pressurePlanMapper;
    @Autowired
    private HeaderMapper headerMapper;
    @Autowired
    private ArgumentMapper argumentMapper;
    @Autowired
    private PressureServiceFeign pressureServiceFeign;

    public String initPressureTest(Integer planId) {
        PressureParams pressureParams=planDetail(planId);
        return pressureServiceFeign.initPressureTest(pressureParams);
    }

    public Map<String,Object> obtainPressureTestResult(PageParam pageParam){
        // 分页
        List<Sample> sampleList= JSONObject.parseArray(redisUtil.lGet(pageParam.getIdentifyCode(),pageParam.getPn(),pageParam.getPn()+pageParam.getSize()).toString(),Sample.class);
        // 总结果
        Sample beginSample=JSONObject.parseObject(redisUtil.lGetIndex(pageParam.getIdentifyCode(),0).toString(),Sample.class);
        Sample endSample=JSONObject.parseObject(redisUtil.lGetIndex(pageParam.getIdentifyCode(),-1).toString(),Sample.class);
        // 计算tps
        long ms=endSample.getEndTime()-beginSample.getStartTime();
        long listSize=redisUtil.lGetListSize(pageParam.getIdentifyCode());
        BigDecimal s=new BigDecimal(ms).divide(new BigDecimal(1000),2,ROUND_HALF_UP);
        BigDecimal tps=new BigDecimal(listSize).divide(s,2,ROUND_HALF_UP);
        // 计算false率
        int falseCount=0;
        for(Sample sample:sampleList){
            if (!sample.getSuccess().equals("true")){
                falseCount++;
            }
        }
        // 整合到返回map
        Map<String,Object> returnVal=new HashMap<>(2);
        returnVal.put("total",listSize);
        returnVal.put("sampleList",sampleList);
        returnVal.put("tps",listSize+" in "+s+"s="+tps+"/s");
        returnVal.put("falseRatio",falseCount+"/"+sampleList.size());
        return returnVal;
    }

    public List<PressurePlanEntity> obtainPressurePlan(){
        return pressurePlanMapper.obtainPressurePlanList(shiroKit.getId());
    }

    public boolean savePlan(PressureParams pressureParams){
        Integer userId=shiroKit.getId();
        // 记录压力测试计划
        PressurePlanEntity pressurePlanEntity=new PressurePlanEntity();
        BeanUtils.copyProperties(pressureParams,pressurePlanEntity);
        pressurePlanEntity.setCreateBy(userId);
        pressurePlanEntity.setCreateTime(new Date());
        pressurePlanMapper.insert(pressurePlanEntity);
        Integer planId=pressurePlanEntity.getId();
        // 记录请求头
        if(pressureParams.getHeaders()!=null){
            Iterator<Map.Entry<String,String>> headersIt=pressureParams.getHeaders().entrySet().iterator();
            while (headersIt.hasNext()){
                Map.Entry<String,String> entry=headersIt.next();
                HeaderEntity headerEntity=new HeaderEntity();
                headerEntity.setPlanId(planId);
                headerEntity.setHeaderKey(entry.getKey());
                headerEntity.setHeaderValue(entry.getValue());
                headerEntity.setCreateBy(userId);
                headerEntity.setCreateTime(new Date());
                headerMapper.insert(headerEntity);
            }
        }
        // 记录参数
        if(pressureParams.getArguments()!=null){
            Iterator<Map.Entry<String,Object>> argumentsIt=pressureParams.getArguments().entrySet().iterator();
            while (argumentsIt.hasNext()){
                Map.Entry<String,Object> entry=argumentsIt.next();
                ArgumentEntity argumentEntity=new ArgumentEntity();
                argumentEntity.setPlanId(planId);
                argumentEntity.setArgumentKey(entry.getKey());
                argumentEntity.setArgumentValue(entry.getValue().toString());
                argumentEntity.setCreateBy(userId);
                argumentEntity.setCreateTime(new Date());
                argumentMapper.insert(argumentEntity);
            }
        }
        return true;
    }

    public boolean deletePlan(Integer planId){
        pressurePlanMapper.deleteById(planId);
        return true;
    }

    public boolean editPlan(PressureParams pressureParams){
        Integer userId=shiroKit.getId();
        // 修改压力测试计划
        PressurePlanEntity pressurePlanEntity=new PressurePlanEntity();
        BeanUtils.copyProperties(pressureParams,pressurePlanEntity);
        pressurePlanEntity.setUpdateBy(userId);
        pressurePlanEntity.setUpdateTime(new Date());
        // todo port为null无法更新
        pressurePlanMapper.updateById(pressurePlanEntity);
        Integer planId=pressurePlanEntity.getId();
        // 修改请求头
        if(pressureParams.getHeaders()!=null){
            List<HeaderEntity> headerEntityList=pressurePlanMapper.obtainHeaders(planId);
            // 遍历参数中的请求头
            Iterator<Map.Entry<String,String>> headersIt=pressureParams.getHeaders().entrySet().iterator();
            while (headersIt.hasNext()){
                Boolean isHandle=false;
                Map.Entry<String,String> entry=headersIt.next();
                // 遍历数据库中的请求头
                Iterator<HeaderEntity> headerListIt=headerEntityList.iterator();
                while (headerListIt.hasNext()){
                    HeaderEntity headerEntity=headerListIt.next();
                    if(headerEntity.getHeaderKey().equals(entry.getKey())){
                        // key相同更新，在查询数据库得出的集合中删除
                        headerEntity.setHeaderValue(entry.getValue());
                        headerEntity.setUpdateBy(shiroKit.getId());
                        headerEntity.setUpdateTime(new Date());
                        headerMapper.updateById(headerEntity);
                        headerListIt.remove();
                        isHandle=true;
                        break;
                    }
                }
                if(!isHandle){
                    // 新增
                    HeaderEntity headerEntity=new HeaderEntity();
                    headerEntity.setPlanId(planId);
                    headerEntity.setHeaderKey(entry.getKey());
                    headerEntity.setHeaderValue(entry.getValue());
                    headerEntity.setCreateBy(shiroKit.getId());
                    headerEntity.setCreateTime(new Date());
                    headerMapper.insert(headerEntity);
                }
            }
            // 删除
            for (HeaderEntity headerEntity:headerEntityList){
                headerMapper.deleteById(headerEntity.getId());
            }
        }
        // 修改参数
        if(pressureParams.getArguments()!=null){
            List<ArgumentEntity> argumentEntityList=pressurePlanMapper.obtainArguments(planId);
            // 遍历参数中的请求头
            Iterator<Map.Entry<String,Object>> argumentsIt=pressureParams.getArguments().entrySet().iterator();
            while (argumentsIt.hasNext()){
                Boolean isHandle=false;
                Map.Entry<String,Object> entry=argumentsIt.next();
                // 遍历数据库中的请求头
                Iterator<ArgumentEntity> argumentListIt=argumentEntityList.iterator();
                while (argumentListIt.hasNext()){
                    ArgumentEntity argumentEntity=argumentListIt.next();
                    if(argumentEntity.getArgumentKey().equals(entry.getKey())){
                        // key相同更新，在查询数据库得出的集合中删除
                        argumentEntity.setArgumentValue(entry.getValue().toString());
                        argumentEntity.setUpdateBy(shiroKit.getId());
                        argumentEntity.setUpdateTime(new Date());
                        argumentMapper.updateById(argumentEntity);
                        argumentListIt.remove();
                        isHandle=true;
                        break;
                    }
                }
                if(!isHandle){
                    // 新增
                    ArgumentEntity argumentEntity=new ArgumentEntity();
                    argumentEntity.setPlanId(planId);
                    argumentEntity.setArgumentKey(entry.getKey());
                    argumentEntity.setArgumentValue(entry.getValue().toString());
                    argumentEntity.setCreateBy(shiroKit.getId());
                    argumentEntity.setCreateTime(new Date());
                    argumentMapper.insert(argumentEntity);
                }
            }
            // 删除
            for (ArgumentEntity argumentEntity:argumentEntityList){
                argumentMapper.deleteById(argumentEntity.getId());
            }
        }
        return true;
    }

    public PressureParams planDetail(Integer planId){
        // 获得压力测试计划并复制到压力测试参数
        PressureParams pressureParams=new PressureParams();
        PressurePlanEntity pressurePlanEntity=pressurePlanMapper.selectById(planId);
        BeanUtils.copyProperties(pressurePlanEntity,pressureParams);
        // 获得请求头
        Map<String,String> headers=new HashMap<>();
        List<HeaderEntity> headerEntityList=pressurePlanMapper.obtainHeaders(planId);
        for(HeaderEntity header:headerEntityList){
            headers.put(header.getHeaderKey(),header.getHeaderValue());
        }
        pressureParams.setHeaders(headers);
        // 获得参数
        Map<String,Object> arguments=new HashMap<>();
        List<ArgumentEntity> argumentEntityList=pressurePlanMapper.obtainArguments(planId);
        for(ArgumentEntity argument:argumentEntityList){
            arguments.put(argument.getArgumentKey(),argument.getArgumentValue());
        }
        pressureParams.setArguments(arguments);
        return pressureParams;
    }

}
