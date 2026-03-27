package com.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyMessageDTO {
    private Long id;
    @NotBlank(message = "情话内容不能为空")
    private String content;
    private String author;
    @NotNull(message = "展示日期不能为空")
    private LocalDate showDate;
}
