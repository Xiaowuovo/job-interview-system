package com.interview.service;

import com.interview.entity.Question;
import com.interview.entity.QuestionFavorite;
import com.interview.repository.QuestionFavoriteRepository;
import com.interview.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 题目收藏服务
 */
@Service
@Transactional
public class QuestionFavoriteService {

    @Autowired
    private QuestionFavoriteRepository favoriteRepository;

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * 添加收藏
     */
    public QuestionFavorite addFavorite(Long userId, Long questionId, String notes) {
        // 检查是否已收藏
        Optional<QuestionFavorite> existing = favoriteRepository
                .findByUserIdAndQuestionId(userId, questionId);

        if (existing.isPresent()) {
            // 已收藏，更新备注
            QuestionFavorite favorite = existing.get();
            favorite.setNotes(notes);
            return favoriteRepository.save(favorite);
        }

        // 新增收藏
        QuestionFavorite favorite = new QuestionFavorite();
        favorite.setUserId(userId);
        favorite.setQuestionId(questionId);
        favorite.setNotes(notes);

        // 更新题目收藏数
        questionRepository.findById(questionId).ifPresent(question -> {
            question.setCollectCount(question.getCollectCount() + 1);
            questionRepository.save(question);
        });

        return favoriteRepository.save(favorite);
    }

    /**
     * 取消收藏
     */
    public boolean removeFavorite(Long userId, Long questionId) {
        Optional<QuestionFavorite> favorite = favoriteRepository
                .findByUserIdAndQuestionId(userId, questionId);

        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());

            // 更新题目收藏数
            questionRepository.findById(questionId).ifPresent(question -> {
                question.setCollectCount(Math.max(0, question.getCollectCount() - 1));
                questionRepository.save(question);
            });

            return true;
        }
        return false;
    }

    /**
     * 获取用户的收藏列表（包含题目详情）
     */
    public List<Question> getUserFavoriteQuestions(Long userId) {
        List<QuestionFavorite> favorites = favoriteRepository.findByUserId(userId);
        List<Question> questions = new ArrayList<>();

        for (QuestionFavorite favorite : favorites) {
            questionRepository.findById(favorite.getQuestionId())
                    .ifPresent(questions::add);
        }

        return questions;
    }

    /**
     * 获取用户的收藏记录
     */
    public List<QuestionFavorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorited(Long userId, Long questionId) {
        return favoriteRepository.existsByUserIdAndQuestionId(userId, questionId);
    }

    /**
     * 获取收藏数量
     */
    public long getFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    /**
     * 批量取消收藏
     */
    public int removeFavorites(Long userId, List<Long> questionIds) {
        int count = 0;
        for (Long questionId : questionIds) {
            if (removeFavorite(userId, questionId)) {
                count++;
            }
        }
        return count;
    }
}
