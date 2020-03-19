package com.yezhaokang.estimatejmeter.util.jmeterUtil;

import com.alibaba.fastjson.JSON;
import com.yezhaokang.estimatejmeter.module.pressure.entity.Sample;
import com.yezhaokang.estimatejmeter.util.redisUtil.RedisUtil;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

public class JmeterResultCollector extends ResultCollector {

    private RedisUtil redisUtil;

    private String identifyCode;

    public JmeterResultCollector(Summariser summer, String identifyCode, RedisUtil redisUtil) {
        super(summer);
        this.redisUtil=redisUtil;
        this.identifyCode=identifyCode;
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        super.sampleOccurred(e);
        SampleResult result = e.getResult();
        Sample sample=new Sample();
        sample.setIdentifyCode(identifyCode);
        sample.setThreadName(result.getThreadName());
        sample.setResponseCode(result.getResponseCode());
        sample.setResponseData(new String(result.getResponseData()));
        sample.setSuccess(result.isSuccessful()?"true":"false");
        sample.setStartTime(result.getStartTime());
        sample.setEndTime(result.getEndTime());
        sample.setElapsedTime(result.getTime());
        sample.setIdleTime(result.getIdleTime());
        sample.setLatency(result.getLatency());
        sample.setActiveCount(Thread.activeCount());
        String jsonStr= JSON.toJSONString(sample);
        redisUtil.lSet(identifyCode,jsonStr);
    }
}
