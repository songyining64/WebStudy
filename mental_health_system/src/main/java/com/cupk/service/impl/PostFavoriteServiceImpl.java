package com.cupk.service.impl;

import com.cupk.entity.PostFavorite;
import com.cupk.mapper.PostFavoriteMapper;
import com.cupk.service.PostFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostFavoriteServiceImpl implements PostFavoriteService {
    @Autowired
    private PostFavoriteMapper mapper;

    @Override
    public boolean favorite(Long userId, Long postId) {
        if (mapper.select(userId, postId) != null)
            return true;
        PostFavorite pf = new PostFavorite();
        pf.setUserId(userId);
        pf.setPostId(postId);
        return mapper.insert(pf) > 0;
    }

    @Override
    public boolean unfavorite(Long userId, Long postId) {
        return mapper.delete(userId, postId) > 0;
    }

    @Override
    public boolean isFavorite(Long userId, Long postId) {
        return mapper.select(userId, postId) != null;
    }
}