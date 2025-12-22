package com.interview.service;

import com.interview.entity.ProjectExperience;
import com.interview.entity.Resume;
import com.interview.repository.ProjectExperienceRepository;
import com.interview.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ProjectExperienceRepository projectExperienceRepository;

    public Resume createResume(Resume resume) {
        return resumeRepository.save(resume);
    }

    public Resume getResumeById(Long id) {
        return resumeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("简历不存在"));
    }

    @Transactional
    public Resume updateResume(Long id, Resume resume) {
        Resume existingResume = getResumeById(id);
        existingResume.setName(resume.getName());
        existingResume.setEducation(resume.getEducation());
        existingResume.setSchool(resume.getSchool());
        existingResume.setMajor(resume.getMajor());
        existingResume.setGraduationYear(resume.getGraduationYear());
        existingResume.setWorkYears(resume.getWorkYears());
        existingResume.setSkills(resume.getSkills());
        existingResume.setSelfIntroduction(resume.getSelfIntroduction());
        existingResume.setResumeUrl(resume.getResumeUrl());
        return resumeRepository.save(existingResume);
    }

    public void deleteResume(Long id) {
        resumeRepository.deleteById(id);
    }

    public List<Resume> getUserResumes(Long userId) {
        return resumeRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Resume getLatestResume(Long userId) {
        return resumeRepository.findFirstByUserIdOrderByUpdatedAtDesc(userId)
            .orElse(null);
    }

    public Long getResumeCount(Long userId) {
        return resumeRepository.countByUserId(userId);
    }

    public ProjectExperience addProjectExperience(ProjectExperience project) {
        return projectExperienceRepository.save(project);
    }

    public ProjectExperience updateProjectExperience(Long id, ProjectExperience project) {
        ProjectExperience existing = projectExperienceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("项目经验不存在"));
        existing.setProjectName(project.getProjectName());
        existing.setProjectDescription(project.getProjectDescription());
        existing.setRole(project.getRole());
        existing.setResponsibilities(project.getResponsibilities());
        existing.setTechnologies(project.getTechnologies());
        existing.setAchievements(project.getAchievements());
        existing.setStartDate(project.getStartDate());
        existing.setEndDate(project.getEndDate());
        return projectExperienceRepository.save(existing);
    }

    public void deleteProjectExperience(Long id) {
        projectExperienceRepository.deleteById(id);
    }

    public List<ProjectExperience> getResumeProjects(Long resumeId) {
        return projectExperienceRepository.findByResumeIdOrderByStartDateDesc(resumeId);
    }

    @Transactional
    public int calculateResumeScore(Resume resume) {
        int score = 0;
        
        if (resume.getName() != null && !resume.getName().isEmpty()) score += 10;
        if (resume.getEducation() != null) score += 10;
        if (resume.getSchool() != null && !resume.getSchool().isEmpty()) score += 10;
        if (resume.getMajor() != null && !resume.getMajor().isEmpty()) score += 10;
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) score += 20;
        if (resume.getSelfIntroduction() != null && !resume.getSelfIntroduction().isEmpty()) score += 15;
        
        List<ProjectExperience> projects = getResumeProjects(resume.getId());
        score += Math.min(projects.size() * 5, 25);
        
        resume.setScore(score);
        resumeRepository.save(resume);
        
        return score;
    }
}
