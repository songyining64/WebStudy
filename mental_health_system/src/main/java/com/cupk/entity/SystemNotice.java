package com.cupk.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import java.sql.Timestamp;

/**
 * 系统公告实体类
 */
@Data
@ToString
public class SystemNotice {
    private Long id;
    private String title;
    private String content;

    // 使用Timestamp类型并添加Jackson格式化注解
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updateTime;

    private Integer status; // 1-有效，0-无效
}