package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Question;
import com.interview.entity.QuestionFavorite;
import com.interview.service.QuestionFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 题目收藏控制器
 */
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class QuestionFavoriteController {

    private final QuestionFavoriteService favoriteService;

    /**
     * 添加收藏（支持题目/知识点/教程多种类型）
     */
    @PostMapping("/add")
    public Result<QuestionFavorite> addFavorite(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String notes = params.get("notes") != null ? params.get("notes").toString() : null;
        String type = params.get("type") != null ? params.get("type").toString() : "question";

        if ("question".equals(type) && params.get("questionId") != null) {
            Long questionId = Long.valueOf(params.get("questionId").toString());
            QuestionFavorite fav = favoriteService.addFavorite(userId, questionId, notes);
            if (fav.getType() == null) { fav.setType("question"); fav.setItemId(questionId); }
            return Result.success(fav);
        } else {
            Long itemId = Long.valueOf(params.get("itemId").toString());
            QuestionFavorite fav = favoriteService.addFavoriteByType(userId, type, itemId, notes);
            return Result.success(fav);
        }
    }

    /**
     * 取消收藏（题目）
     */
    @DeleteMapping("/remove")
    public Result<Void> removeFavorite(@RequestParam Long userId,
                                       @RequestParam(required = false) Long questionId,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(required = false) Long itemId) {
        if (questionId != null) {
            favoriteService.removeFavorite(userId, questionId);
        } else if (type != null && itemId != null) {
            favoriteService.removeFavoriteByType(userId, type, itemId);
        }
        return Result.success(null);
    }

    /**
     * 检查通用类型是否已收藏
     */
    @GetMapping("/check-type")
    public Result<Boolean> isFavoritedByType(@RequestParam Long userId,
                                              @RequestParam String type,
                                              @RequestParam Long itemId) {
        boolean favorited = favoriteService.isFavoritedByType(userId, type, itemId);
        return Result.success(favorited);
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
