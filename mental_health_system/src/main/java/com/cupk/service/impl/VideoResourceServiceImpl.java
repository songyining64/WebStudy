package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.VideoResource;
import com.cupk.mapper.VideoResourceMapper;
import com.cupk.service.VideoResourceService;
import org.springframework.stereotype.Service;

@Service
public class VideoResourceServiceImpl extends ServiceImpl<VideoResourceMapper, VideoResource> implements VideoResourceService {
}