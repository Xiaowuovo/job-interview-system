package com.interview.repository;

import com.interview.entity.StudyRecord;
import com.interview.entity.StudyRecord.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {

    // 查询用户的学习记录
    List<StudyRecord> findByUserId(Long userId);

    // 查询用户对特定知识点的学习记录
    Optional<StudyRecord> findByUserIdAndKnowledgePointId(Long userId, Long knowledgePointId);

    // 查询用户特定状态的学习记录
    List<StudyRecord> findByUserIdAndStatus(Long userId, Status status);

    // 计算用户学习进度
    @Query("SELECT AVG(s.progress) FROM StudyRecord s WHERE s.userId = :userId")
    Double calculateAvgProgressByUserId(@Param("userId") Long userId);

    // 统计已完成的知识点数量
    Long countByUserIdAndStatus(Long userId, Status status);

    // 获取最近学习的知识点
    List<StudyRecord> findTop10ByUserIdOrderByLastStudyAtDesc(Long userId);

    // 新增方法
    List<StudyRecord> findByUserIdAndCreatedAtBetween(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
