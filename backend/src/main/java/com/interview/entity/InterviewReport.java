package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "interview_report")
public class InterviewReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private Long sessionId;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "ranking_percentile")
    private Double rankingPercentile;

    @Column(name = "technical_score")
    private Double technicalScore;

    @Column(name = "communication_score")
    private Double communicationScore;

    @Column(name = "problem_solving_score")
    private Double problemSolvingScore;

    @Column(name = "learning_ability_score")
    private Double learningAbilityScore;

    @Column(name = "strengths_summary", columnDefinition = "TEXT")
    private String strengthsSummary;

    @Column(name = "weaknesses_summary", columnDefinition = "TEXT")
    private String weaknessesSummary;

    @Column(name = "improvement_plan", columnDefinition = "TEXT")
    private String improvementPlan;

    @Column(name = "recommended_resources", columnDefinition = "TEXT")
    private String recommendedResources;

    @Column(name = "similar_users_avg_score")
    private Double similarUsersAvgScore;

    @Column(name = "target_company_avg_score")
    private Double targetCompanyAvgScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
