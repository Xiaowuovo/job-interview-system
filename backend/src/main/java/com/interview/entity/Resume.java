package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 50)
    private String education;

    @Column(length = 100)
    private String school;

    @Column(length = 100)
    private String major;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "work_years")
    private Integer workYears;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "self_introduction", columnDefinition = "TEXT")
    private String selfIntroduction;

    @Column(name = "resume_url", length = 255)
    private String resumeUrl;

    @Column
    private Integer score;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
