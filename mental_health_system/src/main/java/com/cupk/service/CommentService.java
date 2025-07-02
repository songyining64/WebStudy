package com.cupk.service;

import com.cupk.entity.Comment;
import java.util.List;

public interface CommentService {
    /**
     * 发表评论
     *
     * @param comment 评论信息
     * @return 是否成功
     */
    boolean addComment(Comment comment);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID（用于验证权限）
     * @return 是否成功
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 获取帖子的评论列表
     *
     * @param postId 帖子ID
     * @return 评论列表
     */
    List<Comment> getCommentsByPostId(Long postId);

    /**
     * 获取评论的回复列表
     *
     * @param parentId 父评论ID
     * @return 回复列表
     */
    List<Comment> getRepliesByParentId(Long parentId);

    /**
     * 获取带用户信息的帖子评论列表
     *
     * @param postId        帖子ID
     * @param currentUserId 当前登录用户ID（用于判断是否点赞）
     * @return 带用户信息的评论列表
     */
    List<Comment> getCommentsWithUserInfo(Long postId, Long currentUserId);

    /**
     * 获取带用户信息的评论回复列表
     *
     * @param parentId      父评论ID
     * @param currentUserId 当前登录用户ID（用于判断是否点赞）
     * @return 带用户信息的回复列表
     */
    List<Comment> getRepliesWithUserInfo(Long parentId, Long currentUserId);

    /**
     * 根据评论ID获取评论
     *
     * @param commentId 评论ID
     * @return 评论对象，如果不存在则返回null
     */
    Comment getCommentById(Long commentId);

    /**
     * 获取评论总数
     *
     * @return 评论总数
     */
    long count();
}