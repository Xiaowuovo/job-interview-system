package com.interview.repository;

import com.interview.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByClassId(Long classId);

    List<Assignment> findByTeacherId(Long teacherId);

    List<Assignment> findByClassIdAndIsPublished(Long classId, Boolean isPublished);

    List<Assignment> findByType(Assignment.AssignmentType type);

    List<Assignment> findByDeadlineAfter(LocalDateTime deadline);

    List<Assignment> findByDeadlineBefore(LocalDateTime deadline);

    List<Assignment> findByClassIdOrderByCreatedAtDesc(Long classId);

    Long countByTeacherId(Long teacherId);

    Long countByClassId(Long classId);
}
