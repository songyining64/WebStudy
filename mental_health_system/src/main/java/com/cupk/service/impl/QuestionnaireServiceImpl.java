package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.Questionnaire;
import com.cupk.mapper.QuestionnaireMapper;
import com.cupk.service.QuestionnaireService;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireServiceImpl extends ServiceImpl<QuestionnaireMapper, Questionnaire> implements QuestionnaireService {
}