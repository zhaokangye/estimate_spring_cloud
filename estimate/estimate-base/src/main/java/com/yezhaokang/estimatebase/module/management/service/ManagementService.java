package com.yezhaokang.estimatebase.module.management.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.yezhaokang.estimatebase.core.error.BussinessException;
import com.yezhaokang.estimatebase.core.error.EmBussinessError;
import com.yezhaokang.estimatebase.core.shiro.ShiroKit;
import com.yezhaokang.estimatebase.module.management.dao.ServerMapper;
import com.yezhaokang.estimatebase.module.management.dao.UserMapper;
import com.yezhaokang.estimatebase.module.management.entity.Server;
import com.yezhaokang.estimatebase.module.management.entity.User;
import com.yezhaokang.estimatebase.util.Common;
import com.yezhaokang.estimatebase.util.Const;
import com.yezhaokang.estimatebase.util.Ftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author 叶兆康
 */
@Service
public class ManagementService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ServerMapper serverMapper;
    @Resource
    private ShiroKit shiroKit;

    /**
     * 添加用户
     * @param userName
     * @param password
     * @return
     */
    public boolean addUser(String userName,String password){
        User user=new User();
        user.setUserName(userName);
        user.setPassword(password);
        userMapper.insert(user);
        return true;
    }

    /**
     * 添加服务器记录
     * @param server
     * @return
     */
    public boolean addServer(Server server){
        Integer userId=shiroKit.getId();
        server.setCreateBy(userId);
        server.setCreateTime(new Date());
        server.setId(null);
        server.setStsCd(null);
        serverMapper.insert(server);
        return true;
    }

    /**
     * 删除服务器记录
     * @param serverId
     * @return
     */
    public boolean deleteServer(Integer serverId){
        serverMapper.deleteById(serverId);
        return true;
    }

    /**
     * 编辑服务器
     * @param server
     * @return
     */
    public boolean editServer(Server server){
        server.setUpdateBy(shiroKit.getId());
        server.setUpdateTime(new Date());
        serverMapper.updateById(server);
        return true;
    }

    /**
     * 获得无密码的服务器记录
     * @param serverId
     * @return
     */
    public Server serverDetail(Integer serverId){
        Server server=serverMapper.selectById(serverId);
        server.setPassword(null);
        return server;
    }

    public Server serverFullDetail(String host){
        Integer userId=shiroKit.getId();
        Server server=serverMapper.selectByHost(userId,host);
        if(server==null){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return server;
    }

    public List<Server> selectServerList(){
        Integer userId=shiroKit.getId();
        List<Server> serverList=serverMapper.selectServerListByUserId(userId);
        for(Server server:serverList){
            server.setPassword(null);
        }
        return serverList;
    }

    public Page<Server> selectServerList(Page<Server> page){
        List<Server> serverList=serverMapper.selectServerList(page,shiroKit.getId());
        for(Server server:serverList){
            server.setPassword(null);
        }
        return page.setRecords(serverList);
    }

    public Boolean testConnection(Integer serverId) throws Exception {
        Server server=serverMapper.selectById(serverId);
        if(Ftp.getFtpUtil(server).isSession()){
            StringBuilder commadResult=Ftp.getFtpUtil(server).execCommad(Const.HOST_CONF);
            String[] confs=commadResult.toString().split("\n");
            for(String conf:confs){
                if(conf.startsWith(Const.CPU_CORES)){
                    server.setCpuCores(Common.replaceBlank(conf).split(":")[1]);
                }
                if(conf.startsWith(Const.MODEL_NAME)){
                    server.setModelName(Common.replaceBlank(conf).split(":")[1]);
                }
                if(conf.startsWith(Const.MEM_TOTAL)){
                    server.setMemTotal(Common.replaceBlank(conf).split(":")[1]);
                }
            }
            serverMapper.updateById(server);
        };
        return true;
    }
}
