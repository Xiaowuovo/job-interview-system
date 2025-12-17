package com.interview.repository;

import com.interview.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseChapterRepository extends JpaRepository<CourseChapter, Long> {

    List<CourseChapter> findByCourseId(Long courseId);

    List<CourseChapter> findByCourseIdOrderBySortOrder(Long courseId);

    List<CourseChapter> findByParentId(Long parentId);

    List<CourseChapter> findByCourseIdAndParentIdIsNull(Long courseId);

    Long countByCourseId(Long courseId);
}
