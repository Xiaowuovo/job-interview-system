package com.interview.repository;

import com.interview.entity.AnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {

    // 查询用户的答题记录
    List<AnswerRecord> findByUserId(Long userId);

    // 查询用户对特定题目的答题记录
    List<AnswerRecord> findByUserIdAndQuestionId(Long userId, Long questionId);

    // 查询用户的正确答题记录
    List<AnswerRecord> findByUserIdAndIsCorrect(Long userId, Boolean isCorrect);

    // 统计用户答题总数
    Long countByUserId(Long userId);

    // 统计用户正确答题数
    Long countByUserIdAndIsCorrect(Long userId, Boolean isCorrect);

    // 计算用户平均得分
    @Query("SELECT AVG(a.score) FROM AnswerRecord a WHERE a.userId = :userId")
    Double calculateAvgScoreByUserId(@Param("userId") Long userId);

    // 查询用户最近的答题记录
    List<AnswerRecord> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 查询用户的答题记录（按时间倒序）
    List<AnswerRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 新增方法
    List<AnswerRecord> findByUserIdAndCreatedAtBetween(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
