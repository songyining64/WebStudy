package com.cupk.controller;

import com.cupk.entity.UserAssessment;
import com.cupk.service.UserAssessmentService;
import com.cupk.service.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/assessments")
public class UserAssessmentAdminController {

    @Autowired
    private UserAssessmentService userAssessmentService;

    /**
     * 分页查询用户评估问卷
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词（用户名或问卷标题）
     * @return 分页结果
     */
    @GetMapping
    public Result<IPage<UserAssessment>> pageUserAssessments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            IPage<UserAssessment> assessments = userAssessmentService.pageUserAssessments(page, size, keyword);
            return Result.success(assessments);
        } catch (Exception e) {
            return Result.error("查询用户评估问卷失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户评估问卷详情
     * 
     * @param id 记录ID
     * @return 记录详情
     */
    @GetMapping("/{id}")
    public Result<UserAssessment> getUserAssessment(@PathVariable Long id) {
        try {
            UserAssessment assessment = userAssessmentService.getById(id);
            if (assessment == null) {
                return Result.error("评估问卷不存在");
            }
            return Result.success(assessment);
        } catch (Exception e) {
            return Result.error("获取评估问卷失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户评估问卷
     * 
     * @param id 记录ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteUserAssessment(@PathVariable Long id) {
        try {
            boolean success = userAssessmentService.deleteUserAssessment(id);
            if (success) {
                return Result.success("删除评估问卷成功", true);
            } else {
                return Result.error("删除评估问卷失败");
            }
        } catch (Exception e) {
            return Result.error("删除评估问卷失败: " + e.getMessage());
        }
    }
}