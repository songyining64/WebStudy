package com.cupk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.Post;
import com.cupk.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {
    /**
     * 查询用户点赞的帖子列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 用户点赞的帖子分页数据
     */
    @Select("SELECT p.*, u.username, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "LEFT JOIN post_like pl ON p.id = pl.post_id " +
            "WHERE pl.user_id = #{userId} " +
            "ORDER BY pl.create_time DESC")
    IPage<Post> selectLikedPostsByUserId(Page<Post> page, @Param("userId") Long userId);
}