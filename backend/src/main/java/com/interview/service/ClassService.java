package com.interview.service;

import com.interview.entity.ClassInfo;
import com.interview.entity.ClassMember;
import com.interview.repository.ClassInfoRepository;
import com.interview.repository.ClassMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 班级服务
 */
@Service
public class ClassService {

    @Autowired
    private ClassInfoRepository classRepository;

    @Autowired
    private ClassMemberRepository memberRepository;

    /**
     * 创建班级
     */
    @Transactional
    public ClassInfo createClass(ClassInfo classInfo) {
        // 生成班级邀请码
        classInfo.setClassCode(generateClassCode());
        classInfo.setStatus(ClassInfo.ClassStatus.ACTIVE);
        classInfo.setCreatedAt(LocalDateTime.now());
        classInfo.setUpdatedAt(LocalDateTime.now());

        return classRepository.save(classInfo);
    }

    /**
     * 更新班级信息
     */
    @Transactional
    public ClassInfo updateClass(Long classId, ClassInfo updatedClass) {
        ClassInfo classInfo = classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("班级不存在"));

        if (updatedClass.getName() != null) {
            classInfo.setName(updatedClass.getName());
        }
        if (updatedClass.getDescription() != null) {
            classInfo.setDescription(updatedClass.getDescription());
        }
        if (updatedClass.getMaxStudents() != null) {
            classInfo.setMaxStudents(updatedClass.getMaxStudents());
        }
        if (updatedClass.getCourseId() != null) {
            classInfo.setCourseId(updatedClass.getCourseId());
        }
        if (updatedClass.getStartDate() != null) {
            classInfo.setStartDate(updatedClass.getStartDate());
        }
        if (updatedClass.getEndDate() != null) {
            classInfo.setEndDate(updatedClass.getEndDate());
        }

        classInfo.setUpdatedAt(LocalDateTime.now());
        return classRepository.save(classInfo);
    }

    /**
     * 结束班级
     */
    @Transactional
    public ClassInfo endClass(Long classId) {
        ClassInfo classInfo = classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("班级不存在"));

        classInfo.setStatus(ClassInfo.ClassStatus.ENDED);
        classInfo.setEndDate(LocalDateTime.now());
        return classRepository.save(classInfo);
    }

    /**
     * 归档班级
     */
    @Transactional
    public ClassInfo archiveClass(Long classId) {
        ClassInfo classInfo = classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("班级不存在"));

        classInfo.setStatus(ClassInfo.ClassStatus.ARCHIVED);
        return classRepository.save(classInfo);
    }

    /**
     * 学生加入班级
     */
    @Transactional
    public ClassMember joinClass(String classCode, Long studentId) {
        ClassInfo classInfo = classRepository.findByClassCode(classCode)
            .orElseThrow(() -> new RuntimeException("班级邀请码无效"));

        // 检查班级状态
        if (classInfo.getStatus() != ClassInfo.ClassStatus.ACTIVE) {
            throw new RuntimeException("班级不是活跃状态，无法加入");
        }

        // 检查是否已加入
        if (memberRepository.existsByClassIdAndStudentId(classInfo.getId(), studentId)) {
            throw new RuntimeException("已经加入该班级");
        }

        // 检查人数限制
        long currentCount = memberRepository.countByClassIdAndStatus(
            classInfo.getId(),
            ClassMember.MemberStatus.ACTIVE
        );
        if (currentCount >= classInfo.getMaxStudents()) {
            throw new RuntimeException("班级已满");
        }

        // 创建成员记录
        ClassMember member = new ClassMember();
        member.setClassId(classInfo.getId());
        member.setStudentId(studentId);
        member.setRole(ClassMember.MemberRole.STUDENT);
        member.setStatus(ClassMember.MemberStatus.ACTIVE);
        member.setJoinTime(LocalDateTime.now());

        ClassMember savedMember = memberRepository.save(member);

        // 更新班级学生数
        classInfo.setStudentCount(classInfo.getStudentCount() + 1);
        classRepository.save(classInfo);

        return savedMember;
    }

    /**
     * 学生退出班级
     */
    @Transactional
    public void leaveClass(Long classId, Long studentId) {
        ClassMember member = memberRepository.findByClassIdAndStudentId(classId, studentId)
            .orElseThrow(() -> new RuntimeException("未加入该班级"));

        member.setStatus(ClassMember.MemberStatus.DROPPED);
        member.setLeaveTime(LocalDateTime.now());
        memberRepository.save(member);

        // 更新班级学生数
        ClassInfo classInfo = classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("班级不存在"));
        classInfo.setStudentCount(classInfo.getStudentCount() - 1);
        classRepository.save(classInfo);
    }

    /**
     * 移除学生
     */
    @Transactional
    public void removeMember(Long classId, Long studentId) {
        ClassMember member = memberRepository.findByClassIdAndStudentId(classId, studentId)
            .orElseThrow(() -> new RuntimeException("该学生不在班级中"));

        member.setStatus(ClassMember.MemberStatus.KICKED);
        member.setLeaveTime(LocalDateTime.now());
        memberRepository.save(member);

        // 更新班级学生数
        ClassInfo classInfo = classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("班级不存在"));
        classInfo.setStudentCount(classInfo.getStudentCount() - 1);
        classRepository.save(classInfo);
    }

    /**
     * 获取班级信息
     */
    public ClassInfo getClass(Long classId) {
        return classRepository.findById(classId)
            .orElseThrow(() -> new RuntimeException("班级不存在"));
    }

    /**
     * 获取教师的班级列表
     */
    public List<ClassInfo> getTeacherClasses(Long teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }

    /**
     * 获取班级成员
     */
    public List<ClassMember> getClassMembers(Long classId) {
        return memberRepository.findByClassIdAndStatus(classId, ClassMember.MemberStatus.ACTIVE);
    }

    /**
     * 获取学生加入的班级
     */
    public List<ClassMember> getStudentClasses(Long studentId) {
        return memberRepository.findByStudentId(studentId);
    }

    /**
     * 生成班级邀请码
     */
    private String generateClassCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        // 检查是否重复
        if (classRepository.findByClassCode(code.toString()).isPresent()) {
            return generateClassCode(); // 递归生成新的
        }

        return code.toString();
    }
}
