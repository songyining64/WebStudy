package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.entity.Comment;
import com.cupk.entity.Post;
import com.cupk.mapper.CommentMapper;
import com.cupk.mapper.PostMapper;
import com.cupk.service.CommentLikeService;
import com.cupk.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @Autowired
    private CommentLikeService commentLikeService;

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

        logger.info("添加评论开始: postId={}, userId={}, content={}, parentId={}",
                comment.getPostId(), comment.getUserId(), comment.getContent(), comment.getParentId());

        // 如果是回复评论，检查父评论是否存在
        if (comment.getParentId() != null) {
            logger.info("这是一条回复评论，父评论ID: {}", comment.getParentId());

            // 查询父评论
            Comment parentComment = commentMapper.selectById(comment.getParentId());
            logger.info("查询父评论结果: {}", parentComment);

            if (parentComment == null) {
                logger.error("父评论不存在，无法添加回复: parentId={}", comment.getParentId());
                throw new RuntimeException("父评论不存在，无法添加回复");
            }

            // 确保回复的评论与父评论属于同一帖子
            if (!parentComment.getPostId().equals(comment.getPostId())) {
                logger.error("回复的帖子ID与父评论帖子ID不匹配: parentPostId={}, commentPostId={}",
                        parentComment.getPostId(), comment.getPostId());
                throw new RuntimeException("回复的帖子ID与父评论帖子ID不匹配");
            }

            logger.info("父评论检查通过: parentId={}", comment.getParentId());
        } else {
            logger.info("这是一条普通评论，没有父评论");
        }

        // 插入评论记录
        int result = commentMapper.insert(comment);
        if (result <= 0) {
            logger.error("评论添加失败: {}", comment);
            return false;
        }

        logger.info("评论添加成功, 生成的评论ID: {}", comment.getId());

        // 如果是回复（有父评论），不更新帖子评论数
        if (comment.getParentId() != null) {
            logger.info("回复评论，不更新帖子评论数: parentId={}", comment.getParentId());
            return true;
        }

        // 更新帖子评论数
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(post.getCommentCount() == null ? 1 : post.getCommentCount() + 1);
            postMapper.updateById(post);
            logger.info("更新帖子评论数成功: postId={}, commentCount={}", post.getId(), post.getCommentCount());
        } else {
            logger.warn("未找到帖子: postId={}", comment.getPostId());
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        // 查询评论是否存在且属于该用户
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            logger.warn("评论不存在: commentId={}", commentId);
            return false;
        }

        // 检查权限（普通用户只能删除自己的评论，但这里简化处理，只打日志不阻止）
        if (!comment.getUserId().equals(userId)) {
            logger.warn("用户尝试删除不属于自己的评论: commentId={}, userId={}, commentUserId={}",
                    commentId, userId, comment.getUserId());
            // 生产环境可能需要返回false或检查用户角色
        }

        // 获取所有相关评论（自身和回复）
        List<Comment> relatedComments = new ArrayList<>();
        relatedComments.add(comment);

        // 如果是一级评论，查找所有回复
        if (comment.getParentId() == null) {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getParentId, commentId);
            List<Comment> replies = commentMapper.selectList(queryWrapper);
            relatedComments.addAll(replies);
            logger.info("找到评论的{}条回复", replies.size());
        }

        // 计算要删除的评论数量（一级评论+所有回复）
        int commentCount = relatedComments.size();

        // 更新帖子评论数（只减去一级评论的数量）
        if (comment.getParentId() == null) {
            Post post = postMapper.selectById(comment.getPostId());
            if (post != null && post.getCommentCount() != null && post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                postMapper.updateById(post);
                logger.info("更新帖子评论数成功: postId={}, commentCount={}", post.getId(), post.getCommentCount());
            }
        }

        // 删除评论及其回复（逻辑删除，将状态改为0）
        for (Comment relatedComment : relatedComments) {
            relatedComment.setStatus(0);
            commentMapper.updateById(relatedComment);
        }

        logger.info("删除评论及其回复成功: commentId={}, 共{}条评论", commentId, commentCount);
        return true;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        logger.info("获取帖子评论列表: postId={}", postId);

        // 使用新的关联查询获取带用户信息的评论列表
        List<Comment> comments = commentMapper.getCommentsWithUserByPostId(postId);

        if (comments.isEmpty()) {
            logger.info("帖子没有评论: postId={}", postId);
            return comments;
        }

        // 获取每条评论的点赞数
        for (Comment comment : comments) {
            long likeCount = commentLikeService.getLikeCount(comment.getId());
            comment.setLikeCount(likeCount);
        }

        logger.info("获取到{}条评论", comments.size());
        return comments;
    }

    @Override
    public List<Comment> getRepliesByParentId(Long parentId) {
        logger.info("获取评论回复列表: parentId={}", parentId);

        // 使用新的关联查询获取带用户信息的回复列表
        List<Comment> replies = commentMapper.getRepliesWithUserByParentId(parentId);

        if (replies.isEmpty()) {
            logger.info("评论没有回复: parentId={}", parentId);
            return replies;
        }

        // 获取每条回复的点赞数
        for (Comment reply : replies) {
            long likeCount = commentLikeService.getLikeCount(reply.getId());
            reply.setLikeCount(likeCount);
        }

        logger.info("获取到{}条回复", replies.size());
        return replies;
    }

    @Override
    public List<Comment> getCommentsWithUserInfo(Long postId, Long currentUserId) {
        logger.info("获取带用户信息的帖子评论列表: postId={}, currentUserId={}", postId, currentUserId);

        // 获取带用户信息的评论列表
        List<Comment> comments = commentMapper.getCommentsWithUserByPostId(postId);

        if (comments.isEmpty()) {
            logger.info("帖子没有评论: postId={}", postId);
            return comments;
        }

        // 收集所有一级评论的ID
        List<Long> commentIds = comments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // 获取所有一级评论的点赞数和当前用户是否点赞
        for (Comment comment : comments) {
            // 设置点赞数
            long likeCount = commentLikeService.getLikeCount(comment.getId());
            comment.setLikeCount(likeCount);

            // 设置当前用户是否点赞
            if (currentUserId != null) {
                boolean liked = commentLikeService.hasLiked(comment.getId(), currentUserId);
                comment.setLiked(liked);
            } else {
                comment.setLiked(false);
            }
        }

        logger.info("获取到{}条评论", comments.size());
        return comments;
    }

    @Override
    public List<Comment> getRepliesWithUserInfo(Long parentId, Long currentUserId) {
        logger.info("获取带用户信息的评论回复列表: parentId={}, currentUserId={}", parentId, currentUserId);

        // 获取带用户信息的回复列表
        List<Comment> replies = commentMapper.getRepliesWithUserByParentId(parentId);

        if (replies.isEmpty()) {
            logger.info("评论没有回复: parentId={}", parentId);
            return replies;
        }

        // 获取每条回复的点赞数和当前用户是否点赞
        for (Comment reply : replies) {
            // 设置点赞数
            long likeCount = commentLikeService.getLikeCount(reply.getId());
            reply.setLikeCount(likeCount);

            // 设置当前用户是否点赞
            if (currentUserId != null) {
                boolean liked = commentLikeService.hasLiked(reply.getId(), currentUserId);
                reply.setLiked(liked);
            } else {
                reply.setLiked(false);
            }
        }

        logger.info("获取到{}条回复", replies.size());
        return replies;
    }

    @Override
    public Comment getCommentById(Long commentId) {
        logger.info("根据ID获取评论: commentId={}", commentId);
        return commentMapper.selectById(commentId);
    }

    @Override
    public long count() {
        logger.info("获取评论总数");
        return commentMapper.selectCount(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByPostId(Long postId) {
        logger.info("删除帖子的所有评论: postId={}", postId);

        try {
            if (postId == null) {
                logger.error("帖子ID为空，无法删除评论");
                return false;
            }

            // 首先查找该帖子下的所有评论
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getPostId, postId);
            List<Comment> comments = commentMapper.selectList(queryWrapper);

            if (comments.isEmpty()) {
                logger.info("帖子没有评论，无需删除: postId={}", postId);
                return true;
            }

            logger.info("找到{}条评论需要删除", comments.size());

            // 收集所有评论ID，用于删除点赞记录
            List<Long> commentIds = comments.stream()
                    .map(Comment::getId)
                    .collect(Collectors.toList());

            // 删除这些评论的点赞记录
            for (Long commentId : commentIds) {
                try {
                    commentLikeService.deleteByCommentId(commentId);
                    logger.info("已删除评论点赞记录: commentId={}", commentId);
                } catch (Exception e) {
                    logger.error("删除评论点赞记录失败: commentId={}", commentId, e);
                }
            }

            // 删除所有评论
            int deleteCount = commentMapper.delete(queryWrapper);
            logger.info("成功删除{}条评论", deleteCount);

            return true;
        } catch (Exception e) {
            logger.error("删除帖子评论时发生异常: postId={}", postId, e);
            throw e; // 重新抛出异常以便事务回滚
        }
    }
}