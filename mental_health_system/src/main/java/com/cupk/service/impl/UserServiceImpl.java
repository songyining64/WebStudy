package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户未注册，请先注册");
        }
        if (!dto.getPassword().equals(user.getPassword())) { // 直接比较明文密码
            throw new RuntimeException("密码错误");
        }
        // 这里应集成JWT生成token，暂时返回用户名作为token占位
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
}