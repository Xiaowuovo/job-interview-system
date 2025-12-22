package com.interview.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "is_author_reply")
    private Boolean isAuthorReply = false;

    @Column(name = "is_best_answer")
    private Boolean isBestAnswer = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
