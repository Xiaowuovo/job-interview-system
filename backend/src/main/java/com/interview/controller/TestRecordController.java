package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.TestRecord;
import com.interview.service.TestRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 测试记录控制器 - 优化版
 */
@RestController
@RequestMapping("/test-records")
@RequiredArgsConstructor
public class TestRecordController {

    private final TestRecordService testRecordService;

    /**
     * 获取用户的所有测试记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<TestRecord>> getUserTestRecords(@PathVariable Long userId) {
        return Result.success(testRecordService.getUserTestRecords(userId));
    }

    /**
     * 根据分类获取测试记录
     */
    @GetMapping("/user/{userId}/category/{category}")
    public Result<List<TestRecord>> getUserTestRecordsByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        return Result.success(testRecordService.getUserTestRecordsByCategory(userId, category));
    }

    /**
     * 提交测试记录
     */
    @PostMapping
    public Result<TestRecord> submitTestRecord(@RequestBody TestRecord record) {
        // 兼容前端发送的 correctCount 字段
        if (record.getCorrectAnswers() == null && record.getCorrectCount() != null) {
            record.setCorrectAnswers(record.getCorrectCount());
        }
        // 自动计算分数
        if (record.getTotalQuestions() != null && record.getTotalQuestions() > 0 && record.getCorrectAnswers() != null) {
            double score = (double) record.getCorrectAnswers() / record.getTotalQuestions() * 100;
            record.setScore(score);
        }
        TestRecord saved = testRecordService.submitTestRecord(record);
        return Result.success(saved);
    }

    /**
     * 获取测试记录详情
     */
    @GetMapping("/{id}")
    public Result<TestRecord> getTestRecordById(@PathVariable Long id) {
        return testRecordService.getTestRecordById(id)
                .map(Result::success)
                .orElse(Result.error("记录不存在"));
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/statistics/{userId}")
    public Result<Map<String, Object>> getStatistics(@PathVariable Long userId) {
        Map<String, Object> statistics = testRecordService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    /**
     * 获取最近的测试记录
     */
    @GetMapping("/user/{userId}/recent")
    public Result<List<TestRecord>> getRecentTestRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(testRecordService.getRecentTestRecords(userId, limit));
    }

    /**
     * 删除测试记录
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTestRecord(@PathVariable Long id) {
        boolean deleted = testRecordService.deleteTestRecord(id);
        if (deleted) {
            return Result.success(null);
        }
        return Result.error("删除失败");
    }

    /**
     * 获取用户在特定分类的平均分
     */
    @GetMapping("/average-score/{userId}/{category}")
    public Result<Double> getAverageScoreByCategory(
            @PathVariable Long userId,
            @PathVariable String category) {
        Double avgScore = testRecordService.getAverageScoreByCategory(userId, category);
        return Result.success(avgScore);
    }
}
