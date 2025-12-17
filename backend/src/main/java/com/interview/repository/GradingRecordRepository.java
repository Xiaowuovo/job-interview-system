package com.interview.repository;

import com.interview.entity.GradingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradingRecordRepository extends JpaRepository<GradingRecord, Long> {

    List<GradingRecord> findBySubmissionId(Long submissionId);

    List<GradingRecord> findByGraderId(Long graderId);

    List<GradingRecord> findByQuestionId(Long questionId);

    Long countByGraderId(Long graderId);
}
