package com.interview.repository;

import com.interview.entity.ProjectExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectExperienceRepository extends JpaRepository<ProjectExperience, Long> {
    
    List<ProjectExperience> findByResumeIdOrderByStartDateDesc(Long resumeId);
    
    void deleteByResumeId(Long resumeId);
}
