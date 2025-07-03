package com.cupk.mapper;

import com.cupk.entity.PostFavorite;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PostFavoriteMapper {
    @Insert("INSERT INTO post_favorite(user_id, post_id, create_time) VALUES(#{userId}, #{postId}, #{createTime})")
    int insert(PostFavorite favorite);

    @Delete("DELETE FROM post_favorite WHERE user_id=#{userId} AND post_id=#{postId}")
    int delete(@Param("userId") Long userId, @Param("postId") Long postId);

    @Delete("DELETE FROM post_favorite WHERE post_id=#{postId}")
    int deleteByPostId(@Param("postId") Long postId);

    @Select("SELECT * FROM post_favorite WHERE user_id=#{userId} AND post_id=#{postId}")
    PostFavorite select(@Param("userId") Long userId, @Param("postId") Long postId);

    @Select("SELECT COUNT(*) FROM post_favorite WHERE post_id=#{postId}")
    long countByPostId(@Param("postId") Long postId);
}