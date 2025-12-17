package com.interview.repository;

import com.interview.entity.TeacherQA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherQARepository extends JpaRepository<TeacherQA, Long> {

    List<TeacherQA> findByTeacherId(Long teacherId);

    List<TeacherQA> findByStudentId(Long studentId);

    List<TeacherQA> findByClassId(Long classId);

    List<TeacherQA> findByStatus(TeacherQA.QAStatus status);

    List<TeacherQA> findByTeacherIdAndStatus(Long teacherId, TeacherQA.QAStatus status);

    List<TeacherQA> findByIsPublic(Boolean isPublic);

    List<TeacherQA> findByClassIdOrderByAskedAtDesc(Long classId);

    Long countByTeacherIdAndStatus(Long teacherId, TeacherQA.QAStatus status);
}
