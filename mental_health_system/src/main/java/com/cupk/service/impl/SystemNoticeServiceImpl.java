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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Service
public class SystemNoticeServiceImpl extends ServiceImpl<SystemNoticeMapper, SystemNotice>
        implements SystemNoticeService {

    private static final Logger log = LoggerFactory.getLogger(SystemNoticeServiceImpl.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        IPage<SystemNotice> result = this.baseMapper.pageActiveSystemNotices(pageParam);

        // 记录返回的公告对象数据
        if (result != null && result.getRecords() != null && !result.getRecords().isEmpty()) {
            SystemNotice firstNotice = result.getRecords().get(0);
            log.info("获取公告 - 第一条公告数据: {}", firstNotice);

            if (firstNotice.getCreateTime() != null) {
                log.info("时间详情 - 创建时间: {}, 小时:{}, 分钟:{}, 秒:{}",
                        DATE_FORMAT.format(firstNotice.getCreateTime()),
                        firstNotice.getCreateTime().toLocalDateTime().getHour(),
                        firstNotice.getCreateTime().toLocalDateTime().getMinute(),
                        firstNotice.getCreateTime().toLocalDateTime().getSecond());
            }
        }

        return result;
    }

    @Override
    public List<SystemNotice> getRecentActiveNotices(int limit) {
        LambdaQueryWrapper<SystemNotice> queryWrapper = new LambdaQueryWrapper<SystemNotice>()
                .eq(SystemNotice::getStatus, 1)
                .orderByDesc(SystemNotice::getCreateTime)
                .last("LIMIT " + limit);
        List<SystemNotice> notices = this.list(queryWrapper);

        // 记录返回的公告对象数据
        if (notices != null && !notices.isEmpty()) {
            SystemNotice firstNotice = notices.get(0);
            log.info("获取最近公告 - 第一条公告数据: {}", firstNotice);

            if (firstNotice.getCreateTime() != null) {
                log.info("时间详情 - 创建时间: {}, 小时:{}, 分钟:{}, 秒:{}",
                        DATE_FORMAT.format(firstNotice.getCreateTime()),
                        firstNotice.getCreateTime().toLocalDateTime().getHour(),
                        firstNotice.getCreateTime().toLocalDateTime().getMinute(),
                        firstNotice.getCreateTime().toLocalDateTime().getSecond());
            }
        }

        return notices;
    }

    @Override
    public boolean addSystemNotice(SystemNotice notice) {
        if (notice == null || !StringUtils.hasText(notice.getTitle()) || !StringUtils.hasText(notice.getContent())) {
            return false;
        }

        // 使用java.sql.Timestamp存储精确的时间，包括时分秒
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        notice.setCreateTime(now);
        notice.setUpdateTime(now);

        // 记录日志，确认时间格式
        log.info("添加系统公告，时间设置为: {}", now);
        log.info("时间详情 - 小时:{}, 分钟:{}, 秒:{}",
                now.toLocalDateTime().getHour(),
                now.toLocalDateTime().getMinute(),
                now.toLocalDateTime().getSecond());

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

        // 使用java.sql.Timestamp更新时间，确保精确到秒
        java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
        notice.setUpdateTime(now);

        // 记录更新时间
        log.info("更新系统公告，时间设置为: {}", now);

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
        SystemNotice notice = this.getById(id);

        // 记录获取到的公告对象数据
        if (notice != null) {
            log.info("获取公告详情 - ID:{}, 数据: {}", id, notice);

            if (notice.getCreateTime() != null) {
                log.info("时间详情 - 创建时间: {}, 小时:{}, 分钟:{}, 秒:{}",
                        DATE_FORMAT.format(notice.getCreateTime()),
                        notice.getCreateTime().toLocalDateTime().getHour(),
                        notice.getCreateTime().toLocalDateTime().getMinute(),
                        notice.getCreateTime().toLocalDateTime().getSecond());
            }
        }

        return notice;
    }
}