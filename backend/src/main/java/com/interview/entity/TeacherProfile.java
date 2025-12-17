package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 教师信息扩展表
 */
@Data
@Entity
@Table(name = "teacher_profile")
public class TeacherProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId; // 关联用户表

    @Column(length = 100)
    private String realName; // 真实姓名

    @Column(length = 50)
    private String title; // 职称：讲师、副教授、教授等

    @Column(length = 100)
    private String organization; // 所属机构/学校

    @Column(length = 100)
    private String department; // 院系/部门

    @Column(columnDefinition = "TEXT")
    private String expertise; // 专业领域（JSON数组）

    @Column(columnDefinition = "TEXT")
    private String introduction; // 个人简介

    @Column(name = "teaching_years")
    private Integer teachingYears; // 教学年限

    @Column(name = "is_verified")
    private Boolean isVerified = false; // 是否认证

    @Column(name = "verify_status")
    @Enumerated(EnumType.STRING)
    private VerifyStatus verifyStatus = VerifyStatus.PENDING; // 认证状态

    @Column(name = "verify_time")
    private LocalDateTime verifyTime; // 认证时间

    @Column(name = "student_count")
    private Integer studentCount = 0; // 学生数量

    @Column(name = "course_count")
    private Integer courseCount = 0; // 课程数量

    @Column(name = "avg_rating")
    private Double avgRating = 0.0; // 平均评分

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum VerifyStatus {
        PENDING,   // 待审核
        APPROVED,  // 已通过
        REJECTED   // 已拒绝
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
