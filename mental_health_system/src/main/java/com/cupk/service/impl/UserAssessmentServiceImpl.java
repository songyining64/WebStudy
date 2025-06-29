package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.client.DeepSeekClient;
import com.cupk.entity.UserAssessment;
import com.cupk.entity.Questionnaire;
import com.cupk.mapper.UserAssessmentMapper;
import com.cupk.service.UserAssessmentService;
import com.cupk.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class UserAssessmentServiceImpl extends ServiceImpl<UserAssessmentMapper, UserAssessment> implements UserAssessmentService {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Override
    public String generateReport(Long assessmentId) {
        UserAssessment assessment = baseMapper.selectById(assessmentId);
        if (assessment == null) {
            return "未找到评估记录";
        }

        Questionnaire questionnaire = questionnaireService.getById(assessment.getQuestionnaireId());
        if (questionnaire == null) {
            return "未找到问卷";
        }

        String questions = questionnaire.getQuestions();
        String answers = assessment.getAnswers();

        // 构建发送给 AI 的消息
        List<DeepSeekClient.Message> messages = new ArrayList<>();
        messages.add(new DeepSeekClient.Message("system", "你是一位心理评估专家，请根据以下问卷问题和答案，给出具体评分和分析报告"));
        messages.add(new DeepSeekClient.Message("user", "问卷问题: " + questions + "\n用户答案: " + answers));

        // 调用 DeepSeekClient 获取 AI 的回复
        String aiResponse = deepSeekClient.ask(messages);

        // 保存 AI 的回复
        assessment.setReport(aiResponse);
        baseMapper.updateById(assessment);

        return aiResponse;
    }
}