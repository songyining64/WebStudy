package com.cupk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cupk.entity.PasswordResetCode;
import com.cupk.mapper.PasswordResetCodeMapper;
import com.cupk.service.PasswordResetService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private final PasswordResetCodeMapper codeMapper;

    public PasswordResetServiceImpl(PasswordResetCodeMapper codeMapper) {
        this.codeMapper = codeMapper;
    }

    @Override
    public void saveCode(String email, String code) {
        // 删除旧的验证码
        LambdaQueryWrapper<PasswordResetCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordResetCode::getEmail, email);
        codeMapper.delete(wrapper);
        // 保存新验证码
        PasswordResetCode entity = new PasswordResetCode();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setExpireTime(LocalDateTime.now().plusMinutes(10));
        codeMapper.insert(entity);
    }

    @Override
    public boolean verifyCode(String email, String code) {
        LambdaQueryWrapper<PasswordResetCode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PasswordResetCode::getEmail, email)
                .eq(PasswordResetCode::getCode, code)
                .gt(PasswordResetCode::getExpireTime, LocalDateTime.now());
        return codeMapper.selectCount(wrapper) > 0;
    }
}