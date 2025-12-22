package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interview_question")
public class InterviewQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "question_order")
    private Integer questionOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "question_content", columnDefinition = "TEXT", nullable = false)
    private String questionContent;

    @Column(name = "expected_key_points", columnDefinition = "TEXT")
    private String expectedKeyPoints;

    @Column(name = "standard_answer", columnDefinition = "LONGTEXT")
    private String standardAnswer;

    @Enumerated(EnumType.STRING)
    @Column
    private Difficulty difficulty;

    @Column(length = 50)
    private String category;

    @Column(name = "asked_at")
    private LocalDateTime askedAt = LocalDateTime.now();

    public enum QuestionType {
        TECHNICAL,
        PROJECT,
        BEHAVIORAL,
        HR,
        FOLLOW_UP
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
