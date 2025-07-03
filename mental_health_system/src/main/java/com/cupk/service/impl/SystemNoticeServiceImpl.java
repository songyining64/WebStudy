package com.cupk.service.impl;

import com.cupk.entity.SystemNotice;
import com.cupk.mapper.SystemNoticeMapper;
import com.cupk.service.SystemNoticeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class SystemNoticeServiceImpl extends ServiceImpl<SystemNoticeMapper, SystemNotice>
        implements SystemNoticeService {

    @Override
    public IPage<SystemNotice> pageSystemNotices(int page, int size, String keyword) {
        Page<SystemNotice> pageParam = new Page<>(page, size);

        if (StringUtils.hasText(keyword)) {
            // 使用自定义方法搜索
            return this.baseMapper.pageSystemNotices(pageParam, keyword);
        } else {
            // 使用默认方法
            LambdaQueryWrapper<SystemNotice> queryWrapper = new LambdaQueryWrapper<SystemNotice>()
                    .orderByDesc(SystemNotice::getCreateTime);
            return this.baseMapper.selectPage(pageParam, queryWrapper);
        }
    }

    @Override
    public IPage<SystemNotice> pageActiveSystemNotices(int page, int size) {
        Page<SystemNotice> pageParam = new Page<>(page, size);
        return this.baseMapper.pageActiveSystemNotices(pageParam);
    }

    @Override
    public List<SystemNotice> getRecentActiveNotices(int limit) {
        LambdaQueryWrapper<SystemNotice> queryWrapper = new LambdaQueryWrapper<SystemNotice>()
                .eq(SystemNotice::getStatus, 1)
                .orderByDesc(SystemNotice::getCreateTime)
                .last("LIMIT " + limit);
        return this.list(queryWrapper);
    }

    @Override
    public boolean addSystemNotice(SystemNotice notice) {
        if (notice == null || !StringUtils.hasText(notice.getTitle()) || !StringUtils.hasText(notice.getContent())) {
            return false;
        }

        // 设置创建时间和更新时间
        Date now = new Date();
        notice.setCreateTime(now);
        notice.setUpdateTime(now);

        // 默认为有效状态
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }

        return this.save(notice);
    }

    @Override
    public boolean updateSystemNotice(SystemNotice notice) {
        if (notice == null || notice.getId() == null) {
            return false;
        }

        // 更新更新时间
        notice.setUpdateTime(new Date());

        return this.updateById(notice);
    }

    @Override
    public boolean deleteSystemNotice(Long id) {
        if (id == null) {
            return false;
        }
        return this.removeById(id);
    }

    @Override
    public SystemNotice getSystemNoticeById(Long id) {
        if (id == null) {
            return null;
        }
        return this.getById(id);
    }
}