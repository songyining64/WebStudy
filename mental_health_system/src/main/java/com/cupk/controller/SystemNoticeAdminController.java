package com.cupk.controller;

import com.cupk.entity.SystemNotice;
import com.cupk.service.SystemNoticeService;
import com.cupk.service.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/notices")
public class SystemNoticeAdminController {

    @Autowired
    private SystemNoticeService systemNoticeService;

    /**
     * 分页查询系统公告
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词（标题或内容）
     * @return 分页结果
     */
    @GetMapping
    public Result<IPage<SystemNotice>> pageSystemNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            IPage<SystemNotice> notices = systemNoticeService.pageSystemNotices(page, size, keyword);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.fail("查询系统公告失败: " + e.getMessage());
        }
    }

    /**
     * 获取有效的系统公告
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/active")
    public Result<IPage<SystemNotice>> pageActiveSystemNotices(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            IPage<SystemNotice> notices = systemNoticeService.pageActiveSystemNotices(page, size);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.fail("查询有效公告失败: " + e.getMessage());
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
            List<SystemNotice> notices = systemNoticeService.getRecentActiveNotices(limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.fail("获取最近公告失败: " + e.getMessage());
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
            SystemNotice notice = systemNoticeService.getSystemNoticeById(id);
            if (notice == null) {
                return Result.fail("系统公告不存在");
            }
            return Result.success(notice);
        } catch (Exception e) {
            return Result.fail("获取系统公告失败: " + e.getMessage());
        }
    }

    /**
     * 添加系统公告
     * 
     * @param notice 公告信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Boolean> addSystemNotice(@RequestBody SystemNotice notice) {
        try {
            boolean success = systemNoticeService.addSystemNotice(notice);
            if (success) {
                return Result.success("添加系统公告成功", true);
            } else {
                return Result.fail("添加系统公告失败");
            }
        } catch (Exception e) {
            return Result.fail("添加系统公告失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统公告
     * 
     * @param id     公告ID
     * @param notice 公告信息
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Boolean> updateSystemNotice(@PathVariable Long id, @RequestBody SystemNotice notice) {
        try {
            notice.setId(id);
            boolean success = systemNoticeService.updateSystemNotice(notice);
            if (success) {
                return Result.success("更新系统公告成功", true);
            } else {
                return Result.fail("更新系统公告失败");
            }
        } catch (Exception e) {
            return Result.fail("更新系统公告失败: " + e.getMessage());
        }
    }

    /**
     * 删除系统公告
     * 
     * @param id 公告ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteSystemNotice(@PathVariable Long id) {
        try {
            boolean success = systemNoticeService.deleteSystemNotice(id);
            if (success) {
                return Result.success("删除系统公告成功", true);
            } else {
                return Result.fail("删除系统公告失败");
            }
        } catch (Exception e) {
            return Result.fail("删除系统公告失败: " + e.getMessage());
        }
    }
}