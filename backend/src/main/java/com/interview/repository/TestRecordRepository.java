package com.interview.repository;

import com.interview.entity.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestRecordRepository extends JpaRepository<TestRecord, Long> {
    List<TestRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<TestRecord> findByUserId(Long userId);
    List<TestRecord> findByUserIdAndCategory(Long userId, String category);
    List<TestRecord> findByUserIdAndCreatedAtBetween(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end);
}
