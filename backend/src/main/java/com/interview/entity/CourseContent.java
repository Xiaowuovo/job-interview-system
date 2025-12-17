package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 课程内容关联表
 */
@Data
@Entity
@Table(name = "course_contents")
public class CourseContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId; // 章节ID

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType; // 内容类型

    @Column(name = "content_id", nullable = false)
    private Long contentId; // 内容ID（knowledge_point_id或question_id或tutorial_id）

    @Column(name = "sort_order")
    private Integer sortOrder = 0; // 排序

    @Column(name = "is_required")
    private Boolean isRequired = true; // 是否必修

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ContentType {
        KNOWLEDGE_POINT,  // 知识点
        QUESTION,         // 题目
        TUTORIAL,         // 教程
        VIDEO,            // 视频
        DOCUMENT          // 文档
    }
}
