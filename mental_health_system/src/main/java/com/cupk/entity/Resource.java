package com.cupk.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("resource")
public class Resource {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String type;
    private String contentUrl;
    private String emotionTag;
    private String description;
    private LocalDateTime createTime;
}