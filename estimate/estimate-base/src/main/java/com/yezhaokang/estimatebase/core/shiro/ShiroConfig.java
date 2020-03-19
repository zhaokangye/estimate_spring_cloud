package com.yezhaokang.estimatebase.core.shiro;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Filter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ShiroConfig {

    @Value("${session.redis.expireTime}")
    long expireTime;

    /**
     * 请求拦截
     * @param securityManager
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filtersMap = new HashMap<>();
        MyFilter authFilter = new MyFilter();
        filtersMap.put("authc", authFilter);
        shiroFilterFactoryBean.setFilters(filtersMap);
        // 拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setLoginUrl("/login");
        /*
         * 注意过滤器配置顺序 不能颠倒
         * anon. 配置不会被拦截的请求 顺序判断
         * authc. 配置拦截的请求
         * 配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
         * 所有非法请求被重定向到/unauth，该请求返回一个json数据
         * shiroFilterFactoryBean.setLoginUrl("/unauth");
         */
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * @Title: securityManager
     * @Description: SecurityManager，权限管理，这个类组合了登陆，登出，权限，session的处理
     * @return SecurityManager
     */
    @Bean
    public SecurityManager securityManager(RedisSessionDao redisSessionDao) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myRealm());
        securityManager.setSessionManager(sessionManager(redisSessionDao));
        return securityManager;
    }

    /**
     * 自定义认证
     * @Title: myShiroRealm
     * @Description: ShiroRealm，这是个自定义的认证类，继承自AuthorizingRealm，负责用户的认证和权限的处理
     * @return MyShiroRealm
     */
    @Bean
    public MyRealm myRealm() {
        MyRealm myShiroRealm = new MyRealm();
//        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    /**
     * 自定义sessionManager，用户的唯一标识，即Token或Authorization的认证
     */
    @Bean
    public SessionManager sessionManager(RedisSessionDao redisSessionDao) {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setGlobalSessionTimeout(expireTime*1000);
        mySessionManager.setDeleteInvalidSessions(true);
        mySessionManager.setSessionDAO(redisSessionDao);
        mySessionManager.setSessionValidationSchedulerEnabled(true);
        mySessionManager.setDeleteInvalidSessions(true);
        return mySessionManager;
    }

//    /**
//     * 密码凭证匹配器，作为自定义认证的基础 （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了 ）
//     *
//     * @return
//     */
//    @Bean
//    public HashedCredentialsMatcher hashedCredentialsMatcher() {
//        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
//        hashedCredentialsMatcher.setHashAlgorithmName("MD5");// 散列算法:这里使用MD5算法;
//        hashedCredentialsMatcher.setHashIterations(1024);// 散列的次数，比如散列两次，相当于 md5(md5(""));
//        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
//        return hashedCredentialsMatcher;
//    }

}