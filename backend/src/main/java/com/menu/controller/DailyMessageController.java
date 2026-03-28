package com.menu.controller;

import com.menu.common.Result;
import com.menu.config.AdminRequired;
import com.menu.entity.DailyMessage;
import com.menu.service.DailyMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "每日情话管理", description = "每日情话的增删改查和今日情话获取")
@RestController
@RequestMapping("/api/daily-message")
@RequiredArgsConstructor
public class DailyMessageController {
    
    private final DailyMessageService dailyMessageService;
    
    @Operation(summary = "获取今日情话", description = "获取当天的情话，如果没有则随机返回一条历史情话")
    @GetMapping("/today")
    public Result<DailyMessage> getTodayMessage() {
        DailyMessage message = dailyMessageService.getTodayMessage();
        return Result.success(message);
    }
    
    @AdminRequired
    @Operation(summary = "获取所有每日情话列表")
    @GetMapping("/list")
    public Result<List<DailyMessage>> list() {
        return Result.success(dailyMessageService.list());
    }
    
    @AdminRequired
    @Operation(summary = "根据ID获取每日情话")
    @GetMapping("/{id}")
    public Result<DailyMessage> getById(@Parameter(description = "每日情话ID") @PathVariable Long id) {
        return Result.success(dailyMessageService.getById(id));
    }
    
    @AdminRequired
    @Operation(summary = "保存每日情话", description = "新增或更新每日情话信息")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody DailyMessage dailyMessage) {
        dailyMessageService.save(dailyMessage);
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
