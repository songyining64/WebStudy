package com.cupk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cupk.entity.User;
import com.cupk.service.UserService;
import com.cupk.service.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
     * 判断是否为管理员
     * 
     * @param request HTTP请求
     * @return 是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        return "admin".equals(role);
    }
}