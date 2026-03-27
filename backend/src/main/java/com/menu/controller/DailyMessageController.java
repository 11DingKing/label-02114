package com.menu.controller;

import com.menu.common.PageResult;
import com.menu.common.Result;
import com.menu.config.AdminRequired;
import com.menu.dto.DailyMessageDTO;
import com.menu.entity.DailyMessage;
import com.menu.service.DailyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "每日情话管理", description = "每日情话的增删改查、获取今日情话")
@RestController
@RequestMapping("/api/daily-message")
@RequiredArgsConstructor
public class DailyMessageController {
    
    private final DailyMessageService dailyMessageService;
    
    @Operation(summary = "分页查询每日情话列表")
    @GetMapping("/list")
    public Result<PageResult<DailyMessage>> list(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(dailyMessageService.page(current, size, keyword));
    }
    
    @Operation(summary = "获取今日情话", description = "获取当天的情话，如果没有则随机返回一条历史情话")
    @GetMapping("/today")
    public Result<DailyMessage> getToday() {
        return Result.success(dailyMessageService.getToday());
    }
    
    @Operation(summary = "获取每日情话详情")
    @GetMapping("/{id}")
    public Result<DailyMessage> getById(@Parameter(description = "每日情话ID") @PathVariable Long id) {
        return Result.success(dailyMessageService.getById(id));
    }
    
    @AdminRequired
    @Operation(summary = "保存每日情话", description = "新增或更新每日情话信息")
    @PostMapping("/save")
    public Result<Void> save(@Valid @RequestBody DailyMessageDTO dto) {
        dailyMessageService.save(dto);
        return Result.success();
    }
    
    @AdminRequired
    @Operation(summary = "删除每日情话")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "每日情话ID") @PathVariable Long id) {
        dailyMessageService.delete(id);
        return Result.success();
    }
}
