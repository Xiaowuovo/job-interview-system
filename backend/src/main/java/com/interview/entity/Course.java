package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程表
 */
@Data
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId; // 教师ID

    @Column(nullable = false, length = 100)
    private String title; // 课程标题

    @Column(columnDefinition = "TEXT")
    private String description; // 课程描述

    @Column(length = 255)
    private String cover; // 封面图URL

    @Column(length = 50)
    private String category; // 课程分类

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.MEDIUM; // 难度级别

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT; // 课程状态

    @Column(name = "is_public")
    private Boolean isPublic = true; // 是否公开

    @Column(name = "chapter_count")
    private Integer chapterCount = 0; // 章节数

    @Column(name = "student_count")
    private Integer studentCount = 0; // 学生数

    @Column(name = "view_count")
    private Integer viewCount = 0; // 浏览量

    @Column(name = "like_count")
    private Integer likeCount = 0; // 点赞数

    @Column(name = "avg_rating")
    private Double avgRating = 0.0; // 平均评分

    @Column(name = "rating_count")
    private Integer ratingCount = 0; // 评分人数

    private String tags; // 标签（逗号分隔）

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt; // 发布时间

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    public enum CourseStatus {
        DRAFT,      // 草稿
        PUBLISHED,  // 已发布
        ARCHIVED    // 已归档
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
