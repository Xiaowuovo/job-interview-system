package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "study_records")
public class StudyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "knowledge_point_id", nullable = false)
    private Long knowledgePointId;

    private Integer progress = 0; // 进度（0-100）

    @Column(name = "study_time")
    private Integer studyTime = 0; // 学习时长（秒）

    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_STARTED;

    @Column(name = "mastery_level")
    private Integer masteryLevel; // 掌握度（0-100）

    @Column(columnDefinition = "TEXT")
    private String notes; // 学习笔记

    @Column(name = "last_study_at")
    private LocalDateTime lastStudyAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
