package com.yezhaokang.estimatebase.core.shiro;

import com.yezhaokang.estimatebase.core.error.BussinessException;
import com.yezhaokang.estimatebase.core.error.EmBussinessError;
import com.yezhaokang.estimatebase.module.management.dao.UserMapper;
import com.yezhaokang.estimatebase.module.management.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShiroKit {

    @Autowired
    private UserMapper userMapper;

    public Integer getId(){
        Subject subject = SecurityUtils.getSubject();
        String userName=(String) subject.getPrincipal();
        User user=userMapper.selectByName(userName);
        if(user==null){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return user.getId();
    }
}
