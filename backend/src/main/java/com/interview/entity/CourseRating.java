package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程评价表
 */
@Data
@Entity
@Table(name = "course_ratings")
public class CourseRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId; // 课程ID

    @Column(name = "student_id", nullable = false)
    private Long studentId; // 学生ID

    @Column(nullable = false)
    private Integer rating; // 评分（1-5）

    @Column(name = "content_rating")
    private Integer contentRating; // 内容评分

    @Column(name = "difficulty_rating")
    private Integer difficultyRating; // 难度评分

    @Column(name = "teaching_rating")
    private Integer teachingRating; // 教学评分

    @Column(columnDefinition = "TEXT")
    private String comment; // 评价内容

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false; // 是否匿名

    @Column(name = "like_count")
    private Integer likeCount = 0; // 点赞数

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
