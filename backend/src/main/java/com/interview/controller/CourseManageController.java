package com.interview.controller;

import com.interview.common.Result;
import com.interview.entity.Course;
import com.interview.entity.CourseChapter;
import com.interview.entity.CourseContent;
import com.interview.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/api/teacher/course")
@CrossOrigin
public class CourseManageController {

    @Autowired
    private CourseService courseService;

    /**
     * 创建课程
     */
    @PostMapping
    public Result<Course> createCourse(@RequestBody Course course) {
        try {
            Course created = courseService.createCourse(course);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    public Result<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        try {
            Course updated = courseService.updateCourse(id, course);
            return Result.success(updated);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 发布课程
     */
    @PostMapping("/{id}/publish")
    public Result<Course> publishCourse(@PathVariable Long id) {
        try {
            Course published = courseService.publishCourse(id);
            return Result.success(published);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 归档课程
     */
    @PostMapping("/{id}/archive")
    public Result<Course> archiveCourse(@PathVariable Long id) {
        try {
            Course archived = courseService.archiveCourse(id);
            return Result.success(archived);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        try {
            courseService.deleteCourse(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    public Result<Course> getCourse(@PathVariable Long id) {
        try {
            Course course = courseService.getCourse(id);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error(404, e.getMessage());
        }
    }

    /**
     * 获取教师的课程列表
     */
    @GetMapping("/my-courses")
    public Result<List<Course>> getMyCourses(@RequestParam Long teacherId) {
        try {
            List<Course> courses = courseService.getTeacherCourses(teacherId);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public Result<List<Course>> searchCourses(@RequestParam String keyword) {
        try {
            List<Course> courses = courseService.searchCourses(keyword);
            return Result.success(courses);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 添加章节
     */
    @PostMapping("/{courseId}/chapter")
    public Result<CourseChapter> addChapter(@PathVariable Long courseId, @RequestBody CourseChapter chapter) {
        try {
            chapter.setCourseId(courseId);
            CourseChapter created = courseService.addChapter(chapter);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取课程章节
     */
    @GetMapping("/{courseId}/chapters")
    public Result<List<CourseChapter>> getChapters(@PathVariable Long courseId) {
        try {
            List<CourseChapter> chapters = courseService.getCourseChapters(courseId);
            return Result.success(chapters);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 添加章节内容
     */
    @PostMapping("/chapter/{chapterId}/content")
    public Result<CourseContent> addContent(@PathVariable Long chapterId, @RequestBody CourseContent content) {
        try {
            content.setChapterId(chapterId);
            CourseContent created = courseService.addContent(content);
            return Result.success(created);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    /**
     * 获取章节内容
     */
    @GetMapping("/chapter/{chapterId}/contents")
    public Result<List<CourseContent>> getContents(@PathVariable Long chapterId) {
        try {
            List<CourseContent> contents = courseService.getChapterContents(chapterId);
            return Result.success(contents);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
