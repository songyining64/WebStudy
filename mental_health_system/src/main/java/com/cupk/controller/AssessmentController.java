package com.cupk.controller;

<<<<<<< HEAD
import com.cupk.service.UserAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

=======
import com.cupk.entity.UserAssessment;
import com.cupk.service.UserAssessmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
>>>>>>> 21bcbe8e27e27ecccf49aea82546929645c7c480
@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private UserAssessmentService userAssessmentService;

<<<<<<< HEAD
    @GetMapping("/{assessmentId}/report")
    public ResponseEntity<String> getAssessmentReport(@PathVariable Long assessmentId) {
        String report = userAssessmentService.generateReport(assessmentId);



        if (report == null || report.startsWith("未找到")) {
           return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(report);
    }
}
=======
    /**
     * 提交心理问卷答案，生成评估报告
     * @param payload {userId, questionnaireId, answers}
     * @return AI评估报告
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitAssessment(@RequestBody Map<String, Object> payload) {
        try {
            Long userId = Long.valueOf(payload.get("userId").toString());
            Long questionnaireId = payload.get("questionnaireId") != null ? Long.valueOf(payload.get("questionnaireId").toString()) : 1L;
            // 用Jackson序列化答案
            ObjectMapper objectMapper = new ObjectMapper();
            String answersJson = objectMapper.writeValueAsString(payload.get("answers"));

            UserAssessment assessment = new UserAssessment();
            assessment.setUserId(userId);
            assessment.setQuestionnaireId(questionnaireId);
            assessment.setAnswers(answersJson);

            // 先保存，获取ID
            userAssessmentService.save(assessment);

            // 调用AI生成评估报告
            String report = userAssessmentService.generateReport(assessment.getId());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("提交失败: " + e.getMessage());
        }
    }

    @GetMapping("/{assessmentId}/report")
    public ResponseEntity<String> getAssessmentReport(@PathVariable Long assessmentId) {
        String report = userAssessmentService.generateReport(assessmentId);
        if (report == null || report.startsWith("未找到")) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }
}
>>>>>>> 21bcbe8e27e27ecccf49aea82546929645c7c480
