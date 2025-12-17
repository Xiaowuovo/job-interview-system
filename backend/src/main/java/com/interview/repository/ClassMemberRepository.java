package com.interview.repository;

import com.interview.entity.ClassMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassMemberRepository extends JpaRepository<ClassMember, Long> {

    List<ClassMember> findByClassId(Long classId);

    List<ClassMember> findByClassIdAndStatus(Long classId, ClassMember.MemberStatus status);

    List<ClassMember> findByStudentId(Long studentId);

    Optional<ClassMember> findByClassIdAndStudentId(Long classId, Long studentId);

    Long countByClassIdAndStatus(Long classId, ClassMember.MemberStatus status);

    Boolean existsByClassIdAndStudentId(Long classId, Long studentId);
}
