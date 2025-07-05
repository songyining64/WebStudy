package com.cupk.service;

import com.cupk.entity.SystemNotice;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SystemNoticeService extends IService<SystemNotice> {
    /**
     * 分页查询系统公告，支持按标题和内容搜索
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词（标题或内容）
     * @return 分页结果
     */
    IPage<SystemNotice> pageSystemNotices(int page, int size, String keyword);

    /**
     * 获取有效的系统公告
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<SystemNotice> pageActiveSystemNotices(int page, int size);

    /**
     * 获取最近的有效系统公告
     * 
     * @param limit 限制数量
     * @return 公告列表
     */
    List<SystemNotice> getRecentActiveNotices(int limit);

    /**
     * 添加系统公告
     * 
     * @param notice 公告信息
     * @return 是否添加成功
     */
    boolean addSystemNotice(SystemNotice notice);

    /**
     * 更新系统公告
     * 
     * @param notice 公告信息
     * @return 是否更新成功
     */
    boolean updateSystemNotice(SystemNotice notice);

    /**
     * 删除系统公告
     * 
     * @param id 公告ID
     * @return 是否删除成功
     */
    boolean deleteSystemNotice(Long id);

    /**
     * 获取系统公告详情
     * 
     * @param id 公告ID
     * @return 公告详情
     */
    SystemNotice getSystemNoticeById(Long id);
}