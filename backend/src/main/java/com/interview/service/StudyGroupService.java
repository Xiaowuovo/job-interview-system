package com.interview.service;

import com.interview.entity.GroupMember;
import com.interview.entity.StudyGroup;
import com.interview.repository.GroupMemberRepository;
import com.interview.repository.StudyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudyGroupService {

    @Autowired
    private StudyGroupRepository studyGroupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Transactional
    public StudyGroup createGroup(StudyGroup group, Long creatorId) {
        group.setCreatorId(creatorId);
        StudyGroup savedGroup = studyGroupRepository.save(group);
        
        GroupMember owner = new GroupMember();
        owner.setGroupId(savedGroup.getId());
        owner.setUserId(creatorId);
        owner.setRole(GroupMember.MemberRole.OWNER);
        groupMemberRepository.save(owner);
        
        return savedGroup;
    }

    public StudyGroup getGroupById(Long id) {
        return studyGroupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("学习小组不存在"));
    }

    @Transactional
    public StudyGroup updateGroup(Long id, StudyGroup group) {
        StudyGroup existingGroup = getGroupById(id);
        existingGroup.setName(group.getName());
        existingGroup.setDescription(group.getDescription());
        existingGroup.setAvatar(group.getAvatar());
        existingGroup.setTarget(group.getTarget());
        existingGroup.setIsPublic(group.getIsPublic());
        existingGroup.setMaxMemberCount(group.getMaxMemberCount());
        return studyGroupRepository.save(existingGroup);
    }

    public void deleteGroup(Long id) {
        studyGroupRepository.deleteById(id);
    }

    public Page<StudyGroup> getPublicGroups(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studyGroupRepository.findByStatusAndIsPublicOrderByCreatedAtDesc(
            StudyGroup.GroupStatus.ACTIVE, true, pageable);
    }

    public List<StudyGroup> getUserGroups(Long userId) {
        return studyGroupRepository.findByCreatorIdOrderByCreatedAtDesc(userId);
    }

    public Page<StudyGroup> searchGroups(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studyGroupRepository.searchByKeyword(keyword, pageable);
    }

    public List<StudyGroup> getAvailableGroups() {
        return studyGroupRepository.findAvailableGroups();
    }

    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        StudyGroup group = getGroupById(groupId);
        
        if (groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("已经是该小组成员");
        }
        
        if (group.getMemberCount() >= group.getMaxMemberCount()) {
            throw new RuntimeException("小组人数已满");
        }
        
        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        member.setRole(GroupMember.MemberRole.MEMBER);
        groupMemberRepository.save(member);
        
        group.setMemberCount(group.getMemberCount() + 1);
        studyGroupRepository.save(group);
    }

    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        StudyGroup group = getGroupById(groupId);
        
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("不是该小组成员");
        }
        
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
            .orElseThrow(() -> new RuntimeException("成员信息不存在"));
        
        if (member.getRole() == GroupMember.MemberRole.OWNER) {
            throw new RuntimeException("组长不能退出小组");
        }
        
        groupMemberRepository.deleteByGroupIdAndUserId(groupId, userId);
        
        group.setMemberCount(group.getMemberCount() - 1);
        studyGroupRepository.save(group);
    }

    public List<GroupMember> getGroupMembers(Long groupId) {
        return groupMemberRepository.findByGroupIdOrderByJoinAtAsc(groupId);
    }

    public List<GroupMember> getUserGroupMemberships(Long userId) {
        return groupMemberRepository.findByUserIdOrderByJoinAtDesc(userId);
    }

    public boolean isMember(Long groupId, Long userId) {
        return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
    }
}
