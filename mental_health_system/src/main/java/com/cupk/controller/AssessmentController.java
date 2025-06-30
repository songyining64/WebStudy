package com.cupk.controller;

import com.cupk.service.UserAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/api/assessments")
public class AssessmentController {

    @Autowired
    private UserAssessmentService userAssessmentService;

    @GetMapping("/{assessmentId}/report")
    public ResponseEntity<String> getAssessmentReport(@PathVariable Long assessmentId) {
        String report = userAssessmentService.generateReport(assessmentId);



        if (report == null || report.startsWith("未找到")) {
           return ResponseEntity.notFound().build();
        }


        return ResponseEntity.ok(report);
    }
}