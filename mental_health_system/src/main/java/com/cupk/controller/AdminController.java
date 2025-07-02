package com.cupk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cupk.entity.User;
import com.cupk.entity.Post;
import com.cupk.service.UserService;
import com.cupk.service.PostService;
import com.cupk.service.CommentService;
import com.cupk.service.EmotionRecordService;
import com.cupk.service.UserAssessmentService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理员专用接口控制器
 * 所有接口都需要管理员权限
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EmotionRecordService emotionRecordService;

    @Autowired
    private UserAssessmentService userAssessmentService;

    /**
     * 管理员身份验证
     *
     * @param request HTTP请求
     * @return 验证结果
     */
    @GetMapping("/verify")
    public Result<?> verifyAdmin(HttpServletRequest request) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("isAdmin", true);
        data.put("message", "管理员权限验证成功");
        return Result.success(data);
    }

    /**
     * 获取系统统计数据
     *
     * @param request HTTP请求
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Result<?> getSystemStats(HttpServletRequest request) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        long userCount = userService.count();
        long postCount = postService.count();
        long commentCount = commentService.count();
        long emotionRecordCount = emotionRecordService.count();
        long assessmentCount = userAssessmentService.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userCount);
        stats.put("postCount", postCount);
        stats.put("commentCount", commentCount);
        stats.put("emotionRecordCount", emotionRecordCount);
        stats.put("assessmentCount", assessmentCount);

        return Result.success(stats);
    }

    /**
     * 获取用户列表（分页、可搜索）
     * 
     * @param page    页码（默认1）
     * @param size    每页数量（默认10）
     * @param keyword 搜索关键词（用户名或邮箱，可选）
     * @param request HTTP请求
     * @return 用户列表分页结果
     */
    @GetMapping("/users")
    public Result<?> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        IPage<User> userPage = userService.getUserList(page, size, keyword);
        return Result.success(userPage);
    }

    /**
     * 获取用户详情
     *
     * @param id      用户ID
     * @param request HTTP请求
     * @return 用户详情
     */
    @GetMapping("/users/{id}")
    public Result<?> getUserDetail(@PathVariable Long id, HttpServletRequest request) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 敏感信息处理
        user.setPassword(null);

        return Result.success(user);
    }

    /**
     * 编辑用户信息
     * 
     * @param id      用户ID
     * @param user    用户信息
     * @param request HTTP请求
     * @return 操作结果
     */
    @PutMapping("/users/{id}")
    public Result<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user,
            HttpServletRequest request) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        // 防止ID不一致
        if (!id.equals(user.getId())) {
            return Result.error("用户ID不匹配");
        }

        boolean success = userService.updateUserInfo(user);
        return success ? Result.success() : Result.error("更新失败，用户可能不存在");
    }

    /**
     * 删除用户
     * 
     * @param id      用户ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/users/{id}")
    public Result<?> deleteUser(
            @PathVariable Long id,
            HttpServletRequest request) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        boolean success = userService.removeById(id);
        return success ? Result.success() : Result.error("删除失败，用户可能不存在");
    }

    /**
     * 切换用户角色
     * 
     * @param id      用户ID
     * @param roleMap 包含role字段的JSON对象
     * @param request HTTP请求
     * @return 操作结果
     */
    @PatchMapping("/users/{id}/role")
    public Result<?> changeUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> roleMap,
            HttpServletRequest request) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        String role = roleMap.get("role");
        if (role == null) {
            return Result.error("缺少role参数");
        }

        try {
            boolean success = userService.changeUserRole(id, role);
            return success ? Result.success() : Result.error("更新失败，用户可能不存在");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取帖子列表（分页、可搜索）
     * 
     * @param page    页码（默认1）
     * @param size    每页数量（默认10）
     * @param keyword 搜索关键词（标题或内容，可选）
     * @param request HTTP请求
     * @return 帖子列表分页结果
     */
    @GetMapping("/posts")
    public Result<?> getPostList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        IPage<Post> postPage = postService.searchPostsByKeyword(page, size, keyword);
        return Result.success(postPage);
    }

    /**
     * 获取帖子详情
     *
     * @param id      帖子ID
     * @param request HTTP请求
     * @return 帖子详情
     */
    @GetMapping("/posts/{id}")
    public Result<?> getPostDetail(@PathVariable Long id, HttpServletRequest request) {
        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        Post post = postService.getPostWithUserInfo(id);
        if (post == null) {
            return Result.error("帖子不存在");
        }

        return Result.success(post);
    }

    /**
     * 删除帖子
     * 
     * @param id      帖子ID
     * @param request HTTP请求
     * @return 操作结果
     */
    @DeleteMapping("/posts/{id}")
    public Result<?> deletePost(
            @PathVariable Long id,
            HttpServletRequest request) {

        // 验证管理员权限
        if (!isAdmin(request)) {
            return Result.error("无管理员权限");
        }

        boolean success = postService.removeById(id);
        return success ? Result.success() : Result.error("删除失败，帖子可能不存在");
    }

    /**
     * 判断是否为管理员
     * 
     * @param request HTTP请求
     * @return 是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        String authHeader = request.getHeader("Authorization");

        System.out.println("管理员权限验证 - X-User-Role: " + role);
        System.out.println("管理员权限验证 - Authorization: " + authHeader);

        if (role == null && authHeader != null) {
            // 如果没有角色但有认证令牌，尝试从UserService验证用户权限
            try {
                // 提取token
                String token = authHeader.replace("Bearer ", "");
                User user = userService.validateToken(token);
                if (user != null && "admin".equals(user.getRole())) {
                    System.out.println("通过Token验证用户为管理员: " + user.getUsername());
                    return true;
                }
            } catch (Exception e) {
                System.out.println("Token验证失败: " + e.getMessage());
            }
        }

        // 开发环境下暂时放宽验证
        if ("admin".equals(role)) {
            System.out.println("通过X-User-Role验证为管理员");
            return true;
        }

        System.out.println("管理员验证失败");
        return false;
    }
}