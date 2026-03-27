package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.menu.common.PageResult;
import com.menu.dto.DailyMessageDTO;
import com.menu.entity.DailyMessage;
import com.menu.exception.BusinessException;
import com.menu.mapper.DailyMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMessageService {
    
    private final DailyMessageMapper dailyMessageMapper;
    
    public PageResult<DailyMessage> page(int current, int size, String keyword) {
        log.debug("分页查询每日情话: current={}, size={}, keyword={}", current, size, keyword);
        IPage<DailyMessage> page = dailyMessageMapper.selectPage(
            new Page<>(current, size),
            new LambdaQueryWrapper<DailyMessage>()
                .like(keyword != null && !keyword.isEmpty(), DailyMessage::getContent, keyword)
                .or()
                .like(keyword != null && !keyword.isEmpty(), DailyMessage::getAuthor, keyword)
                .orderByDesc(DailyMessage::getShowDate)
        );
        log.debug("查询到 {} 条每日情话记录", page.getTotal());
        return PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }
    
    public DailyMessage getById(Long id) {
        log.debug("获取每日情话详情: id={}", id);
        return dailyMessageMapper.selectById(id);
    }
    
    public DailyMessage getToday() {
        log.debug("获取今日情话");
        LocalDate today = LocalDate.now();
        DailyMessage message = dailyMessageMapper.selectByShowDate(today);
        if (message == null) {
            log.debug("今日没有配置情话，随机获取一条历史情话");
            message = dailyMessageMapper.selectRandom();
        }
        return message;
    }
    
    public void save(DailyMessageDTO dto) {
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BusinessException("情话内容不能为空");
        }
        if (dto.getShowDate() == null) {
            throw new BusinessException("展示日期不能为空");
        }
        
        DailyMessage message = new DailyMessage();
        BeanUtils.copyProperties(dto, message);
        if (dto.getId() == null) {
            dailyMessageMapper.insert(message);
            log.info("新增每日情话成功: id={}, showDate={}", message.getId(), dto.getShowDate());
        } else {
            dailyMessageMapper.updateById(message);
            log.info("更新每日情话成功: id={}, showDate={}", dto.getId(), dto.getShowDate());
        }
    }
    
    public void delete(Long id) {
        DailyMessage message = dailyMessageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException("每日情话不存在");
        }
        
        dailyMessageMapper.deleteById(id);
        log.info("删除每日情话成功: id={}", id);
    }
}
