// src/main/java/com/cupk/controller/QuestionnaireController.java
package com.cupk.controller;

import com.cupk.config.QuestionnaireConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/questionnaire")
public class QuestionnaireController {

    @Autowired
    private QuestionnaireConfig questionnaireConfig;

    @GetMapping
    public List<QuestionnaireConfig.Question> getQuestionnaire() {
        return questionnaireConfig.getQuestions();
    }
}