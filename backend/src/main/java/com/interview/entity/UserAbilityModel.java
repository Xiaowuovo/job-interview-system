package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_ability_model")
public class UserAbilityModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    // 基础技术能力
    @Column(name = "algorithm_ability")
    private Double algorithmAbility = 0.0; // 算法能力

    @Column(name = "coding_ability")
    private Double codingAbility = 0.0; // 编码能力

    @Column(name = "system_design_ability")
    private Double systemDesignAbility = 0.0; // 系统设计

    @Column(name = "database_ability")
    private Double databaseAbility = 0.0; // 数据库

    @Column(name = "network_ability")
    private Double networkAbility = 0.0; // 计算机网络

    @Column(name = "os_ability")
    private Double osAbility = 0.0; // 操作系统

    // 语言技能（JSON格式：{"Java": 85, "Python": 70}）
    @Column(name = "language_skills", columnDefinition = "TEXT")
    private String languageSkills;

    @Column(name = "framework_skills", columnDefinition = "TEXT")
    private String frameworkSkills;

    // 软技能
    @Column(name = "communication_ability")
    private Double communicationAbility = 0.0;

    @Column(name = "teamwork_ability")
    private Double teamworkAbility = 0.0;

    @Column(name = "problem_solving_ability")
    private Double problemSolvingAbility = 0.0;

    @Column(name = "learning_ability")
    private Double learningAbility = 0.0;

    // 综合评分
    @Column(name = "overall_score")
    private Double overallScore = 0.0;

    @Enumerated(EnumType.STRING)
    private Level level = Level.BEGINNER;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum Level {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED,
        EXPERT
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
