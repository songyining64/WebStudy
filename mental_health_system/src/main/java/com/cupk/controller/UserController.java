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
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO dto) {
        String token = userService.login(dto);
        return Result.success(token);
    }

    @PostMapping("/forgot-password/code")
    public Result<?> sendResetCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        User user = userService.getByEmail(email);
        if (user == null)
            return Result.error("邮箱未注册");
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        passwordResetService.saveCode(email, code);
        mailService.send(email, "重置密码验证码", "您的验证码是：" + code);
        return Result.success("验证码已发送");
    }

    @PostMapping("/forgot-password/reset")
    public Result<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String newPassword = body.get("newPassword");
        if (!passwordResetService.verifyCode(email, code)) {
            return Result.error("验证码错误或已过期");
        }
        userService.resetPassword(email, newPassword);
        return Result.success("密码重置成功");
    }
}