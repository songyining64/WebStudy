package com.cupk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.UserAssessment;

public interface UserAssessmentService extends IService<UserAssessment> {
    String generateReport(Long assessmentId);
}