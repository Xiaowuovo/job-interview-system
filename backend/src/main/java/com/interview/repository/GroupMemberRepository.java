package com.interview.repository;

import com.interview.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    List<GroupMember> findByGroupIdOrderByJoinAtAsc(Long groupId);
    
    List<GroupMember> findByUserIdOrderByJoinAtDesc(Long userId);
    
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    
    Boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    
    Long countByGroupId(Long groupId);
    
    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}
