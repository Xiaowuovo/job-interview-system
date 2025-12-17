package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Question;
import com.interview.entity.QuestionFavorite;
import com.interview.service.QuestionFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题目收藏控制器
 */
@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class QuestionFavoriteController {

    @Autowired
    private QuestionFavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<QuestionFavorite> addFavorite(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        Long questionId = Long.valueOf(params.get("questionId").toString());
        String notes = params.get("notes") != null ? params.get("notes").toString() : null;

        QuestionFavorite favorite = favoriteService.addFavorite(userId, questionId, notes);
        return Result.success(favorite);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long questionId) {
        boolean removed = favoriteService.removeFavorite(userId, questionId);
        if (removed) {
            return Result.success(null);
        }
        return Result.error("取消收藏失败");
    }

    /**
     * 获取用户收藏的题目列表
     */
    @GetMapping("/user/{userId}/questions")
    public Result<List<Question>> getUserFavoriteQuestions(@PathVariable Long userId) {
        List<Question> questions = favoriteService.getUserFavoriteQuestions(userId);
        return Result.success(questions);
    }

    /**
     * 获取用户收藏记录
     */
    @GetMapping("/user/{userId}")
    public Result<List<QuestionFavorite>> getUserFavorites(@PathVariable Long userId) {
        List<QuestionFavorite> favorites = favoriteService.getUserFavorites(userId);
        return Result.success(favorites);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check")
    public Result<Boolean> isFavorited(@RequestParam Long userId, @RequestParam Long questionId) {
        boolean favorited = favoriteService.isFavorited(userId, questionId);
        return Result.success(favorited);
    }

    /**
     * 获取收藏数量
     */
    @GetMapping("/count/{userId}")
    public Result<Long> getFavoriteCount(@PathVariable Long userId) {
        long count = favoriteService.getFavoriteCount(userId);
        return Result.success(count);
    }

    /**
     * 批量取消收藏
     */
    @PostMapping("/remove-batch")
    public Result<Integer> removeFavorites(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Long> questionIds = (List<Long>) params.get("questionIds");

        int count = favoriteService.removeFavorites(userId, questionIds);
        return Result.success(count);
    }
}
