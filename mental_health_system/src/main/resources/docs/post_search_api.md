# 社区帖子搜索 API 文档

本文档描述了心理健康系统社区帖子的搜索功能 API。

## 基本帖子列表 (支持简单搜索)

### 请求

```
GET /api/post
```

### 参数

| 参数名 | 类型 | 必填 | 描述 |
|-------|-----|-----|------|
| current | int | 否 | 当前页码，默认为1 |
| page | int | 否 | 当前页码（与current二选一，优先使用current） |
| size | int | 否 | 每页数量，默认为10 |
| keyword | string | 否 | 搜索关键词，将在标题、内容和标签中搜索 |
| category | string | 否 | 分类名称，筛选指定分类的帖子 |
| sortBy | string | 否 | 排序字段，可选值：create_time（创建时间）、like_count（点赞数）、comment_count（评论数） |
| sortOrder | string | 否 | 排序方式，可选值：ASC（升序）、DESC（降序），默认为DESC |

### 响应

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "title": "帖子标题",
        "content": "帖子内容",
        "images": "图片URL，多个以逗号分隔",
        "tags": "标签1,标签2",
        "category": "心理健康",
        "likeCount": 10,
        "commentCount": 5,
        "status": 1,
        "createTime": "2023-05-01 12:00:00",
        "updateTime": "2023-05-01 12:00:00",
        "username": "用户名",
        "avatar": "用户头像URL"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

## 关键词搜索

### 请求

```
GET /api/post/search
```

### 参数

| 参数名 | 类型 | 必填 | 描述 |
|-------|-----|-----|------|
| keyword | string | 是 | 搜索关键词，将在标题、内容和标签中搜索 |
| current | int | 否 | 当前页码，默认为1 |
| page | int | 否 | 当前页码（与current二选一，优先使用current） |
| size | int | 否 | 每页数量，默认为10 |

### 响应

与基本帖子列表相同。

## 分类筛选

### 请求

```
GET /api/post/category/{category}
```

### 参数

| 参数名 | 类型 | 必填 | 描述 |
|-------|-----|-----|------|
| category | string | 是 | 分类名称，作为路径参数 |
| current | int | 否 | 当前页码，默认为1 |
| page | int | 否 | 当前页码（与current二选一，优先使用current） |
| size | int | 否 | 每页数量，默认为10 |

### 响应

与基本帖子列表相同。

## 高级搜索

### 请求

```
GET /api/post/advanced-search
```

### 参数

| 参数名 | 类型 | 必填 | 描述 |
|-------|-----|-----|------|
| keyword | string | 否 | 搜索关键词，将在标题、内容和标签中搜索 |
| category | string | 否 | 分类名称，筛选指定分类的帖子 |
| sortBy | string | 否 | 排序字段，可选值：create_time（创建时间）、like_count（点赞数）、comment_count（评论数） |
| sortOrder | string | 否 | 排序方式，可选值：ASC（升序）、DESC（降序），默认为DESC |
| current | int | 否 | 当前页码，默认为1 |
| page | int | 否 | 当前页码（与current二选一，优先使用current） |
| size | int | 否 | 每页数量，默认为10 |

### 响应

与基本帖子列表相同。

## 常见分类

系统预设的常见分类包括：

- 心理健康
- 学习方法
- 情感
- 压力管理
- 社交
- 职业发展
- 健康
- 生活
- 其他

## 示例

1. 搜索包含"压力"关键词的帖子：
   ```
   GET /api/post/search?keyword=压力
   ```

2. 获取"心理健康"分类的帖子：
   ```
   GET /api/post/category/心理健康
   ```

3. 高级搜索：按点赞数降序排列的"学习方法"分类帖子：
   ```
   GET /api/post/advanced-search?category=学习方法&sortBy=like_count&sortOrder=DESC
   ```

4. 简化版搜索：通过基本列表API进行搜索：
   ```
   GET /api/post?keyword=考试&category=学习方法
   ``` 