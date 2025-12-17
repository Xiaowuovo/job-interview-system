package com.interview.repository;

import com.interview.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {

    List<CourseContent> findByChapterId(Long chapterId);

    List<CourseContent> findByChapterIdOrderBySortOrder(Long chapterId);

    List<CourseContent> findByContentType(CourseContent.ContentType contentType);

    List<CourseContent> findByContentTypeAndContentId(CourseContent.ContentType contentType, Long contentId);

    Long countByChapterId(Long chapterId);

    void deleteByChapterId(Long chapterId);
}
