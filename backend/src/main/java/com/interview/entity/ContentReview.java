package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 内容审核表
 */
@Data
@Entity
@Table(name = "content_reviews")
public class ContentReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType; // 内容类型

    @Column(name = "content_id", nullable = false)
    private Long contentId; // 内容ID

    @Column(name = "creator_id", nullable = false)
    private Long creatorId; // 创建者ID

    @Column(nullable = false, length = 200)
    private String title; // 内容标题

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING; // 审核状态

    @Column(name = "reviewer_id")
    private Long reviewerId; // 审核人ID

    @Column(columnDefinition = "TEXT")
    private String reviewComments; // 审核意见

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt = LocalDateTime.now(); // 提交时间

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt; // 审核时间

    public enum ContentType {
        QUESTION,        // 题目
        KNOWLEDGE_POINT, // 知识点
        TUTORIAL,        // 教程
        COURSE           // 课程
    }

    public enum ReviewStatus {
        PENDING,   // 待审核
        APPROVED,  // 已通过
        REJECTED,  // 已拒绝
        REVISING   // 修改中
    }
}
