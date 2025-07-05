package com.cupk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.UserAssessment;

import java.util.List;
import java.util.Map;

public interface UserAssessmentService extends IService<UserAssessment> {
    /**
     * 保存用户评估结果
     * 
     * @param assessment 评估结果实体
     * @return 是否保存成功
     */
    boolean saveAssessment(UserAssessment assessment);

    /**
     * 获取用户的评估记录（分页）
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 评估记录分页结果
     */
    IPage<UserAssessment> getUserAssessments(Long userId, int page, int size);

    /**
     * 获取用户的最新评估结果
     * 
     * @param userId 用户ID
     * @return 最新的评估结果
     */
    UserAssessment getLatestAssessment(Long userId);

    /**
     * 生成评估报告
     * 
     * @param assessmentId 评估ID
     * @return 生成的报告内容
     */
    String generateReport(Long assessmentId);

    /**
     * 获取评估结果统计数据
     * 
     * @return 统计数据
     */
    Map<String, Object> getAssessmentStats();

    /**
     * 获取评估记录总数
     *
     * @return 评估记录总数
     */
    long count();

    /**
     * 分页查询用户评估问卷，支持按用户名和问卷标题搜索
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词（用户名或问卷标题）
     * @return 分页结果
     */
    IPage<UserAssessment> pageUserAssessments(int page, int size, String keyword);

    /**
     * 删除用户评估问卷
     * 
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean deleteUserAssessment(Long id);
}