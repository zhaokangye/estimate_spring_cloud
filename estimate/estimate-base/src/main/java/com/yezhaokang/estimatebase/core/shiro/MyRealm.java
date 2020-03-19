package com.yezhaokang.estimatebase.core.shiro;

import com.yezhaokang.estimatebase.core.error.BussinessException;
import com.yezhaokang.estimatebase.core.error.EmBussinessError;
import com.yezhaokang.estimatebase.module.management.dao.UserMapper;
import com.yezhaokang.estimatebase.module.management.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    /**
     * 权限验证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println(principalCollection.getPrimaryPrincipal());
        return null;
    }

    /**
     * 登陆认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token=(UsernamePasswordToken)authenticationToken;
        Map<String,Object> param=new HashMap<>(1);
        param.put("userName",token.getUsername());
        List<User> users=userMapper.selectByMap(param);
        if(users.size()!=1){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return new SimpleAuthenticationInfo(token.getPrincipal(), users.get(0).getPassword(), getName());
    }
}