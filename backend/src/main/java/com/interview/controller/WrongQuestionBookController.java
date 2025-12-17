package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.WrongQuestionBook;
import com.interview.service.WrongQuestionBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 错题本控制器
 */
@RestController
@RequestMapping("/api/wrong-questions")
@CrossOrigin(origins = "*")
public class WrongQuestionBookController {

    @Autowired
    private WrongQuestionBookService wrongQuestionBookService;

    /**
     * 添加错题
     */
    @PostMapping("/add")
    public Result<WrongQuestionBook> addWrongQuestion(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());

        WrongQuestionBook wrongQuestion = wrongQuestionBookService.addWrongQuestion(userId, questionId);
        return Result.success(wrongQuestion);
    }

    /**
     * 获取用户的错题本
     */
    @GetMapping("/user/{userId}")
    public Result<List<WrongQuestionBook>> getUserWrongQuestions(@PathVariable Long userId) {
        return Result.success(wrongQuestionBookService.getUserWrongQuestions(userId));
    }

    /**
     * 获取用户未掌握的错题
     */
    @GetMapping("/user/{userId}/unmastered")
    public Result<List<WrongQuestionBook>> getUnmasteredQuestions(@PathVariable Long userId) {
        return Result.success(wrongQuestionBookService.getUnmasteredQuestions(userId));
    }

    /**
     * 获取需要复习的错题
     */
    @GetMapping("/user/{userId}/need-review")
    public Result<List<WrongQuestionBook>> getQuestionsNeedReview(@PathVariable Long userId) {
        return Result.success(wrongQuestionBookService.getQuestionsNeedReview(userId));
    }

    /**
     * 记录复习
     */
    @PostMapping("/review")
    public Result<WrongQuestionBook> recordReview(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());
        Boolean mastered = Boolean.valueOf(params.get("mastered").toString());

        WrongQuestionBook updated = wrongQuestionBookService.recordReview(userId, questionId, mastered);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("错题记录不存在");
    }

    /**
     * 添加笔记
     */
    @PostMapping("/notes")
    public Result<WrongQuestionBook> addNotes(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());
        String notes = params.get("notes").toString();

        WrongQuestionBook updated = wrongQuestionBookService.addNotes(userId, questionId, notes);
        if (updated != null) {
            return Result.success(updated);
        }
        return Result.error("错题记录不存在");
    }

    /**
     * 从错题本移除
     */
    @DeleteMapping("/remove")
    public Result<Void> removeWrongQuestion(@RequestParam Long userId, @RequestParam Long questionId) {
        boolean removed = wrongQuestionBookService.removeWrongQuestion(userId, questionId);
        if (removed) {
            return Result.success(null);
        }
        return Result.error("删除失败");
    }

    /**
     * 检查是否在错题本中
     */
    @GetMapping("/check")
    public Result<Boolean> isInWrongBook(@RequestParam Long userId, @RequestParam Long questionId) {
        boolean inBook = wrongQuestionBookService.isInWrongBook(userId, questionId);
        return Result.success(inBook);
    }

    /**
     * 获取错题统计
     */
    @GetMapping("/statistics/{userId}")
    public Result<Map<String, Object>> getWrongQuestionStatistics(@PathVariable Long userId) {
        return Result.success(wrongQuestionBookService.getWrongQuestionStatistics(userId));
    }

    /**
     * 清空已掌握的错题
     */
    @DeleteMapping("/clear-mastered/{userId}")
    public Result<Integer> clearMasteredQuestions(@PathVariable Long userId) {
        int count = wrongQuestionBookService.clearMasteredQuestions(userId);
        return Result.success(count);
    }

    /**
     * 批量标记为已掌握
     */
    @PostMapping("/mark-mastered")
    public Result<Integer> markAsMastered(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Long> questionIds = (List<Long>) params.get("questionIds");

        int count = wrongQuestionBookService.markAsMastered(userId, questionIds);
        return Result.success(count);
    }
}
