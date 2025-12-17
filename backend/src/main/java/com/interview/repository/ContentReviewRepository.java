package com.interview.repository;

import com.interview.entity.ContentReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentReviewRepository extends JpaRepository<ContentReview, Long> {

    List<ContentReview> findByStatus(ContentReview.ReviewStatus status);

    List<ContentReview> findByCreatorId(Long creatorId);

    List<ContentReview> findByReviewerId(Long reviewerId);

    List<ContentReview> findByContentTypeAndStatus(ContentReview.ContentType contentType, ContentReview.ReviewStatus status);

    Optional<ContentReview> findByContentTypeAndContentId(ContentReview.ContentType contentType, Long contentId);

    Long countByStatus(ContentReview.ReviewStatus status);

    Long countByCreatorId(Long creatorId);
}
