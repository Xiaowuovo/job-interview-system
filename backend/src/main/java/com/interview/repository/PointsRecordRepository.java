package com.interview.repository;

import com.interview.entity.PointsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 积分记录Repository
 */
@Repository
public interface PointsRecordRepository extends JpaRepository<PointsRecord, Long> {

    /**
     * 查找用户的积分记录
     */
    List<PointsRecord> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 查找用户在时间范围内的积分记录
     */
    List<PointsRecord> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计用户总积分
     */
    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointsRecord p WHERE p.userId = :userId")
    Integer getTotalPointsByUserId(Long userId);

    /**
     * 统计用户某类型的积分记录数
     */
    long countByUserIdAndType(Long userId, String type);

    /**
     * 检查今日是否已签到
     */
    boolean existsByUserIdAndTypeAndCreatedAtBetween(Long userId, String type, LocalDateTime start, LocalDateTime end);
}
