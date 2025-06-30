package com.cupk.mapper;

import com.cupk.entity.PostFavorite;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PostFavoriteMapper {
    @Insert("INSERT INTO post_favorite(user_id, post_id) VALUES(#{userId}, #{postId})")
    int insert(PostFavorite favorite);

    @Delete("DELETE FROM post_favorite WHERE user_id=#{userId} AND post_id=#{postId}")
    int delete(@Param("userId") Long userId, @Param("postId") Long postId);

    @Select("SELECT * FROM post_favorite WHERE user_id=#{userId} AND post_id=#{postId}")
    PostFavorite select(@Param("userId") Long userId, @Param("postId") Long postId);
}