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
    public List<QuestionnaireConfig.Question> getQuestionnaire() {
        return questionnaireConfig.getQuestions();
    }

    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody Map<String, Object> answers) {
        // TODO: 实现你的评估逻辑
        return ResponseEntity.ok("评估成功");
    }
}