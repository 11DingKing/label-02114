package com.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.menu.entity.DailyMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDate;

@Mapper
public interface DailyMessageMapper extends BaseMapper<DailyMessage> {
    
    @Select("SELECT * FROM daily_message WHERE show_date = #{date} LIMIT 1")
    DailyMessage selectByShowDate(LocalDate date);
    
    @Select("SELECT * FROM daily_message ORDER BY RAND() LIMIT 1")
    DailyMessage selectRandom();
}
