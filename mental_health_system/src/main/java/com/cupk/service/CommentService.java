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
}