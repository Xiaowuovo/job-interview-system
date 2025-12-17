package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程章节表
 */
@Data
@Entity
@Table(name = "course_chapters")
public class CourseChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId; // 课程ID

    @Column(name = "parent_id")
    private Long parentId; // 父章节ID（用于多级章节）

    @Column(nullable = false, length = 100)
    private String title; // 章节标题

    @Column(columnDefinition = "TEXT")
    private String description; // 章节描述

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    @Column(name = "is_free")
    private Boolean isFree = false; // 是否免费

    @Column(name = "content_count")
    private Integer contentCount = 0; // 内容数量

    @Column(name = "duration")
    private Integer duration = 0; // 时长（分钟）

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
