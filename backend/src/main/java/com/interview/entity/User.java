package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(length = 255)
    private String avatar; // 头像URL

    @Column(length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT; // STUDENT, TEACHER, ADMIN

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type")
    private MembershipType membershipType = MembershipType.FREE;

    @Column(name = "membership_expire_at")
    private LocalDateTime membershipExpireAt;

    @Column(name = "target_position", length = 100)
    private String targetPosition; // 目标岗位

    @Column(name = "points")
    private Integer points = 0; // 积分

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 枚举类型定义
    public enum Role {
        STUDENT,
        TEACHER,
        ADMIN
    }

    public enum MembershipType {
        FREE,
        VIP,
        ENTERPRISE
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
