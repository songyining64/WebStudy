package com.cupk.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    // 密码必须包含至少8个字符，至少一个字母和一个数字
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    /**
     * 验证密码是否符合规则
     * 
     * @param password 密码
     * @return 验证结果
     */
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 获取密码规则说明
     * 
     * @return 密码规则说明
     */
    public String getPasswordRequirements() {
        return "密码必须包含至少8个字符，至少一个字母和一个数字";
    }
}