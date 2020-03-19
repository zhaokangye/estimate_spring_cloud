package com.yezhaokang.estimatebase.module.management.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yezhaokang.estimatebase.module.management.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 叶兆康
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByName(@Param("userName") String userName);

}
