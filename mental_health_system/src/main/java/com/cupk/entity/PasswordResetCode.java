package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("password_reset_code")
public class PasswordResetCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String email;
    private String code;
    private LocalDateTime expireTime;
}