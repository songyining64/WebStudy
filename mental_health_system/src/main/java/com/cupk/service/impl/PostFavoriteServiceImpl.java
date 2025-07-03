package com.cupk.service.impl;

import com.cupk.entity.PostFavorite;
import com.cupk.mapper.PostFavoriteMapper;
import com.cupk.service.PostFavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostFavoriteServiceImpl implements PostFavoriteService {
    private static final Logger logger = LoggerFactory.getLogger(PostFavoriteServiceImpl.class);

    @Autowired
    private PostFavoriteMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favorite(Long userId, Long postId) {
        if (mapper.select(userId, postId) != null) {
            logger.info("用户已收藏该帖子: userId={}, postId={}", userId, postId);
            return true;
        }

        PostFavorite pf = new PostFavorite();
        pf.setUserId(userId);
        pf.setPostId(postId);
        pf.setCreateTime(LocalDateTime.now());

        int result = mapper.insert(pf);
        logger.info("收藏帖子结果: {}, userId={}, postId={}", result > 0, userId, postId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavorite(Long userId, Long postId) {
        int result = mapper.delete(userId, postId);
        logger.info("取消收藏帖子结果: {}, userId={}, postId={}", result > 0, userId, postId);
        return result > 0;
    }

    @Override
    public boolean isFavorite(Long userId, Long postId) {
        boolean result = mapper.select(userId, postId) != null;
        logger.debug("检查用户是否收藏帖子: {}, userId={}, postId={}", result, userId, postId);
        return result;
    }

    @Override
    public long getFavoriteCount(Long postId) {
        long count = mapper.countByPostId(postId);
        logger.debug("获取帖子收藏数: {}, postId={}", count, postId);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByPostId(Long postId) {
        try {
            logger.info("删除帖子的所有收藏记录: postId={}", postId);
            int result = mapper.deleteByPostId(postId);
            logger.info("删除帖子收藏记录结果: 删除了{}条记录, postId={}", result, postId);
            return true;
        } catch (Exception e) {
            logger.error("删除帖子收藏记录时发生异常: postId={}", postId, e);
            throw e;
        }
    }
}