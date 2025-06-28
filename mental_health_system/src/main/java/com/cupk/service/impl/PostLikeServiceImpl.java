package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.entity.Post;
import com.cupk.entity.PostLike;
import com.cupk.mapper.PostLikeMapper;
import com.cupk.mapper.PostMapper;
import com.cupk.service.PostLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PostLikeServiceImpl implements PostLikeService {
    private static final Logger logger = LoggerFactory.getLogger(PostLikeServiceImpl.class);

    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    public PostLikeServiceImpl(PostLikeMapper postLikeMapper, PostMapper postMapper) {
        this.postLikeMapper = postLikeMapper;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likePost(Long postId, Long userId) {
        // 检查是否已经点赞
        if (hasLiked(postId, userId)) {
            return false;
        }

        // 创建新的点赞记录
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreateTime(LocalDateTime.now());

        // 更新帖子点赞数
        Post post = postMapper.selectById(postId);
        logger.info("点赞前帖子信息: {}", post);

        if (post != null) {
            int currentLikes = post.getLikeCount() == null ? 0 : post.getLikeCount();
            post.setLikeCount(currentLikes + 1);
            int updateResult = postMapper.updateById(post);
            logger.info("更新帖子点赞数结果: {}, 更新后的点赞数: {}", updateResult, post.getLikeCount());
        } else {
            logger.error("未找到ID为{}的帖子", postId);
            return false;
        }

        int insertResult = postLikeMapper.insert(postLike);
        logger.info("插入点赞记录结果: {}", insertResult);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikePost(Long postId, Long userId) {
        // 检查是否已经点赞
        if (!hasLiked(postId, userId)) {
            return false;
        }

        // 更新帖子点赞数
        Post post = postMapper.selectById(postId);
        logger.info("取消点赞前帖子信息: {}", post);

        if (post != null && post.getLikeCount() != null && post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            int updateResult = postMapper.updateById(post);
            logger.info("更新帖子点赞数结果: {}, 更新后的点赞数: {}", updateResult, post.getLikeCount());
        } else {
            logger.error("未找到ID为{}的帖子或点赞数已为0", postId);
            return false;
        }

        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);

        int deleteResult = postLikeMapper.delete(wrapper);
        logger.info("删除点赞记录结果: {}", deleteResult);

        return true;
    }

    @Override
    public boolean hasLiked(Long postId, Long userId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId);

        return postLikeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Long getLikeCount(Long postId) {
        LambdaQueryWrapper<PostLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PostLike::getPostId, postId);

        return postLikeMapper.selectCount(wrapper);
    }
}