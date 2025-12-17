package com.interview.repository;

import com.interview.entity.DailyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 每日一题Repository
 */
@Repository
public interface DailyQuestionRepository extends JpaRepository<DailyQuestion, Long> {

    /**
     * 查找今日一题
     */
    Optional<DailyQuestion> findByDate(LocalDate date);

    /**
     * 查找最近N天的每日一题
     */
    List<DailyQuestion> findTop7ByOrderByDateDesc();

    /**
     * 检查某日是否已设置每日一题
     */
    boolean existsByDate(LocalDate date);
}
