package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.TeacherProfile;
import com.interview.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师控制器
 */
@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    /**
     * 创建教师资料
     */
    @PostMapping("/profile")
    public Result<TeacherProfile> createProfile(@RequestParam Long userId, @RequestBody TeacherProfile profile) {
        try {
            TeacherProfile created = teacherService.createProfile(userId, profile);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 更新教师资料
     */
    @PutMapping("/profile")
    public Result<TeacherProfile> updateProfile(@RequestParam Long userId, @RequestBody TeacherProfile profile) {
        try {
            TeacherProfile updated = teacherService.updateProfile(userId, profile);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取教师资料
     */
    @GetMapping("/profile")
    public Result<TeacherProfile> getProfile(@RequestParam Long userId) {
        try {
            TeacherProfile profile = teacherService.getProfile(userId);
            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(404, e.getMessage());
        }
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
        try {
            TeacherProfile profile = teacherService.verifyTeacher(profileId, approved, comments);
            return Result.success(profile);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取待认证教师列表（管理员）
     */
    @GetMapping("/pending-verifications")
    public Result<List<TeacherProfile>> getPendingVerifications() {
        try {
            List<TeacherProfile> profiles = teacherService.getPendingVerifications();
            return Result.success(profiles);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取认证教师列表
     */
    @GetMapping("/verified")
    public Result<List<TeacherProfile>> getVerifiedTeachers() {
        try {
            List<TeacherProfile> profiles = teacherService.getVerifiedTeachers();
            return Result.success(profiles);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取热门教师（按评分）
     */
    @GetMapping("/top-rated")
    public Result<List<TeacherProfile>> getTopRatedTeachers() {
        try {
            List<TeacherProfile> profiles = teacherService.getTopRatedTeachers();
            return Result.success(profiles);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取热门教师（按学生数）
     */
    @GetMapping("/popular")
    public Result<List<TeacherProfile>> getPopularTeachers() {
        try {
            List<TeacherProfile> profiles = teacherService.getPopularTeachers();
            return Result.success(profiles);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
