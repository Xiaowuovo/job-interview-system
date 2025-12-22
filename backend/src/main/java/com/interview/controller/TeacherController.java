package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.TeacherProfile;
import com.interview.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * 创建教师资料
     */
    @PostMapping("/profile")
    public Result<TeacherProfile> createProfile(@RequestParam Long userId, @RequestBody TeacherProfile profile) {
        TeacherProfile created = teacherService.createProfile(userId, profile);
        return Result.success(created);
    }

    /**
     * 更新教师资料
     */
    @PutMapping("/profile")
    public Result<TeacherProfile> updateProfile(@RequestParam Long userId, @RequestBody TeacherProfile profile) {
        TeacherProfile updated = teacherService.updateProfile(userId, profile);
        return Result.success(updated);
    }

    /**
     * 获取教师资料
     */
    @GetMapping("/profile")
    public Result<TeacherProfile> getProfile(@RequestParam Long userId) {
        TeacherProfile profile = teacherService.getProfile(userId);
        return Result.success(profile);
    }

    /**
     * 认证教师（管理员）
     */
    @PostMapping("/verify")
    public Result<TeacherProfile> verifyTeacher(
        @RequestParam Long profileId,
        @RequestParam Boolean approved,
        @RequestParam(required = false) String comments
    ) {
        TeacherProfile profile = teacherService.verifyTeacher(profileId, approved, comments);
        return Result.success(profile);
    }

    /**
     * 获取待认证教师列表（管理员）
     */
    @GetMapping("/pending-verifications")
    public Result<List<TeacherProfile>> getPendingVerifications() {
        List<TeacherProfile> profiles = teacherService.getPendingVerifications();
        return Result.success(profiles);
    }

    /**
     * 获取认证教师列表
     */
    @GetMapping("/verified")
    public Result<List<TeacherProfile>> getVerifiedTeachers() {
        List<TeacherProfile> profiles = teacherService.getVerifiedTeachers();
        return Result.success(profiles);
    }

    /**
     * 获取热门教师（按评分）
     */
    @GetMapping("/top-rated")
    public Result<List<TeacherProfile>> getTopRatedTeachers() {
        List<TeacherProfile> profiles = teacherService.getTopRatedTeachers();
        return Result.success(profiles);
    }

    /**
     * 获取热门教师（按学生数）
     */
    @GetMapping("/popular")
    public Result<List<TeacherProfile>> getPopularTeachers() {
        List<TeacherProfile> profiles = teacherService.getPopularTeachers();
        return Result.success(profiles);
    }
}
