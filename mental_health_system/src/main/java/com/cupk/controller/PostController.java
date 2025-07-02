package com.cupk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cupk.entity.Post;
import com.cupk.service.PostFavoriteService;
import com.cupk.service.PostService;
import com.cupk.service.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/post")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private PostFavoriteService postFavoriteService;

    // 1. 发帖
    @PostMapping
    public Result<?> addPost(@RequestBody Post post) {
        post.setId(null); // 关键：让数据库自增
        postService.save(post);
        return Result.success(post.getId()); // 返回真实ID给前端
    }

    // 2. 帖子列表（分页）
    @GetMapping
    public Result<?> listPost(
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder) {

        // 支持current和page两个参数，优先使用current
        int pageNum = current > 0 ? current : page;

        logger.info("获取帖子列表请求: pageNum={}, size={}, keyword={}, category={}, sortBy={}, sortOrder={}",
                pageNum, size, keyword, category, sortOrder, sortBy);

        // 判断是否为高级搜索
        boolean isAdvancedSearch = (keyword != null && !keyword.trim().isEmpty()) ||
                (category != null && !category.trim().isEmpty()) ||
                (sortBy != null && !sortBy.trim().isEmpty());

        IPage<Post> postPage;

        if (isAdvancedSearch) {
            // 使用高级搜索
            postPage = postService.advancedSearch(pageNum, size, keyword, category, sortBy, sortOrder);
        } else {
            // 使用基本帖子列表查询
            postPage = postService.getPostListWithUserInfo(pageNum, size);
        }

        return Result.success(postPage);
    }

    // 3. 搜索帖子（关键词）
    @GetMapping("/search")
    public Result<?> searchPosts(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 支持current和page两个参数，优先使用current
        int pageNum = current > 0 ? current : page;

        logger.info("搜索帖子请求: keyword={}, pageNum={}, size={}", keyword, pageNum, size);

        IPage<Post> postPage = postService.searchPostsByKeyword(pageNum, size, keyword);
        return Result.success(postPage);
    }

    // 4. 按分类筛选帖子
    @GetMapping("/category/{category}")
    public Result<?> getPostsByCategory(
            @PathVariable String category,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 支持current和page两个参数，优先使用current
        int pageNum = current > 0 ? current : page;

        logger.info("按分类筛选帖子请求: category={}, pageNum={}, size={}", category, pageNum, size);

        IPage<Post> postPage = postService.filterPostsByCategory(pageNum, size, category);
        return Result.success(postPage);
    }

    // 5. 高级搜索
    @GetMapping("/advanced-search")
    public Result<?> advancedSearch(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            @RequestParam(value = "current", defaultValue = "1") int current,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // 支持current和page两个参数，优先使用current
        int pageNum = current > 0 ? current : page;

        logger.info("高级搜索帖子请求: keyword={}, category={}, sortBy={}, sortOrder={}, pageNum={}, size={}",
                keyword, category, sortBy, sortOrder, pageNum, size);

        IPage<Post> postPage = postService.advancedSearch(pageNum, size, keyword, category, sortBy, sortOrder);
        return Result.success(postPage);
    }

    // 6. 帖子详情
    @GetMapping("/{id}")
    public Result<?> postDetail(@PathVariable Long id) {
        // 使用带用户信息的帖子详情方法
        Post post = postService.getPostWithUserInfo(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }
        return Result.success(post);
    }

    // 7. 点赞帖子
    @PostMapping("/{id}/like")
    public Result<?> likePost(@PathVariable("id") Long postId, @RequestParam Long userId) {
        boolean success = postService.likePost(postId, userId);
        return success ? Result.success() : Result.error("点赞失败，可能已经点赞过或帖子不存在");
    }

    // 8. 取消点赞
    @DeleteMapping("/{id}/like")
    public Result<?> unlikePost(@PathVariable("id") Long postId, @RequestParam Long userId) {
        boolean success = postService.unlikePost(postId, userId);
        return success ? Result.success() : Result.error("取消点赞失败，可能尚未点赞或帖子不存在");
    }

    // 9. 获取点赞状态
    @GetMapping("/{id}/like/status")
    public Result<?> getLikeStatus(@PathVariable("id") Long postId, @RequestParam Long userId) {
        boolean hasLiked = postService.hasLiked(postId, userId);
        return Result.success(hasLiked);
    }

    // 10. 获取点赞数
    @GetMapping("/{id}/like/count")
    public Result<?> getLikeCount(@PathVariable("id") Long postId) {
        Long count = postService.getLikeCount(postId);
        return Result.success(count);
    }

    // 11. 查询收藏状态（简化版，默认用户ID为1）
    @GetMapping("/{id}/is-favorite")
    public Result<?> checkFavoriteStatus(@PathVariable("id") Long postId) {
        logger.info("检查帖子收藏状态: postId={}", postId);
        boolean isFavorite = postFavoriteService.isFavorite(1L, postId);
        return Result.success(isFavorite);
    }

    // 12. 获取收藏数
    @GetMapping("/{id}/favorite-count")
    public Result<?> getFavoriteCount(@PathVariable("id") Long postId) {
        logger.info("获取帖子收藏数: postId={}", postId);
        long count = postFavoriteService.getFavoriteCount(postId);
        return Result.success(count);
    }

    @DeleteMapping("/{id}")
    public Result<?> deletePost(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request))
            throw new RuntimeException("无权限");
        boolean success = postService.removeById(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    // 工具方法：判断是否为管理员
    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        return "admin".equals(role);
    }
}