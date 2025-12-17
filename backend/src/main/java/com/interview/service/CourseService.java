package com.interview.service;

import com.interview.entity.Course;
import com.interview.entity.CourseChapter;
import com.interview.entity.CourseContent;
import com.interview.repository.CourseRepository;
import com.interview.repository.CourseChapterRepository;
import com.interview.repository.CourseContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程服务
 */
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseChapterRepository chapterRepository;

    @Autowired
    private CourseContentRepository contentRepository;

    /**
     * 创建课程
     */
    @Transactional
    public Course createCourse(Course course) {
        course.setStatus(Course.CourseStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    /**
     * 更新课程
     */
    @Transactional
    public Course updateCourse(Long courseId, Course updatedCourse) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (updatedCourse.getTitle() != null) {
            course.setTitle(updatedCourse.getTitle());
        }
        if (updatedCourse.getDescription() != null) {
            course.setDescription(updatedCourse.getDescription());
        }
        if (updatedCourse.getCover() != null) {
            course.setCover(updatedCourse.getCover());
        }
        if (updatedCourse.getCategory() != null) {
            course.setCategory(updatedCourse.getCategory());
        }
        if (updatedCourse.getDifficulty() != null) {
            course.setDifficulty(updatedCourse.getDifficulty());
        }
        if (updatedCourse.getTags() != null) {
            course.setTags(updatedCourse.getTags());
        }
        if (updatedCourse.getIsPublic() != null) {
            course.setIsPublic(updatedCourse.getIsPublic());
        }

        course.setUpdatedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    /**
     * 发布课程
     */
    @Transactional
    public Course publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));

        if (course.getChapterCount() == 0) {
            throw new RuntimeException("课程至少需要一个章节才能发布");
        }

        course.setStatus(Course.CourseStatus.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        return courseRepository.save(course);
    }

    /**
     * 归档课程
     */
    @Transactional
    public Course archiveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));

        course.setStatus(Course.CourseStatus.ARCHIVED);
        return courseRepository.save(course);
    }

    /**
     * 删除课程
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));

        // 删除课程的所有章节和内容
        List<CourseChapter> chapters = chapterRepository.findByCourseId(courseId);
        for (CourseChapter chapter : chapters) {
            contentRepository.deleteByChapterId(chapter.getId());
        }
        chapters.forEach(chapter -> chapterRepository.deleteById(chapter.getId()));

        courseRepository.deleteById(courseId);
    }

    /**
     * 获取课程详情
     */
    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));
    }

    /**
     * 获取教师的课程列表
     */
    public List<Course> getTeacherCourses(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    /**
     * 获取教师的指定状态课程
     */
    public List<Course> getTeacherCoursesByStatus(Long teacherId, Course.CourseStatus status) {
        return courseRepository.findByTeacherIdAndStatus(teacherId, status);
    }

    /**
     * 搜索课程
     */
    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByTitleContaining(keyword);
    }

    /**
     * 获取热门课程
     */
    public List<Course> getPopularCourses() {
        return courseRepository.findByOrderByStudentCountDesc();
    }

    /**
     * 获取最新课程
     */
    public List<Course> getLatestCourses() {
        return courseRepository.findTop10ByStatusOrderByCreatedAtDesc(Course.CourseStatus.PUBLISHED);
    }

    /**
     * 添加章节
     */
    @Transactional
    public CourseChapter addChapter(CourseChapter chapter) {
        CourseChapter savedChapter = chapterRepository.save(chapter);

        // 更新课程章节数
        Course course = courseRepository.findById(chapter.getCourseId())
            .orElseThrow(() -> new RuntimeException("课程不存在"));
        course.setChapterCount(course.getChapterCount() + 1);
        courseRepository.save(course);

        return savedChapter;
    }

    /**
     * 获取课程章节
     */
    public List<CourseChapter> getCourseChapters(Long courseId) {
        return chapterRepository.findByCourseIdOrderBySortOrder(courseId);
    }

    /**
     * 添加章节内容
     */
    @Transactional
    public CourseContent addContent(CourseContent content) {
        CourseContent savedContent = contentRepository.save(content);

        // 更新章节内容数
        CourseChapter chapter = chapterRepository.findById(content.getChapterId())
            .orElseThrow(() -> new RuntimeException("章节不存在"));
        chapter.setContentCount(chapter.getContentCount() + 1);
        chapterRepository.save(chapter);

        return savedContent;
    }

    /**
     * 获取章节内容
     */
    public List<CourseContent> getChapterContents(Long chapterId) {
        return contentRepository.findByChapterIdOrderBySortOrder(chapterId);
    }

    /**
     * 更新课程浏览量
     */
    @Transactional
    public void incrementViewCount(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));
        course.setViewCount(course.getViewCount() + 1);
        courseRepository.save(course);
    }

    /**
     * 更新课程学生数
     */
    @Transactional
    public void updateStudentCount(Long courseId, int delta) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new RuntimeException("课程不存在"));
        course.setStudentCount(course.getStudentCount() + delta);
        courseRepository.save(course);
    }
}
