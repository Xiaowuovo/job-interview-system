package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interview_answer")
public class InterviewAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "user_answer", columnDefinition = "LONGTEXT", nullable = false)
    private String userAnswer;

    @Column(name = "answer_time")
    private Integer answerTime;

    @Column(name = "is_satisfactory")
    private Boolean isSatisfactory;

    @Column(name = "triggered_follow_up")
    private Boolean triggeredFollowUp = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
