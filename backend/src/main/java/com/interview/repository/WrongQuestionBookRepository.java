package com.interview.repository;

import com.interview.entity.WrongQuestionBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WrongQuestionBookRepository extends JpaRepository<WrongQuestionBook, Long> {

    // 查询用户的错题本
    List<WrongQuestionBook> findByUserId(Long userId);

    // 查询用户未掌握的错题
    List<WrongQuestionBook> findByUserIdAndIsMastered(Long userId, Boolean isMastered);

    // 查询特定用户和题目的错题记录
    Optional<WrongQuestionBook> findByUserIdAndQuestionId(Long userId, Long questionId);

    // 查询需要复习的题目
    List<WrongQuestionBook> findByUserIdAndNextReviewAtBefore(Long userId, LocalDateTime now);

    // 统计用户错题数量
    Long countByUserId(Long userId);

    // 新增方法
    List<WrongQuestionBook> findByUserIdAndNextReviewAtBeforeAndIsMastered(Long userId, LocalDateTime now, Boolean isMastered);
    List<WrongQuestionBook> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
