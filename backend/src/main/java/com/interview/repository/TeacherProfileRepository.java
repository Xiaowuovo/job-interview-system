package com.interview.repository;

import com.interview.entity.TeacherProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile, Long> {

    Optional<TeacherProfile> findByUserId(Long userId);

    List<TeacherProfile> findByIsVerified(Boolean isVerified);

    List<TeacherProfile> findByVerifyStatus(TeacherProfile.VerifyStatus status);

    List<TeacherProfile> findByOrganization(String organization);

    List<TeacherProfile> findByOrderByAvgRatingDesc();

    List<TeacherProfile> findByOrderByStudentCountDesc();
}
