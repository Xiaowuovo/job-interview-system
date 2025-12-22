package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.DailyQuestion;
import com.interview.service.DailyQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 每日一题控制器
 */
@RestController
@RequestMapping("/daily-question")
@RequiredArgsConstructor
public class DailyQuestionController {

    private final DailyQuestionService dailyQuestionService;

    /**
     * 获取今日一题
     */
    @GetMapping("/today")
    public Result<Map<String, Object>> getTodayQuestion() {
        Map<String, Object> result = dailyQuestionService.getTodayQuestion();
        return Result.success(result);
    }

    /**
     * 获取最近7天的每日一题
     */
    @GetMapping("/recent")
    public Result<List<DailyQuestion>> getRecentDailyQuestions() {
        List<DailyQuestion> questions = dailyQuestionService.getRecentDailyQuestions();
        return Result.success(questions);
    }

    /**
     * 手动设置每日一题（管理员）
     */
    @PostMapping("/set")
    public Result<DailyQuestion> setDailyQuestion(@RequestBody Map<String, Object> params) {
        LocalDate date = LocalDate.parse(params.get("date").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());

        DailyQuestion daily = dailyQuestionService.setDailyQuestion(date, questionId);
        return Result.success(daily);
    }

    /**
     * 记录完成
     */
    @PostMapping("/complete")
    public Result<Void> recordComplete(@RequestBody Map<String, Object> params) {
        LocalDate date = LocalDate.parse(params.get("date").toString());
        dailyQuestionService.recordComplete(date);
        return Result.success(null);
    }
}
