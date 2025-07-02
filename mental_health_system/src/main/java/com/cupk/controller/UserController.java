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
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" }, allowCredentials = "true")
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
    public List<User> list(HttpServletRequest request) {
        if (!isAdmin(request))
            throw new RuntimeException("无权限");
        return userService.list();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request))
            throw new RuntimeException("无权限");
        return userService.getById(id);
    }

    @PostMapping
    public boolean add(@RequestBody User user, HttpServletRequest request) {
        if (!isAdmin(request))
            throw new RuntimeException("无权限");
        return userService.save(user);
    }

    @PutMapping
    public boolean update(@RequestBody User user, HttpServletRequest request) {
        if (!isAdmin(request))
            throw new RuntimeException("无权限");
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request))
            throw new RuntimeException("无权限");
        return userService.removeById(id);
    }

    @PostMapping("/register/code")
    public Result<?> sendRegisterCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            return Result.error("邮箱不能为空");
        }
        if (userService.findByEmail(email) != null) {
            return Result.error("邮箱已注册");
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        passwordResetService.saveCode(email, code);
        mailService.send(email, "注册验证码", "您的注册验证码是：" + code);
        return Result.success("验证码已发送");
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String code = body.get("code");
        if (username == null || password == null || email == null || code == null) {
            return Result.error("所有字段均不能为空");
        }
        if (!passwordResetService.verifyCode(email, code)) {
            return Result.error("验证码错误或已过期");
        }
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setEmail(email);
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
            User user = userService.findByEmail(dto.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("role", user.getRole());
            data.put("avatar", user.getAvatar() == null ? "" : user.getAvatar());
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

    // 工具方法：判断是否为管理员
    private boolean isAdmin(HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        return "admin".equals(role);
    }
}