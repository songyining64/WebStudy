package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.entity.Comment;
import com.cupk.entity.Post;
import com.cupk.mapper.CommentMapper;
import com.cupk.mapper.PostMapper;
import com.cupk.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    public CommentServiceImpl(CommentMapper commentMapper, PostMapper postMapper) {
        this.commentMapper = commentMapper;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addComment(Comment comment) {
        // 设置评论基本信息
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setStatus(1); // 1表示正常状态

        logger.info("添加评论: {}", comment);

        // 更新帖子评论数
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(post.getCommentCount() == null ? 1 : post.getCommentCount() + 1);
            postMapper.updateById(post);
            logger.info("更新帖子评论数成功: postId={}, commentCount={}", post.getId(), post.getCommentCount());
        } else {
            logger.warn("未找到帖子: postId={}", comment.getPostId());
        }

        int result = commentMapper.insert(comment);
        logger.info("评论添加结果: {}, 生成的评论ID: {}", result > 0, comment.getId());
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        // 查询评论是否存在且属于该用户
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            logger.warn("删除评论失败: 评论不存在或不属于该用户, commentId={}, userId={}", commentId, userId);
            return false;
        }

        // 更新帖子评论数
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null && post.getCommentCount() != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            postMapper.updateById(post);
            logger.info("更新帖子评论数成功: postId={}, commentCount={}", post.getId(), post.getCommentCount());
        }

        // 删除评论及其回复
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getId, commentId)
                .or()
                .eq(Comment::getParentId, commentId);

        int result = commentMapper.delete(wrapper);
        logger.info("删除评论及其回复结果: {}, commentId={}", result > 0, commentId);
        return result > 0;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        logger.info("获取帖子评论列表: postId={}", postId);

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
                .isNull(Comment::getParentId) // 只查询主评论，不包括回复
                .eq(Comment::getStatus, 1) // 只查询正常状态的评论
                .orderByDesc(Comment::getCreateTime);

        List<Comment> comments = commentMapper.selectList(wrapper);
        logger.info("获取到{}条评论", comments.size());
        return comments;
    }

    @Override
    public List<Comment> getRepliesByParentId(Long parentId) {
        logger.info("获取评论回复列表: parentId={}", parentId);

        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
                .eq(Comment::getStatus, 1) // 只查询正常状态的评论
                .orderByAsc(Comment::getCreateTime);

        List<Comment> replies = commentMapper.selectList(wrapper);
        logger.info("获取到{}条回复, SQL: {}", replies.size(), wrapper.getCustomSqlSegment());
        return replies;
    }
}