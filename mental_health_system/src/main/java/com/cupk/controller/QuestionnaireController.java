// src/main/java/com/cupk/controller/QuestionnaireController.java
package com.cupk.controller;

import com.cupk.config.QuestionnaireConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireConfig questionnaireConfig;

    @GetMapping
    public List<Map<String, Object>> getQuestionnaire() {
        List<QuestionnaireConfig.Question> questions = questionnaireConfig.getQuestions();
        System.out.println("[QuestionnaireController] 问卷数据: " + (questions == null ? "null" : questions.toString()));
        if (questions == null) {
            return java.util.Collections.emptyList();
        }
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (QuestionnaireConfig.Question q : questions) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", q.getId());
            map.put("text", q.getText());
            map.put("type", q.getType());
            map.put("options", q.getOptions());
            result.add(map);
        }
        return result;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody Map<String, Object> answers) {
        // TODO: 实现你的评估逻辑
        return ResponseEntity.ok("评估成功");
    }
}