package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.GroupMember;
import com.interview.entity.StudyGroup;
import com.interview.service.StudyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/study-groups")
@RequiredArgsConstructor
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    @PostMapping
    public Result<StudyGroup> createGroup(@RequestBody StudyGroup group, @RequestHeader("userId") Long userId) {
        StudyGroup created = studyGroupService.createGroup(group, userId);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<StudyGroup> getGroup(@PathVariable Long id) {
        StudyGroup group = studyGroupService.getGroupById(id);
        return Result.success(group);
    }

    @PutMapping("/{id}")
    public Result<StudyGroup> updateGroup(@PathVariable Long id, @RequestBody StudyGroup group, @RequestHeader("userId") Long userId) {
        StudyGroup existingGroup = studyGroupService.getGroupById(id);
        if (!existingGroup.getCreatorId().equals(userId)) {
            return Result.error("无权修改他人创建的小组");
        }
        StudyGroup updated = studyGroupService.updateGroup(id, group);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGroup(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        StudyGroup group = studyGroupService.getGroupById(id);
        if (!group.getCreatorId().equals(userId)) {
            return Result.error("无权删除他人创建的小组");
        }
        studyGroupService.deleteGroup(id);
        return Result.success();
    }

    @GetMapping
    public Result<Page<StudyGroup>> getPublicGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<StudyGroup> groups = studyGroupService.getPublicGroups(page, size);
        return Result.success(groups);
    }

    @GetMapping("/user/{userId}")
    public Result<List<StudyGroup>> getUserGroups(@PathVariable Long userId) {
        List<StudyGroup> groups = studyGroupService.getUserGroups(userId);
        return Result.success(groups);
    }

    @GetMapping("/search")
    public Result<Page<StudyGroup>> searchGroups(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<StudyGroup> groups = studyGroupService.searchGroups(keyword, page, size);
        return Result.success(groups);
    }

    @GetMapping("/available")
    public Result<List<StudyGroup>> getAvailableGroups() {
        List<StudyGroup> groups = studyGroupService.getAvailableGroups();
        return Result.success(groups);
    }

    @PostMapping("/{groupId}/join")
    public Result<Void> joinGroup(@PathVariable Long groupId, @RequestHeader("userId") Long userId) {
        studyGroupService.joinGroup(groupId, userId);
        return Result.success();
    }

    @PostMapping("/{groupId}/leave")
    public Result<Void> leaveGroup(@PathVariable Long groupId, @RequestHeader("userId") Long userId) {
        studyGroupService.leaveGroup(groupId, userId);
        return Result.success();
    }

    @GetMapping("/{groupId}/members")
    public Result<List<GroupMember>> getGroupMembers(@PathVariable Long groupId) {
        List<GroupMember> members = studyGroupService.getGroupMembers(groupId);
        return Result.success(members);
    }

    @GetMapping("/user/{userId}/memberships")
    public Result<List<GroupMember>> getUserGroupMemberships(@PathVariable Long userId) {
        List<GroupMember> memberships = studyGroupService.getUserGroupMemberships(userId);
        return Result.success(memberships);
    }

    @GetMapping("/{groupId}/is-member")
    public Result<Boolean> isMember(@PathVariable Long groupId, @RequestHeader("userId") Long userId) {
        boolean isMember = studyGroupService.isMember(groupId, userId);
        return Result.success(isMember);
    }
}
