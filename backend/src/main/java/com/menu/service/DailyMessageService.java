package com.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.menu.entity.DailyMessage;
import com.menu.exception.BusinessException;
import com.menu.mapper.DailyMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyMessageService {
    
    private final DailyMessageMapper dailyMessageMapper;
    
    public List<DailyMessage> list() {
        log.debug("查询所有每日情话列表");
        return dailyMessageMapper.selectList(
            new LambdaQueryWrapper<DailyMessage>().orderByDesc(DailyMessage::getShowDate)
        );
    }
    
    public DailyMessage getById(Long id) {
        return dailyMessageMapper.selectById(id);
    }
    
    public void save(DailyMessage dailyMessage) {
        // 校验内容
        if (dailyMessage.getContent() == null || dailyMessage.getContent().trim().isEmpty()) {
            throw new BusinessException("情话内容不能为空");
        }
        if (dailyMessage.getShowDate() == null) {
            throw new BusinessException("展示日期不能为空");
        }
        
        // 检查当天是否已有情话
        LambdaQueryWrapper<DailyMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DailyMessage::getShowDate, dailyMessage.getShowDate());
        if (dailyMessage.getId() != null) {
            // 更新时排除自身
            queryWrapper.ne(DailyMessage::getId, dailyMessage.getId());
        }
        DailyMessage existing = dailyMessageMapper.selectOne(queryWrapper);
        if (existing != null) {
            throw new BusinessException(dailyMessage.getShowDate() + " 已经配置过情话了");
        }
        
        if (dailyMessage.getId() == null) {
            dailyMessageMapper.insert(dailyMessage);
            log.info("新增每日情话成功: id={}, showDate={}", dailyMessage.getId(), dailyMessage.getShowDate());
        } else {
            dailyMessageMapper.updateById(dailyMessage);
            log.info("更新每日情话成功: id={}, showDate={}", dailyMessage.getId(), dailyMessage.getShowDate());
        }
    }
    
    public void delete(Long id) {
        DailyMessage dailyMessage = dailyMessageMapper.selectById(id);
        if (dailyMessage == null) {
            throw new BusinessException("每日情话不存在");
        }
        
        dailyMessageMapper.deleteById(id);
        log.info("删除每日情话成功: id={}", id);
    }
    
    public DailyMessage getTodayMessage() {
        LocalDate today = LocalDate.now();
        log.debug("获取今日情话, today: {}", today);
        
        // 先查找当天的情话
        LambdaQueryWrapper<DailyMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DailyMessage::getShowDate, today);
        DailyMessage todayMessage = dailyMessageMapper.selectOne(queryWrapper);
        
        if (todayMessage != null) {
            log.debug("找到今天的情话: {}", todayMessage.getContent());
            return todayMessage;
        }
        
        // 如果当天没有，随机获取一条历史情话
        log.debug("今天没有配置情话，随机获取一条历史情话");
        List<DailyMessage> allMessages = dailyMessageMapper.selectList(null);
        if (allMessages.isEmpty()) {
            log.debug("没有任何历史情话");
            return null;
        }
        
        Random random = new Random();
        int index = random.nextInt(allMessages.size());
        DailyMessage randomMessage = allMessages.get(index);
        log.debug("随机获取到的情话: {}", randomMessage.getContent());
        return randomMessage;
    }
}
