package com.cupk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cupk.entity.SystemNotice;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 系统公告Mapper接口
 */
@Repository
public interface SystemNoticeMapper extends BaseMapper<SystemNotice> {
    /**
     * 分页查询系统公告，支持按标题和内容搜索
     * 
     * @param page    分页参数
     * @param keyword 关键词（标题或内容）
     * @return 分页结果
     */
    IPage<SystemNotice> pageSystemNotices(Page<SystemNotice> page, @Param("keyword") String keyword);

    /**
     * 获取有效的系统公告
     * 
     * @param page 分页参数
     * @return 分页结果
     */
    IPage<SystemNotice> pageActiveSystemNotices(Page<SystemNotice> page);
}