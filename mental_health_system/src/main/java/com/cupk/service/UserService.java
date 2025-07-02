package com.cupk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cupk.dto.UserRegisterDTO;
import com.cupk.dto.UserLoginDTO;
import com.cupk.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    void register(UserRegisterDTO dto);

    String login(UserLoginDTO dto);

    User findByUsername(String username);

    User findByEmail(String email);

    void resetPassword(String email, String newPassword);

    User getByEmail(String email);

    // 添加Token验证方法
    /**
     * 验证令牌并返回用户信息
     * 
     * @param token JWT令牌
     * @return 用户信息，如果令牌无效则返回null
     */
    User validateToken(String token);

    // 新增管理员功能接口

    /**
     * 分页获取用户列表，支持搜索
     * 
     * @param page    页码
     * @param size    每页数量
     * @param keyword 关键词（用户名或邮箱，模糊搜索）
     * @return 分页用户列表
     */
    IPage<User> getUserList(int page, int size, String keyword);

    /**
     * 更新用户信息
     * 
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUserInfo(User user);

    /**
     * 切换用户角色
     * 
     * @param userId 用户ID
     * @param role   新角色
     * @return 是否成功
     */
    boolean changeUserRole(Long userId, String role);
}
