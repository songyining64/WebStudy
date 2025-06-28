package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.entity.CommentLike;
import com.cupk.mapper.CommentLikeMapper;
import com.cupk.service.CommentLikeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeMapper commentLikeMapper;

    public CommentLikeServiceImpl(CommentLikeMapper commentLikeMapper) {
        this.commentLikeMapper = commentLikeMapper;
    }

    @Override
    @Transactional
    public boolean likeComment(Long commentId, Long userId) {
        if (hasLiked(commentId, userId))
            return false;
        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);
        return commentLikeMapper.insert(like) > 0;
    }

    @Override
    @Transactional
    public boolean unlikeComment(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        return commentLikeMapper.delete(wrapper) > 0;
    }

    @Override
    public boolean hasLiked(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
                .eq(CommentLike::getUserId, userId);
        return commentLikeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public Long getLikeCount(Long commentId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId);
        return commentLikeMapper.selectCount(wrapper);
    }
}