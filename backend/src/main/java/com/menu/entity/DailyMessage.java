package com.menu.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_message")
public class DailyMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String content;
    
    private String author;
    
    private LocalDate showDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
