package com.interview.repository;

import com.interview.entity.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRating, Long> {

    List<CourseRating> findByCourseId(Long courseId);

    List<CourseRating> findByStudentId(Long studentId);

    Optional<CourseRating> findByCourseIdAndStudentId(Long courseId, Long studentId);

    Long countByCourseId(Long courseId);

    @Query("SELECT AVG(r.rating) FROM CourseRating r WHERE r.courseId = ?1")
    Double getAverageRatingByCourseId(Long courseId);

    Boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);
}
