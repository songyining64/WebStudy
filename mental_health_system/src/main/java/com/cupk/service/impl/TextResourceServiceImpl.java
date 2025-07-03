package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.TextResource;
import com.cupk.mapper.TextResourceMapper;
import com.cupk.service.TextResourceService;
import org.springframework.stereotype.Service;

@Service
public class TextResourceServiceImpl extends ServiceImpl<TextResourceMapper, TextResource> implements TextResourceService {
}