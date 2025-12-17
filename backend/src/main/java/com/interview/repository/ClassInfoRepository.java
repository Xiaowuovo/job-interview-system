package com.interview.repository;

import com.interview.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Long> {

    List<ClassInfo> findByTeacherId(Long teacherId);

    List<ClassInfo> findByTeacherIdAndStatus(Long teacherId, ClassInfo.ClassStatus status);

    Optional<ClassInfo> findByClassCode(String classCode);

    List<ClassInfo> findByCourseId(Long courseId);

    List<ClassInfo> findByStatus(ClassInfo.ClassStatus status);

    Long countByTeacherId(Long teacherId);
}
