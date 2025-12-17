package com.interview.service;

import com.interview.entity.Assignment;
import com.interview.entity.AssignmentSubmission;
import com.interview.entity.GradingRecord;
import com.interview.repository.AssignmentRepository;
import com.interview.repository.AssignmentSubmissionRepository;
import com.interview.repository.GradingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业服务
 */
@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    @Autowired
    private GradingRecordRepository gradingRepository;

    /**
     * 创建作业
     */
    @Transactional
    public Assignment createAssignment(Assignment assignment) {
        assignment.setIsPublished(false);
        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    /**
     * 更新作业
     */
    @Transactional
    public Assignment updateAssignment(Long assignmentId, Assignment updatedAssignment) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("作业不存在"));

        if (updatedAssignment.getTitle() != null) {
            assignment.setTitle(updatedAssignment.getTitle());
        }
        if (updatedAssignment.getDescription() != null) {
            assignment.setDescription(updatedAssignment.getDescription());
        }
        if (updatedAssignment.getQuestionIds() != null) {
            assignment.setQuestionIds(updatedAssignment.getQuestionIds());
        }
        if (updatedAssignment.getTotalScore() != null) {
            assignment.setTotalScore(updatedAssignment.getTotalScore());
        }
        if (updatedAssignment.getPassScore() != null) {
            assignment.setPassScore(updatedAssignment.getPassScore());
        }
        if (updatedAssignment.getTimeLimit() != null) {
            assignment.setTimeLimit(updatedAssignment.getTimeLimit());
        }
        if (updatedAssignment.getStartTime() != null) {
            assignment.setStartTime(updatedAssignment.getStartTime());
        }
        if (updatedAssignment.getDeadline() != null) {
            assignment.setDeadline(updatedAssignment.getDeadline());
        }
        if (updatedAssignment.getAllowLateSubmission() != null) {
            assignment.setAllowLateSubmission(updatedAssignment.getAllowLateSubmission());
        }

        assignment.setUpdatedAt(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    /**
     * 发布作业
     */
    @Transactional
    public Assignment publishAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("作业不存在"));

        if (assignment.getQuestionIds() == null || assignment.getQuestionIds().isEmpty()) {
            throw new RuntimeException("作业至少需要包含一道题目");
        }

        assignment.setIsPublished(true);
        return assignmentRepository.save(assignment);
    }

    /**
     * 删除作业
     */
    @Transactional
    public void deleteAssignment(Long assignmentId) {
        assignmentRepository.deleteById(assignmentId);
    }

    /**
     * 提交作业
     */
    @Transactional
    public AssignmentSubmission submitAssignment(AssignmentSubmission submission) {
        Assignment assignment = assignmentRepository.findById(submission.getAssignmentId())
            .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 检查是否已提交
        if (submissionRepository.existsByAssignmentIdAndStudentId(
            submission.getAssignmentId(), submission.getStudentId())) {
            throw new RuntimeException("已经提交过该作业");
        }

        // 检查是否过期
        LocalDateTime now = LocalDateTime.now();
        boolean isLate = assignment.getDeadline() != null && now.isAfter(assignment.getDeadline());

        if (isLate && !assignment.getAllowLateSubmission()) {
            throw new RuntimeException("作业已过截止时间，不允许迟交");
        }

        submission.setIsLate(isLate);
        submission.setSubmitTime(now);
        submission.setStatus(AssignmentSubmission.SubmissionStatus.SUBMITTED);

        AssignmentSubmission savedSubmission = submissionRepository.save(submission);

        // 更新作业提交数
        assignment.setSubmissionCount(assignment.getSubmissionCount() + 1);
        assignmentRepository.save(assignment);

        return savedSubmission;
    }

    /**
     * 批改作业
     */
    @Transactional
    public AssignmentSubmission gradeSubmission(
        Long submissionId,
        Long graderId,
        Double manualScore,
        String feedback
    ) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        submission.setManualScore(manualScore);
        submission.setFeedback(feedback);
        submission.setGradedBy(graderId);
        submission.setGradedTime(LocalDateTime.now());
        submission.setStatus(AssignmentSubmission.SubmissionStatus.GRADED);

        // 计算总分（自动评分 + 人工评分）
        double totalScore = (submission.getAutoScore() != null ? submission.getAutoScore() : 0.0) +
                           (manualScore != null ? manualScore : 0.0);
        submission.setTotalScore(totalScore);

        return submissionRepository.save(submission);
    }

    /**
     * 返还作业
     */
    @Transactional
    public AssignmentSubmission returnSubmission(Long submissionId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("提交记录不存在"));

        submission.setStatus(AssignmentSubmission.SubmissionStatus.RETURNED);
        return submissionRepository.save(submission);
    }

    /**
     * 获取班级作业列表
     */
    public List<Assignment> getClassAssignments(Long classId) {
        return assignmentRepository.findByClassIdOrderByCreatedAtDesc(classId);
    }

    /**
     * 获取作业详情
     */
    public Assignment getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new RuntimeException("作业不存在"));
    }

    /**
     * 获取作业提交列表
     */
    public List<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    /**
     * 获取待批改的作业
     */
    public List<AssignmentSubmission> getPendingGradings(Long assignmentId) {
        return submissionRepository.findByAssignmentIdAndStatus(
            assignmentId,
            AssignmentSubmission.SubmissionStatus.SUBMITTED
        );
    }

    /**
     * 获取学生的作业提交
     */
    public AssignmentSubmission getStudentSubmission(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId)
            .orElse(null);
    }

    /**
     * 添加批改记录
     */
    @Transactional
    public GradingRecord addGradingRecord(GradingRecord record) {
        record.setGradedAt(LocalDateTime.now());
        return gradingRepository.save(record);
    }

    /**
     * 获取提交的批改记录
     */
    public List<GradingRecord> getSubmissionGradings(Long submissionId) {
        return gradingRepository.findBySubmissionId(submissionId);
    }
}
