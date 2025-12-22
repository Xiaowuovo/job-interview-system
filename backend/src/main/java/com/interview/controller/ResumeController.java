package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.ProjectExperience;
import com.interview.entity.Resume;
import com.interview.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping
    public Result<Resume> createResume(@RequestBody Resume resume, @RequestHeader("userId") Long userId) {
        resume.setUserId(userId);
        Resume created = resumeService.createResume(resume);
        return Result.success(created);
    }

    @GetMapping("/{id}")
    public Result<Resume> getResume(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        return Result.success(resume);
    }

    @PutMapping("/{id}")
    public Result<Resume> updateResume(@PathVariable Long id, @RequestBody Resume resume, @RequestHeader("userId") Long userId) {
        Resume existingResume = resumeService.getResumeById(id);
        if (!existingResume.getUserId().equals(userId)) {
            return Result.error("无权修改他人简历");
        }
        Resume updated = resumeService.updateResume(id, resume);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteResume(@PathVariable Long id, @RequestHeader("userId") Long userId) {
        Resume resume = resumeService.getResumeById(id);
        if (!resume.getUserId().equals(userId)) {
            return Result.error("无权删除他人简历");
        }
        resumeService.deleteResume(id);
        return Result.success();
    }

    @GetMapping("/user")
    public Result<List<Resume>> getUserResumes(@RequestHeader("userId") Long userId) {
        List<Resume> resumes = resumeService.getUserResumes(userId);
        return Result.success(resumes);
    }

    @GetMapping("/user/latest")
    public Result<Resume> getLatestResume(@RequestHeader("userId") Long userId) {
        Resume resume = resumeService.getLatestResume(userId);
        return Result.success(resume);
    }

    @GetMapping("/user/count")
    public Result<Long> getResumeCount(@RequestHeader("userId") Long userId) {
        Long count = resumeService.getResumeCount(userId);
        return Result.success(count);
    }

    @PostMapping("/{resumeId}/projects")
    public Result<ProjectExperience> addProject(@PathVariable Long resumeId, @RequestBody ProjectExperience project) {
        project.setResumeId(resumeId);
        ProjectExperience created = resumeService.addProjectExperience(project);
        return Result.success(created);
    }

    @PutMapping("/projects/{id}")
    public Result<ProjectExperience> updateProject(@PathVariable Long id, @RequestBody ProjectExperience project) {
        ProjectExperience updated = resumeService.updateProjectExperience(id, project);
        return Result.success(updated);
    }

    @DeleteMapping("/projects/{id}")
    public Result<Void> deleteProject(@PathVariable Long id) {
        resumeService.deleteProjectExperience(id);
        return Result.success();
    }

    @GetMapping("/{resumeId}/projects")
    public Result<List<ProjectExperience>> getResumeProjects(@PathVariable Long resumeId) {
        List<ProjectExperience> projects = resumeService.getResumeProjects(resumeId);
        return Result.success(projects);
    }

    @PostMapping("/{id}/calculate-score")
    public Result<Integer> calculateScore(@PathVariable Long id) {
        Resume resume = resumeService.getResumeById(id);
        int score = resumeService.calculateResumeScore(resume);
        return Result.success(score);
    }
}
