package com.cupk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cupk.entity.EmotionRecord;

public interface EmotionRecordService extends IService<EmotionRecord> {
    /**
     * 添加情绪记录
     * 
     * @param record 情绪记录实体
     * @return 是否添加成功
     */
    boolean addRecord(EmotionRecord record);

    /**
     * 获取用户情绪记录列表（分页）
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 情绪记录分页结果
     */
    IPage<EmotionRecord> getUserRecords(Long userId, int page, int size);

    /**
     * 获取情绪记录总数
     *
     * @return 情绪记录总数
     */
    long count();

    /**
     * 分页查询情绪记录，支持按用户名和情绪类型搜索
     * 
     * @param page    页码
     * @param size    每页大小
     * @param keyword 关键词（用户名或情绪类型）
     * @return 分页结果
     */
    IPage<EmotionRecord> pageEmotionRecords(int page, int size, String keyword);

    /**
     * 删除情绪记录
     * 
     * @param id 记录ID
     * @return 是否删除成功
     */
    boolean deleteEmotionRecord(Long id);
}