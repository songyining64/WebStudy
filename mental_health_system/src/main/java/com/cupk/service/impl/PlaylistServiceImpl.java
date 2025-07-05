package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.Playlist;
import com.cupk.mapper.PlaylistMapper;
import com.cupk.service.PlaylistService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist> implements PlaylistService {

    @Override
    public List<Playlist> getByUserId(Long userId) {
        QueryWrapper<Playlist> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return list(wrapper);
    }
} 