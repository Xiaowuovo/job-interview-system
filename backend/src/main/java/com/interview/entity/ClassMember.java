package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 班级成员表
 */
@Data
@Entity
@Table(name = "class_members")
public class ClassMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_id", nullable = false)
    private Long classId; // 班级ID

    @Column(name = "student_id", nullable = false)
    private Long studentId; // 学生ID（关联user表）

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.STUDENT; // 角色

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.ACTIVE; // 状态

    @Column(name = "join_time")
    private LocalDateTime joinTime = LocalDateTime.now(); // 加入时间

    @Column(name = "leave_time")
    private LocalDateTime leaveTime; // 离开时间

    @Column(columnDefinition = "TEXT")
    private String notes; // 备注

    public enum MemberRole {
        STUDENT,         // 学生
        TEACHING_ASSISTANT  // 助教
    }

    public enum MemberStatus {
        ACTIVE,   // 在读
        DROPPED,  // 退出
        KICKED    // 被移除
    }
}
