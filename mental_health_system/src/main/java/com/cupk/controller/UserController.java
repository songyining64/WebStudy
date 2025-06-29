package com.cupk.controller;

import com.cupk.entity.User;
import com.cupk.service.UserService;
import com.cupk.dto.UserRegisterDTO;
import com.cupk.dto.UserLoginDTO;
import com.cupk.service.Result;
import com.cupk.service.PasswordResetService;
import com.cupk.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private MailService mailService;

    @GetMapping
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public boolean add(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return userService.removeById(id);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody UserRegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO dto) {
        try {
            String token = userService.login(dto);
            // 获取用户信息
            User user = userService.findByUsername(dto.getUsername());
            // 封装返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("avatar", user.getAvatar());
            return Result.success("登录成功", data);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/forgot-password/code")
    public Result<?> sendResetCode(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱不能为空");
            }

            User user = userService.getByEmail(email);
            if (user == null)
                return Result.error("邮箱未注册");

            String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            passwordResetService.saveCode(email, code);
            mailService.send(email, "重置密码验证码", "您的验证码是：" + code);
            return Result.success("验证码已发送");
        } catch (Exception e) {
            return Result.error("发送验证码失败：" + e.getMessage());
        }
    }

    @PostMapping("/forgot-password/reset")
    public Result<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");
        if (!passwordResetService.verifyCode(email, code)) {
            return Result.error("验证码错误或已过期");
        }
        try {
            userService.resetPassword(email, newPassword);
            return Result.success("密码重置成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}