package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 班级表
 */
@Data
@Entity
@Table(name = "classes")
public class ClassInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId; // 教师ID

    @Column(nullable = false, length = 100)
    private String name; // 班级名称

    @Column(columnDefinition = "TEXT")
    private String description; // 班级描述

    @Column(name = "class_code", unique = true, length = 20)
    private String classCode; // 班级邀请码

    @Column(name = "course_id")
    private Long courseId; // 关联课程ID（可选）

    @Column(name = "max_students")
    private Integer maxStudents = 100; // 最大学生数

    @Column(name = "student_count")
    private Integer studentCount = 0; // 当前学生数

    @Enumerated(EnumType.STRING)
    private ClassStatus status = ClassStatus.ACTIVE; // 班级状态

    @Column(name = "start_date")
    private LocalDateTime startDate; // 开班日期

    @Column(name = "end_date")
    private LocalDateTime endDate; // 结束日期

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ClassStatus {
        ACTIVE,    // 进行中
        ENDED,     // 已结束
        ARCHIVED   // 已归档
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
