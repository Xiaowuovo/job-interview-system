package com.interview.repository;

import com.interview.entity.QuestionFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 题目收藏Repository
 */
@Repository
public interface QuestionFavoriteRepository extends JpaRepository<QuestionFavorite, Long> {

    /**
     * 查找用户的所有收藏
     */
    List<QuestionFavorite> findByUserId(Long userId);

    /**
     * 查找用户对某题的收藏记录
     */
    Optional<QuestionFavorite> findByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 检查用户是否收藏了某题
     */
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 删除用户对某题的收藏
     */
    void deleteByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 统计用户收藏数量
     */
    long countByUserId(Long userId);
}
