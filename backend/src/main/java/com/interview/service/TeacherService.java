package com.interview.service;

import com.interview.entity.TeacherProfile;
import com.interview.entity.User;
import com.interview.repository.TeacherProfileRepository;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 教师服务
 */
@Service
public class TeacherService {

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建教师资料
     */
    @Transactional
    public TeacherProfile createProfile(Long userId, TeacherProfile profile) {
        // 验证用户存在且角色为教师
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (user.getRole() != User.Role.TEACHER) {
            throw new RuntimeException("用户角色不是教师");
        }

        // 检查是否已存在资料
        if (teacherProfileRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("教师资料已存在");
        }

        profile.setUserId(userId);
        profile.setVerifyStatus(TeacherProfile.VerifyStatus.PENDING);
        profile.setCreatedAt(LocalDateTime.now());

        return teacherProfileRepository.save(profile);
    }

    /**
     * 更新教师资料
     */
    @Transactional
    public TeacherProfile updateProfile(Long userId, TeacherProfile updatedProfile) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("教师资料不存在"));

        // 更新基本信息
        if (updatedProfile.getRealName() != null) {
            profile.setRealName(updatedProfile.getRealName());
        }
        if (updatedProfile.getTitle() != null) {
            profile.setTitle(updatedProfile.getTitle());
        }
        if (updatedProfile.getOrganization() != null) {
            profile.setOrganization(updatedProfile.getOrganization());
        }
        if (updatedProfile.getDepartment() != null) {
            profile.setDepartment(updatedProfile.getDepartment());
        }
        if (updatedProfile.getExpertise() != null) {
            profile.setExpertise(updatedProfile.getExpertise());
        }
        if (updatedProfile.getIntroduction() != null) {
            profile.setIntroduction(updatedProfile.getIntroduction());
        }
        if (updatedProfile.getTeachingYears() != null) {
            profile.setTeachingYears(updatedProfile.getTeachingYears());
        }

        profile.setUpdatedAt(LocalDateTime.now());
        return teacherProfileRepository.save(profile);
    }

    /**
     * 获取教师资料
     */
    public TeacherProfile getProfile(Long userId) {
        return teacherProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("教师资料不存在"));
    }

    /**
     * 认证教师
     */
    @Transactional
    public TeacherProfile verifyTeacher(Long profileId, boolean approved, String comments) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("教师资料不存在"));

        if (approved) {
            profile.setVerifyStatus(TeacherProfile.VerifyStatus.APPROVED);
            profile.setIsVerified(true);
        } else {
            profile.setVerifyStatus(TeacherProfile.VerifyStatus.REJECTED);
            profile.setIsVerified(false);
        }

        profile.setVerifyTime(LocalDateTime.now());
        return teacherProfileRepository.save(profile);
    }

    /**
     * 获取待认证教师列表
     */
    public List<TeacherProfile> getPendingVerifications() {
        return teacherProfileRepository.findByVerifyStatus(TeacherProfile.VerifyStatus.PENDING);
    }

    /**
     * 获取认证教师列表
     */
    public List<TeacherProfile> getVerifiedTeachers() {
        return teacherProfileRepository.findByIsVerified(true);
    }

    /**
     * 获取热门教师（按评分排序）
     */
    public List<TeacherProfile> getTopRatedTeachers() {
        return teacherProfileRepository.findByOrderByAvgRatingDesc();
    }

    /**
     * 获取热门教师（按学生数排序）
     */
    public List<TeacherProfile> getPopularTeachers() {
        return teacherProfileRepository.findByOrderByStudentCountDesc();
    }

    /**
     * 更新教师统计数据
     */
    @Transactional
    public void updateStatistics(Long userId, int studentCountDelta, int courseCountDelta) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("教师资料不存在"));

        profile.setStudentCount(profile.getStudentCount() + studentCountDelta);
        profile.setCourseCount(profile.getCourseCount() + courseCountDelta);
        teacherProfileRepository.save(profile);
    }

    /**
     * 更新教师评分
     */
    @Transactional
    public void updateRating(Long userId, double newRating) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("教师资料不存在"));

        profile.setAvgRating(newRating);
        teacherProfileRepository.save(profile);
    }
}
