package com.interview.repository;

import com.interview.entity.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    
    Page<StudyGroup> findByStatusAndIsPublicOrderByCreatedAtDesc(
        StudyGroup.GroupStatus status, Boolean isPublic, Pageable pageable);
    
    List<StudyGroup> findByCreatorIdOrderByCreatedAtDesc(Long creatorId);
    
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.name LIKE %:keyword% OR sg.description LIKE %:keyword%")
    Page<StudyGroup> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT sg FROM StudyGroup sg WHERE sg.memberCount < sg.maxMemberCount AND sg.status = 'ACTIVE' AND sg.isPublic = true")
    List<StudyGroup> findAvailableGroups();
}
