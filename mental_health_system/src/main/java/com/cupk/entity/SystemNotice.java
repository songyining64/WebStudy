package com.cupk.entity;

import lombok.Data;
import java.util.Date;

/**
 * 系统公告实体类
 */
@Data
public class SystemNotice {
    private Long id;
    private String title;
    private String content;
    private Date createTime;
    private Date updateTime;
    private Integer status; // 1-有效，0-无效
}