package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.PointsRecord;
import com.interview.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 积分控制器
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;

    /**
     * 每日签到
     */
    @PostMapping("/signin/{userId}")
    public Result<Map<String, Object>> dailySignIn(@PathVariable Long userId) {
        Map<String, Object> result = pointsService.dailySignIn(userId);
        // 统一返回成功，前端根据success字段判断是否已签到
        return Result.success(result);
    }

    /**
     * 获取用户积分统计
     */
    @GetMapping("/stats/{userId}")
    public Result<Map<String, Object>> getUserPointsStats(@PathVariable Long userId) {
        Map<String, Object> stats = pointsService.getUserPointsStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取用户积分记录
     */
    @GetMapping("/records/{userId}")
    public Result<List<PointsRecord>> getUserPointsRecords(@PathVariable Long userId) {
        List<PointsRecord> records = pointsService.getUserPointsRecords(userId);
        return Result.success(records);
    }

    /**
     * 添加积分（系统调用）
     */
    @PostMapping("/add")
    public Result<PointsRecord> addPoints(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String type = params.get("type").toString();
        String description = params.get("description").toString();

        PointsRecord record = pointsService.addPoints(userId, type, description);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("添加积分失败");
    }

    /**
     * 扣除积分（用于兑换）
     */
    @PostMapping("/deduct")
    public Result<Void> deductPoints(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Integer points = Integer.valueOf(params.get("points").toString());
        String description = params.get("description").toString();

        boolean success = pointsService.deductPoints(userId, points, description);
        if (success) {
            return Result.success(null);
        }
        return Result.error("积分不足");
    }
}
