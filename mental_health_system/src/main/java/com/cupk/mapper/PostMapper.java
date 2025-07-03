package com.cupk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    // 可扩展自定义SQL
    @Update("UPDATE post SET like_count = like_count + 1 WHERE id = #{postId}")
    void incrementLikeCount(@Param("postId") Long postId);

    @Update("UPDATE post SET like_count = like_count - 1 WHERE id = #{postId} AND like_count > 0")
    void decrementLikeCount(@Param("postId") Long postId);

    /**
     * 分页查询帖子列表，并关联用户信息
     * 
     * @param page 分页参数
     * @return 包含用户信息的帖子分页数据
     */
    @Select("SELECT p.id, p.user_id, p.title, p.content, p.images, p.tags, p.category, p.like_count, p.comment_count, "
            +
            "p.status, p.create_time, p.update_time, u.username, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "ORDER BY p.create_time DESC")
    IPage<Post> selectPostsWithUserInfo(Page<Post> page);

    /**
     * 获取带用户信息的帖子详情
     * 
     * @param postId 帖子ID
     * @return 带用户信息的帖子详情
     */
    @Select("SELECT p.id, p.user_id, p.title, p.content, p.images, p.tags, p.category, p.like_count, p.comment_count, "
            +
            "p.status, p.create_time, p.update_time, u.username, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.id = #{postId}")
    Post selectPostWithUserInfo(@Param("postId") Long postId);

    /**
     * 根据关键词搜索帖子（标题、内容、标签）
     * 
     * @param page    分页参数
     * @param keyword 搜索关键词
     * @return 符合条件的帖子分页数据
     */
    @Select("SELECT p.id, p.user_id, p.title, p.content, p.images, p.tags, p.category, p.like_count, p.comment_count, "
            +
            "p.status, p.create_time, p.update_time, u.username, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.title LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.content LIKE CONCAT('%', #{keyword}, '%') " +
            "OR p.tags LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY p.create_time DESC")
    IPage<Post> searchPostsByKeyword(Page<Post> page, @Param("keyword") String keyword);

    /**
     * 根据分类筛选帖子
     * 
     * @param page     分页参数
     * @param category 分类名称
     * @return 符合条件的帖子分页数据
     */
    @Select("SELECT p.id, p.user_id, p.title, p.content, p.images, p.tags, p.category, p.like_count, p.comment_count, "
            +
            "p.status, p.create_time, p.update_time, u.username, u.avatar FROM post p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.category = #{category} " +
            "ORDER BY p.create_time DESC")
    IPage<Post> filterPostsByCategory(Page<Post> page, @Param("category") String category);

    /**
     * 高级搜索：组合条件查询帖子
     * 
     * @param page      分页参数
     * @param keyword   搜索关键词（可选）
     * @param category  分类名称（可选）
     * @param sortBy    排序字段（create_time, like_count, comment_count）
     * @param sortOrder 排序方式（ASC, DESC）
     * @return 符合条件的帖子分页数据
     */
    @Select({
            "<script>",
            "SELECT p.id, p.user_id, p.title, p.content, p.images, p.tags, p.category, p.like_count, p.comment_count, ",
            "p.status, p.create_time, p.update_time, u.username, u.avatar FROM post p ",
            "LEFT JOIN user u ON p.user_id = u.id ",
            "WHERE 1=1 ",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (p.title LIKE CONCAT('%', #{keyword}, '%') ",
            "OR p.content LIKE CONCAT('%', #{keyword}, '%') ",
            "OR p.tags LIKE CONCAT('%', #{keyword}, '%')) ",
            "</if>",
            "<if test='category != null and category != \"\"'>",
            "AND p.category = #{category} ",
            "</if>",
            "<choose>",
            "<when test='sortBy == \"like_count\"'>ORDER BY p.like_count ${sortOrder}</when>",
            "<when test='sortBy == \"comment_count\"'>ORDER BY p.comment_count ${sortOrder}</when>",
            "<otherwise>ORDER BY p.create_time ${sortOrder}</otherwise>",
            "</choose>",
            "</script>"
    })
    IPage<Post> advancedSearch(
            Page<Post> page,
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder);
}