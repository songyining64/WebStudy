package com.cupk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.MusicFavorite;
import com.cupk.mapper.MusicFavoriteMapper;
import com.cupk.service.MusicFavoriteService;
import org.springframework.stereotype.Service;

@Service
public class MusicFavoriteServiceImpl extends ServiceImpl<MusicFavoriteMapper, MusicFavorite> implements MusicFavoriteService {
} 