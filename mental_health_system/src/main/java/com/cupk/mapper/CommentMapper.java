package com.cupk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupk.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 获取指定帖子的评论列表，包含用户信息
     * 
     * @param postId 帖子ID
     * @return 评论列表
     */
    @Select("SELECT c.*, u.username, u.avatar FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} AND c.parent_id IS NULL AND c.status = 1 " +
            "ORDER BY c.create_time DESC")
    List<Comment> getCommentsWithUserByPostId(@Param("postId") Long postId);

    /**
     * 获取指定评论的回复列表，包含用户信息
     * 
     * @param parentId 父评论ID
     * @return 回复列表
     */
    @Select("SELECT c.*, u.username, u.avatar FROM comment c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.parent_id = #{parentId} AND c.status = 1 " +
            "ORDER BY c.create_time ASC")
    List<Comment> getRepliesWithUserByParentId(@Param("parentId") Long parentId);
}