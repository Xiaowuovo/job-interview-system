package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.ClassInfo;
import com.interview.entity.ClassMember;
import com.interview.entity.StudyRecord;
import com.interview.entity.TestRecord;
import com.interview.entity.WrongQuestionBook;
import com.interview.repository.StudyRecordRepository;
import com.interview.repository.TestRecordRepository;
import com.interview.repository.UserRepository;
import com.interview.repository.WrongQuestionBookRepository;
import com.interview.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级管理控制器
 */
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class ClassRoomController {

    private final ClassService classService;
    private final UserRepository userRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final TestRecordRepository testRecordRepository;
    private final WrongQuestionBookRepository wrongQuestionBookRepository;

    /**
     * 教师创建班级
     */
    @PostMapping
    public Result<ClassInfo> createClass(@RequestBody ClassInfo classInfo) {
        ClassInfo created = classService.createClass(classInfo);
        return Result.success(created);
    }

    /**
     * 教师更新班级
     */
    @PutMapping("/{id}")
    public Result<ClassInfo> updateClass(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        ClassInfo updated = classService.updateClass(id, classInfo);
        return Result.success(updated);
    }

    /**
     * 教师删除班级
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteClass(@PathVariable Long id) {
        classService.archiveClass(id);
        return Result.success(null);
    }

    /**
     * 获取教师的班级列表
     */
    @GetMapping("/teacher/{teacherId}")
    public Result<List<ClassInfo>> getTeacherClasses(@PathVariable Long teacherId) {
        List<ClassInfo> classes = classService.getTeacherClasses(teacherId);
        return Result.success(classes);
    }

    /**
     * 获取班级详情
     */
    @GetMapping("/{id}")
    public Result<ClassInfo> getClass(@PathVariable Long id) {
        ClassInfo classInfo = classService.getClass(id);
        return Result.success(classInfo);
    }

    /**
     * 获取班级学生列表（带基本信息）
     */
    @GetMapping("/{id}/students")
    public Result<List<Map<String, Object>>> getClassStudents(@PathVariable Long id) {
        List<ClassMember> members = classService.getClassMembers(id);
        List<Map<String, Object>> students = new ArrayList<>();
        for (ClassMember member : members) {
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("memberId", member.getId());
            studentInfo.put("studentId", member.getStudentId());
            studentInfo.put("joinTime", member.getJoinTime());
            studentInfo.put("status", member.getStatus());
            userRepository.findById(member.getStudentId()).ifPresent(user -> {
                studentInfo.put("username", user.getUsername());
                studentInfo.put("nickname", user.getNickname());
                studentInfo.put("email", user.getEmail());
                studentInfo.put("phone", user.getPhone());
                studentInfo.put("targetPosition", user.getTargetPosition());
                studentInfo.put("points", user.getPoints());
            });
            students.add(studentInfo);
        }
        return Result.success(students);
    }

    /**
     * 教师移除学生
     */
    @DeleteMapping("/{classId}/students/{studentId}")
    public Result<Void> removeStudent(@PathVariable Long classId, @PathVariable Long studentId) {
        classService.removeMember(classId, studentId);
        return Result.success(null);
    }

    /**
     * 学生通过邀请码加入班级
     */
    @PostMapping("/join")
    public Result<ClassInfo> joinClass(@RequestBody Map<String, Object> params) {
        String classCode = params.get("classCode").toString();
        Long studentId = Long.valueOf(params.get("studentId").toString());
        classService.joinClass(classCode, studentId);
        return Result.success(classService.getClassByCode(classCode));
    }

    /**
     * 学生退出班级
     */
    @PostMapping("/{classId}/leave")
    public Result<Void> leaveClass(@PathVariable Long classId, @RequestBody Map<String, Object> params) {
        Long studentId = Long.valueOf(params.get("studentId").toString());
        classService.leaveClass(classId, studentId);
        return Result.success(null);
    }

    /**
     * 获取某学生的学习数据（教师查看）
     */
    @GetMapping("/student/{studentId}/stats")
    public Result<Map<String, Object>> getStudentStats(@PathVariable Long studentId) {
        Map<String, Object> stats = new HashMap<>();
        userRepository.findById(studentId).ifPresent(user -> {
            stats.put("username", user.getUsername());
            stats.put("nickname", user.getNickname());
            stats.put("email", user.getEmail());
            stats.put("targetPosition", user.getTargetPosition());
            stats.put("points", user.getPoints());
        });
        List<StudyRecord> studyRecords = studyRecordRepository.findByUserId(studentId);
        long masteredCount = studyRecords.stream()
            .filter(r -> "master".equals(r.getLevel()) || StudyRecord.Status.COMPLETED.equals(r.getStatus()))
            .count();
        int totalStudyTime = studyRecords.stream()
            .mapToInt(r -> r.getStudyTime() != null ? r.getStudyTime() : 0).sum();
        stats.put("studiedKnowledgePoints", studyRecords.size());
        stats.put("masteredKnowledgePoints", masteredCount);
        stats.put("totalStudyTimeMinutes", totalStudyTime / 60);

        List<TestRecord> testRecords = testRecordRepository.findByUserId(studentId);
        double avgScore = testRecords.stream()
            .mapToDouble(r -> r.getScore() != null ? r.getScore() : 0.0).average().orElse(0.0);
        stats.put("testCount", testRecords.size());
        stats.put("averageScore", Math.round(avgScore * 10.0) / 10.0);

        List<WrongQuestionBook> wrongQuestions = wrongQuestionBookRepository.findByUserId(studentId);
        long unsolvedWrong = wrongQuestions.stream()
            .filter(w -> !Boolean.TRUE.equals(w.getIsMastered())).count();
        stats.put("wrongQuestionCount", wrongQuestions.size());
        stats.put("unsolvedWrongCount", unsolvedWrong);

        return Result.success(stats);
    }

    /**
     * 获取学生加入的班级列表
     */
    @GetMapping("/student/{studentId}")
    public Result<List<Map<String, Object>>> getStudentClasses(@PathVariable Long studentId) {
        List<ClassMember> memberships = classService.getStudentClasses(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (ClassMember member : memberships) {
            if (member.getStatus() != ClassMember.MemberStatus.ACTIVE) continue;
            Map<String, Object> info = new HashMap<>();
            info.put("memberId", member.getId());
            info.put("joinTime", member.getJoinTime());
            ClassInfo classInfo = classService.getClass(member.getClassId());
            info.put("classId", classInfo.getId());
            info.put("className", classInfo.getName());
            info.put("classCode", classInfo.getClassCode());
            info.put("description", classInfo.getDescription());
            info.put("studentCount", classInfo.getStudentCount());
            info.put("status", classInfo.getStatus());
            result.add(info);
        }
        return Result.success(result);
    }
}
