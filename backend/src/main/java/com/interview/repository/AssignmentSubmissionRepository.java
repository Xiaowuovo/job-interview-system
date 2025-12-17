package com.interview.repository;

import com.interview.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    List<AssignmentSubmission> findByStudentId(Long studentId);

    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    List<AssignmentSubmission> findByAssignmentIdAndStatus(Long assignmentId, AssignmentSubmission.SubmissionStatus status);

    List<AssignmentSubmission> findByStatus(AssignmentSubmission.SubmissionStatus status);

    Long countByAssignmentId(Long assignmentId);

    Long countByAssignmentIdAndStatus(Long assignmentId, AssignmentSubmission.SubmissionStatus status);

    Boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}
