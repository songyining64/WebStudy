package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.entity.Comment;
import com.cupk.entity.CommentLike;
import com.cupk.mapper.CommentLikeMapper;
import com.cupk.mapper.CommentMapper;
import com.cupk.service.CommentLikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {
    private static final Logger logger = LoggerFactory.getLogger(CommentLikeServiceImpl.class);

    private final CommentLikeMapper commentLikeMapper;

    @Autowired
    private CommentMapper commentMapper;

    public CommentLikeServiceImpl(CommentLikeMapper commentLikeMapper) {
        this.commentLikeMapper = commentLikeMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeComment(Long commentId, Long userId) {
        logger.info("点赞评论: commentId={}, userId={}", commentId, userId);

        // 验证评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            logger.warn("评论不存在: commentId={}", commentId);
            return false;
        }

        // 检查是否已点赞
        if (hasLiked(commentId, userId)) {
            logger.info("用户已点赞该评论: commentId={}, userId={}", commentId, userId);
            return true;
        }

        // 创建点赞记录
        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        like.setCreateTime(LocalDateTime.now());

        int result = commentLikeMapper.insert(like);
        logger.info("点赞评论结果: {}, commentId={}, userId={}", result > 0, commentId, userId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeComment(Long commentId, Long userId) {
        logger.info("取消点赞评论: commentId={}, userId={}", commentId, userId);

        // 验证评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            logger.warn("评论不存在: commentId={}", commentId);
            return false;
        }

        // 删除点赞记录
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);

        int result = commentLikeMapper.delete(wrapper);
        logger.info("取消点赞评论结果: {}, commentId={}, userId={}", result > 0, commentId, userId);
        return result > 0;
    }

    @Override
    public boolean hasLiked(Long commentId, Long userId) {
        logger.debug("检查用户是否点赞评论: commentId={}, userId={}", commentId, userId);

        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);

        boolean result = commentLikeMapper.selectCount(wrapper) > 0;
        logger.debug("用户是否点赞评论: {}, commentId={}, userId={}", result, commentId, userId);
        return result;
    }

    @Override
    public Long getLikeCount(Long commentId) {
        logger.debug("获取评论点赞数: commentId={}", commentId);

        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId);

        Long count = commentLikeMapper.selectCount(wrapper);
        logger.debug("评论点赞数: {}, commentId={}", count, commentId);
        return count;
    }
}