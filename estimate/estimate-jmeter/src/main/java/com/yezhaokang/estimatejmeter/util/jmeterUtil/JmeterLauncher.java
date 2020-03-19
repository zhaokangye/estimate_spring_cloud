package com.yezhaokang.estimatejmeter.util.jmeterUtil;

import com.yezhaokang.estimatejmeter.module.pressure.entity.PressureParams;
import com.yezhaokang.estimatejmeter.util.redisUtil.RedisUtil;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;

public class JmeterLauncher {
    public void presureTest(RedisUtil redisUtil, PressureParams pressureParams){
        // 创建jmx脚本
        // JMeterKit.createScript(pressureParams);
        // jemter 引擎
        StandardJMeterEngine standardJMeterEngine = new StandardJMeterEngine();
        // 设置不适用gui的方式调用jmeter
        System.setProperty(JMeter.JMETER_NON_GUI, "true");
        // 设置jmeter.properties文件，我们将jmeter文件存放在resources中，通过classload
        String path = JmeterLauncher.class.getClassLoader().getResource("jmeter.properties").getPath();
        File jmeterPropertiesFile = new File(path);
        if (jmeterPropertiesFile.exists()) {
            JMeterUtils.setJMeterHome("");
            JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.getPath());
            HashTree testPlanTree = new HashTree();
            // 创建测试计划
            TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
            HashTree requestHashTree = new HashTree();
            requestHashTree.add(JmeterKit.createHTTPSamplerProxy(pressureParams), JmeterKit.createHeaderManager(pressureParams));
            testPlanTree.add(testPlan);
            HashTree threadGroupHashTree = testPlanTree.add(testPlan, JmeterKit.createThreadGroup(pressureParams));
            threadGroupHashTree.add(requestHashTree);

            //增加结果收集
            Summariser summer = null;
            String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
            if (summariserName.length() > 0) {
                summer = new Summariser(summariserName);
            }

            testPlanTree.add(testPlanTree.getArray()[0], new JmeterResultCollector(summer,pressureParams.getIdentifyCode(),redisUtil));
            // 配置jmeter
            standardJMeterEngine.configure(testPlanTree);
            // 运行
            standardJMeterEngine.run();
        }
    }
}
