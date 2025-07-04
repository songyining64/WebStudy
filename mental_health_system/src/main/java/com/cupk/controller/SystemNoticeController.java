package com.cupk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cupk.entity.SystemNotice;
import com.cupk.service.Result;
import com.cupk.service.SystemNoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

/**
 * 系统公告控制器 - 用户端
 */
@CrossOrigin(origins = { "http://localhost:5173", "http://127.0.0.1:5173" }, allowCredentials = "true")
@RestController
@RequestMapping("/api/notices")
public class SystemNoticeController {
    private static final Logger logger = LoggerFactory.getLogger(SystemNoticeController.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private SystemNoticeService systemNoticeService;

    /**
     * 获取有效的系统公告（分页）
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping
    public Result<IPage<SystemNotice>> getActiveSystemNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            logger.info("获取系统公告列表请求: page={}, size={}", page, size);
            IPage<SystemNotice> notices = systemNoticeService.pageActiveSystemNotices(page, size);

            // 记录返回数据的时间格式
            if (notices != null && notices.getRecords() != null && !notices.getRecords().isEmpty()) {
                SystemNotice firstNotice = notices.getRecords().get(0);
                logger.info("返回的第一条公告ID: {}, 标题: {}", firstNotice.getId(), firstNotice.getTitle());

                if (firstNotice.getCreateTime() != null) {
                    logger.info("创建时间格式: {}, 原始类型: {}",
                            DATE_FORMAT.format(firstNotice.getCreateTime()),
                            firstNotice.getCreateTime().getClass().getName());
                }
            }

            return Result.success(notices);
        } catch (Exception e) {
            logger.error("获取系统公告列表失败", e);
            return Result.error("获取系统公告列表失败");
        }
    }

    /**
     * 获取最近的系统公告
     * 
     * @param limit 限制数量
     * @return 公告列表
     */
    @GetMapping("/recent")
    public Result<List<SystemNotice>> getRecentSystemNotices(
            @RequestParam(defaultValue = "5") Integer limit) {
        try {
            logger.info("获取最近公告请求: limit={}", limit);
            List<SystemNotice> notices = systemNoticeService.getRecentActiveNotices(limit);

            // 记录返回数据
            if (notices != null && !notices.isEmpty()) {
                logger.info("获取到{}条公告", notices.size());
                SystemNotice firstNotice = notices.get(0);
                logger.info("第一条公告: {}", firstNotice);

                if (firstNotice.getCreateTime() != null) {
                    logger.info("时间详情 - 创建时间: {}, 原始类型: {}",
                            DATE_FORMAT.format(firstNotice.getCreateTime()),
                            firstNotice.getCreateTime().getClass().getName());
                }
            } else {
                logger.info("未获取到公告");
            }

            return Result.success(notices);
        } catch (Exception e) {
            logger.error("获取最近公告失败", e);
            return Result.error("获取最近公告失败");
        }
    }

    /**
     * 获取系统公告详情
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    public Result<SystemNotice> getSystemNotice(@PathVariable Long id) {
        try {
            logger.info("获取公告详情请求: id={}", id);
            SystemNotice notice = systemNoticeService.getSystemNoticeById(id);
            if (notice == null || notice.getStatus() != 1) {
                logger.warn("公告不存在或已失效: id={}", id);
                return Result.error("系统公告不存在或已失效");
            }

            // 记录返回的公告时间格式
            if (notice.getCreateTime() != null) {
                logger.info("公告创建时间: {}, 格式化后: {}, 原始类型: {}",
                        notice.getCreateTime(),
                        DATE_FORMAT.format(notice.getCreateTime()),
                        notice.getCreateTime().getClass().getName());
            }

            return Result.success(notice);
        } catch (Exception e) {
            logger.error("获取系统公告详情失败", e);
            return Result.error("获取系统公告详情失败");
        }
    }

    /**
     * 获取服务器当前时间
     * 这个API可以用来检查服务器时间和前端时间的差异
     * 
     * @return 当前服务器时间
     */
    @GetMapping("/server-time")
    public Result<Map<String, Object>> getServerTime() {
        try {
            Date currentTime = new Date();
            Map<String, Object> result = new HashMap<>();
            result.put("serverTime", currentTime);
            result.put("timestamp", currentTime.getTime());
            result.put("formattedTime", DATE_FORMAT.format(currentTime));

            logger.info("获取服务器时间: {}, 时间戳: {}", DATE_FORMAT.format(currentTime), currentTime.getTime());
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取服务器时间失败", e);
            return Result.error("获取服务器时间失败");
        }
    }
}