package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.dto.UserRegisterDTO;
import com.cupk.dto.UserLoginDTO;
import com.cupk.entity.User;
import com.cupk.mapper.UserMapper;
import com.cupk.service.UserService;
import com.cupk.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordValidator passwordValidator;

    @Override
    public void register(UserRegisterDTO dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("邮箱已注册");
        }

        // 验证密码格式
        if (!passwordValidator.isValid(dto.getPassword())) {
            throw new RuntimeException(passwordValidator.getPasswordRequirements());
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // 直接使用明文密码
        user.setEmail(dto.getEmail());
        user.setRole("user");
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = userMapper.selectByEmail(dto.getUsername()); // 只用邮箱查找
        if (user == null) {
            throw new RuntimeException("用户未注册，请先注册");
        }
        if (!dto.getPassword().equals(user.getPassword())) { // 直接比较明文密码
            throw new RuntimeException("密码错误");
        }
        // 这里应集成JWT生成token，暂时返回用户名作token占位
        return user.getUsername();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        // 验证密码格式
        if (!passwordValidator.isValid(newPassword)) {
            throw new RuntimeException(passwordValidator.getPasswordRequirements());
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = this.getOne(wrapper);
        if (user != null) {
            user.setPassword(newPassword); // 直接使用明文密码
            this.updateById(user);
        }
    }

    @Override
    public User getByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return this.getOne(wrapper);
    }

    // 实现管理员相关接口

    @Override
    public IPage<User> getUserList(int page, int size, String keyword) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 如果有关键词，添加搜索条件（模糊搜索用户名或邮箱）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword);
        }

        // 按创建时间倒序排序
        wrapper.orderByDesc(User::getCreateTime);

        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateUserInfo(User user) {
        // 检查用户是否存在
        User existingUser = this.getById(user.getId());
        if (existingUser == null) {
            return false;
        }

        // 只允许更新某些字段，避免修改敏感信息
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setUsername(user.getUsername());
        updateUser.setEmail(user.getEmail());
        updateUser.setRole(user.getRole());
        updateUser.setAvatar(user.getAvatar());

        return this.updateById(updateUser);
    }

    @Override
    public boolean changeUserRole(Long userId, String role) {
        if (!"admin".equals(role) && !"user".equals(role)) {
            throw new RuntimeException("角色只能是admin或user");
        }

        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
                .set(User::getRole, role);

        return this.update(wrapper);
    }

    @Override
    public User validateToken(String token) {
        try {
            // 简单实现，实际项目中应该使用JWT进行解析
            // 这里我们用用户名作为token，所以直接用token查找用户
            User user = this.findByUsername(token);
            if (user == null) {
                // 也尝试用邮箱查找
                user = this.findByEmail(token);
            }
            return user;
        } catch (Exception e) {
            System.out.println("验证token失败: " + e.getMessage());
            return null;
        }
    }
}