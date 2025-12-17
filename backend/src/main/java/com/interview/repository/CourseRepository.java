package com.interview.repository;

import com.interview.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByTeacherIdAndStatus(Long teacherId, Course.CourseStatus status);

    List<Course> findByStatus(Course.CourseStatus status);

    List<Course> findByCategory(String category);

    List<Course> findByDifficulty(Course.Difficulty difficulty);

    List<Course> findByIsPublic(Boolean isPublic);

    List<Course> findByTitleContaining(String keyword);

    List<Course> findByOrderByViewCountDesc();

    List<Course> findByOrderByAvgRatingDesc();

    List<Course> findByOrderByStudentCountDesc();

    List<Course> findTop10ByStatusOrderByCreatedAtDesc(Course.CourseStatus status);
}
