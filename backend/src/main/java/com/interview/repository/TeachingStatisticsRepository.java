package com.interview.repository;

import com.interview.entity.TeachingStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeachingStatisticsRepository extends JpaRepository<TeachingStatistics, Long> {

    List<TeachingStatistics> findByTeacherId(Long teacherId);

    List<TeachingStatistics> findByTeacherIdAndStatType(Long teacherId, TeachingStatistics.StatType statType);

    Optional<TeachingStatistics> findByTeacherIdAndStatDateAndStatType(
        Long teacherId,
        LocalDate statDate,
        TeachingStatistics.StatType statType
    );

    List<TeachingStatistics> findByTeacherIdAndStatDateBetween(
        Long teacherId,
        LocalDate startDate,
        LocalDate endDate
    );
}
