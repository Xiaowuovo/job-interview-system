package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "knowledge_points")
public class KnowledgePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(length = 100)
    private String category; // 分类名称（如：Java基础、算法等）

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content; // 支持Markdown

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private Integer importance; // 重要度(1-5)

    @Column(length = 500)
    private String tags; // 标签（逗号分隔）

    @Column(name = "prerequisite_points", columnDefinition = "TEXT")
    private String prerequisitePoints; // 前置知识点ID（JSON数组）

    @Column(name = "related_points", columnDefinition = "TEXT")
    private String relatedPoints; // 相关知识点ID（JSON数组）

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
