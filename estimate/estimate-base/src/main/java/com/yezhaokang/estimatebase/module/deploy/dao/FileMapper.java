package com.yezhaokang.estimatebase.module.deploy.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yezhaokang.estimatebase.module.deploy.entity.FileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * @author 叶兆康
 */
@Mapper
public interface FileMapper extends BaseMapper<FileEntity> {

    FileEntity selectByPath(@Param("path") String path);

    List<FileEntity> selectFileList(@Param("createBy") Integer createBy);
}
