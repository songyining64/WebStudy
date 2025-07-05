package com.cupk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.UserAssessment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAssessmentMapper extends BaseMapper<UserAssessment> {
    /**
     * 分页查询用户评估问卷，支持按用户名和问卷标题搜索
     * 
     * @param page    分页参数
     * @param keyword 关键词（用户名或问卷标题）
     * @return 分页结果
     */
    IPage<UserAssessment> pageUserAssessments(Page<UserAssessment> page, @Param("keyword") String keyword);
}