package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("password_reset_code")
public class PasswordResetCode {
    @TableId
    private Long id;
    private String email;
    private String code;
    private LocalDateTime expireTime;
}