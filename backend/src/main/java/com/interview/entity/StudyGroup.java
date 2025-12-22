package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "study_group")
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String avatar;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "member_count")
    private Integer memberCount = 1;

    @Column(name = "max_member_count")
    private Integer maxMemberCount = 100;

    @Column(length = 200)
    private String target;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status = GroupStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum GroupStatus {
        ACTIVE,
        INACTIVE
    }
}
