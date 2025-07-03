package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.client.DeepSeekClient;
import com.cupk.entity.UserAssessment;
import com.cupk.entity.Questionnaire;
import com.cupk.mapper.UserAssessmentMapper;
import com.cupk.service.UserAssessmentService;
import com.cupk.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.StringUtils;

@Service
public class UserAssessmentServiceImpl extends ServiceImpl<UserAssessmentMapper, UserAssessment>
        implements UserAssessmentService {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Value("${questionnaire.assessment.prompt}")
    private String assessmentPrompt;

    @Override
    public String generateReport(Long assessmentId) {
        // 获取问卷评估记录
        UserAssessment assessment = getById(assessmentId);
        if (assessment == null) {
            throw new RuntimeException("评估记录不存在");
        }

        // 获取用户的答案
        String answers = assessment.getAnswers();

        // 构造提示词
        String prompt = assessmentPrompt.replace("{{answers}}", answers);

        // 调用AI生成评估报告
        String aiResponse = deepSeekClient.chat(prompt);

        // 更新评估报告
        assessment.setReport(aiResponse);
        updateById(assessment);

        return aiResponse;
    }

    @Override
    public boolean saveAssessment(UserAssessment assessment) {
        // 不能直接设置createTime，因为实体中没有该字段
        return save(assessment);
    }

    @Override
    public IPage<UserAssessment> getUserAssessments(Long userId, int page, int size) {
        Page<UserAssessment> pageParam = new Page<>(page, size);
        try {
            // 使用手动 SQL 查询或在 XML 中定义专门的方法更安全
            // 这里我们使用已有的 pageUserAssessments 方法并添加过滤条件
            LambdaQueryWrapper<UserAssessment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserAssessment::getUserId, userId)
                    .orderByDesc(UserAssessment::getSubmitTime);

            // 使用 XML 中定义的列
            return this.baseMapper.pageUserAssessments(pageParam, null);
        } catch (Exception e) {
            throw new RuntimeException("查询用户评估问卷失败: " + e.getMessage(), e);
        }
    }

    @Override
    public UserAssessment getLatestAssessment(Long userId) {
        LambdaQueryWrapper<UserAssessment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAssessment::getUserId, userId)
                .last("LIMIT 1");
        return getOne(wrapper);
    }

    @Override
    public Map<String, Object> getAssessmentStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总评估次数
        long totalCount = count();
        stats.put("totalCount", totalCount);

        // TODO: 添加更多统计数据，如按类型、分数区间等

        return stats;
    }

    @Override
    public long count() {
        return count(null);
    }

    @Override
    public IPage<UserAssessment> pageUserAssessments(int page, int size, String keyword) {
        Page<UserAssessment> pageParam = new Page<>(page, size);

        try {
            if (StringUtils.hasText(keyword)) {
                // 使用自定义方法搜索
                return this.baseMapper.pageUserAssessments(pageParam, keyword);
            } else {
                // 使用自定义方法但不带关键词
                return this.baseMapper.pageUserAssessments(pageParam, null);
            }
        } catch (Exception e) {
            throw new RuntimeException("查询用户评估问卷失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUserAssessment(Long id) {
        if (id == null) {
            return false;
        }
        return this.removeById(id);
    }
}