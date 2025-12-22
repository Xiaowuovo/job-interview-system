package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interview_evaluation")
public class InterviewEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "answer_id", nullable = false)
    private Long answerId;

    @Column(name = "completeness_score")
    private Double completenessScore;

    @Column(name = "accuracy_score")
    private Double accuracyScore;

    @Column(name = "logic_score")
    private Double logicScore;

    @Column(name = "expression_score")
    private Double expressionScore;

    @Column(name = "total_score")
    private Double totalScore;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(name = "key_points_covered", columnDefinition = "TEXT")
    private String keyPointsCovered;

    @Column(name = "key_points_missed", columnDefinition = "TEXT")
    private String keyPointsMissed;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
