-- 就业面试辅助系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS interview_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE interview_system;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
  `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
  `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
  `avatar` VARCHAR(255) COMMENT '头像URL',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `role` ENUM('STUDENT', 'TEACHER', 'ADMIN') DEFAULT 'STUDENT',
  `membership_type` ENUM('FREE', 'VIP', 'ENTERPRISE') DEFAULT 'FREE',
  `membership_expire_at` DATETIME COMMENT 'VIP到期时间',
  `target_position` VARCHAR(100) COMMENT '目标岗位',
  `points` INT DEFAULT 0 COMMENT '积分',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_email` (`email`),
  INDEX `idx_phone` (`phone`),
  INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 简历表
CREATE TABLE IF NOT EXISTS `resume` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `education` VARCHAR(50) COMMENT '学历',
  `school` VARCHAR(100) COMMENT '学校',
  `major` VARCHAR(100) COMMENT '专业',
  `graduation_year` INT COMMENT '毕业年份',
  `work_years` INT COMMENT '工作年限',
  `skills` TEXT COMMENT '技能（JSON数组）',
  `self_introduction` TEXT COMMENT '自我介绍',
  `resume_url` VARCHAR(255) COMMENT '简历文件URL',
  `score` INT COMMENT 'AI评分',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- 项目经验表
CREATE TABLE IF NOT EXISTS `project_experience` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `resume_id` BIGINT NOT NULL,
  `project_name` VARCHAR(100) NOT NULL,
  `project_description` TEXT,
  `role` VARCHAR(50) COMMENT '担任角色',
  `responsibilities` TEXT COMMENT '职责描述',
  `technologies` TEXT COMMENT '技术栈（JSON数组）',
  `achievements` TEXT COMMENT '项目成果',
  `start_date` DATE,
  `end_date` DATE,
  FOREIGN KEY (`resume_id`) REFERENCES `resume`(`id`) ON DELETE CASCADE,
  INDEX `idx_resume_id` (`resume_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目经验表';

-- 知识分类表
CREATE TABLE IF NOT EXISTS `knowledge_category` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT COMMENT '父分类ID',
  `level` INT COMMENT '层级',
  `sort_order` INT COMMENT '排序',
  `icon` VARCHAR(255) COMMENT '图标',
  `description` TEXT COMMENT '描述',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`parent_id`) REFERENCES `knowledge_category`(`id`) ON DELETE CASCADE,
  INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识分类表';

-- 知识点表
CREATE TABLE IF NOT EXISTS `knowledge_point` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `category_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL COMMENT '知识点标题',
  `content` LONGTEXT COMMENT '内容（支持Markdown）',
  `difficulty` ENUM('EASY', 'MEDIUM', 'HARD'),
  `importance` INT COMMENT '重要度(1-5)',
  `tags` VARCHAR(500) COMMENT '标签（逗号分隔）',
  `prerequisite_points` TEXT COMMENT '前置知识点ID（JSON数组）',
  `related_points` TEXT COMMENT '相关知识点ID（JSON数组）',
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `created_by` BIGINT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`category_id`) REFERENCES `knowledge_category`(`id`) ON DELETE CASCADE,
  INDEX `idx_category` (`category_id`),
  INDEX `idx_difficulty` (`difficulty`),
  FULLTEXT INDEX `ft_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识点表';

-- 学习记录表
CREATE TABLE IF NOT EXISTS `study_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `knowledge_point_id` BIGINT NOT NULL,
  `progress` INT DEFAULT 0 COMMENT '进度（0-100）',
  `study_time` INT DEFAULT 0 COMMENT '学习时长（秒）',
  `status` ENUM('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'NOT_STARTED',
  `mastery_level` INT COMMENT '掌握度（0-100）',
  `notes` TEXT COMMENT '学习笔记',
  `last_study_at` DATETIME,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_point`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user_kp` (`user_id`, `knowledge_point_id`),
  INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录表';

-- 题目表
CREATE TABLE IF NOT EXISTS `question` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '题目标题',
  `content` LONGTEXT NOT NULL COMMENT '题目内容',
  `type` ENUM('CHOICE', 'MULTIPLE_CHOICE', 'CODING', 'SHORT_ANSWER', 'SYSTEM_DESIGN') NOT NULL,
  `difficulty` ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
  `category` VARCHAR(50) COMMENT '一级分类',
  `sub_category` VARCHAR(50) COMMENT '二级分类',
  `tags` VARCHAR(500) COMMENT '标签（逗号分隔）',
  `option_a` TEXT,
  `option_b` TEXT,
  `option_c` TEXT,
  `option_d` TEXT,
  `correct_answer` VARCHAR(10) COMMENT '正确答案',
  `input_format` TEXT COMMENT '输入格式说明',
  `output_format` TEXT COMMENT '输出格式说明',
  `sample_input` TEXT COMMENT '样例输入',
  `sample_output` TEXT COMMENT '样例输出',
  `time_limit` INT COMMENT '时间限制（ms）',
  `memory_limit` INT COMMENT '内存限制（MB）',
  `explanation` LONGTEXT COMMENT '答案解析',
  `key_points` VARCHAR(500) COMMENT '考察知识点',
  `reference_answer` LONGTEXT COMMENT '参考答案',
  `view_count` INT DEFAULT 0,
  `attempt_count` INT DEFAULT 0,
  `pass_count` INT DEFAULT 0,
  `pass_rate` DECIMAL(5,2) COMMENT '通过率',
  `avg_score` DECIMAL(5,2) COMMENT '平均分',
  `like_count` INT DEFAULT 0,
  `collect_count` INT DEFAULT 0,
  `created_by` BIGINT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_type` (`type`),
  INDEX `idx_difficulty` (`difficulty`),
  INDEX `idx_category` (`category`),
  FULLTEXT INDEX `ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 答题记录表
CREATE TABLE IF NOT EXISTS `answer_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `question_id` BIGINT NOT NULL,
  `user_answer` TEXT COMMENT '用户答案',
  `is_correct` BOOLEAN COMMENT '是否正确',
  `score` DECIMAL(5,2) COMMENT '得分',
  `time_spent` INT COMMENT '用时（秒）',
  `submit_count` INT DEFAULT 1 COMMENT '提交次数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`question_id`) REFERENCES `question`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_question` (`user_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- 错题本表
CREATE TABLE IF NOT EXISTS `wrong_question_book` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `question_id` BIGINT NOT NULL,
  `wrong_count` INT DEFAULT 1 COMMENT '错误次数',
  `review_count` INT DEFAULT 0 COMMENT '复习次数',
  `last_review_at` DATETIME COMMENT '最后复习时间',
  `next_review_at` DATETIME COMMENT '下次复习时间',
  `is_mastered` BOOLEAN DEFAULT FALSE COMMENT '是否已掌握',
  `notes` TEXT COMMENT '笔记',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`question_id`) REFERENCES `question`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='错题本表';

-- 题目收藏表
CREATE TABLE IF NOT EXISTS `question_favorites` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `question_id` BIGINT NOT NULL,
  `notes` TEXT COMMENT '收藏备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`question_id`) REFERENCES `question`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目收藏表';

-- 每日一题表
CREATE TABLE IF NOT EXISTS `daily_questions` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `date` DATE NOT NULL UNIQUE COMMENT '日期',
  `question_id` BIGINT NOT NULL,
  `category` VARCHAR(50) COMMENT '分类',
  `difficulty` VARCHAR(20) COMMENT '难度',
  `complete_count` INT DEFAULT 0 COMMENT '完成人数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`question_id`) REFERENCES `question`(`id`) ON DELETE CASCADE,
  INDEX `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日一题表';

-- 积分记录表
CREATE TABLE IF NOT EXISTS `points_records` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `points` INT NOT NULL COMMENT '积分变化',
  `type` VARCHAR(50) NOT NULL COMMENT '积分类型',
  `description` VARCHAR(200) COMMENT '积分说明',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user` (`user_id`),
  INDEX `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

-- 面试会话表
CREATE TABLE IF NOT EXISTS `interview_session` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `session_type` ENUM('TECHNICAL', 'BEHAVIORAL', 'HR') NOT NULL,
  `position` VARCHAR(100) COMMENT '面试岗位',
  `resume_id` BIGINT COMMENT '使用的简历',
  `status` ENUM('ONGOING', 'COMPLETED', 'ABANDONED') DEFAULT 'ONGOING',
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME,
  `duration` INT COMMENT '时长（秒）',
  `total_score` DECIMAL(5,2) COMMENT '总分',
  `conversation` LONGTEXT COMMENT '完整对话记录（JSON）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`resume_id`) REFERENCES `resume`(`id`),
  INDEX `idx_user_type` (`user_id`, `session_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试会话表';

-- 用户能力模型表
CREATE TABLE IF NOT EXISTS `user_ability_model` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `algorithm_ability` DECIMAL(5,2) DEFAULT 0 COMMENT '算法能力',
  `coding_ability` DECIMAL(5,2) DEFAULT 0 COMMENT '编码能力',
  `system_design_ability` DECIMAL(5,2) DEFAULT 0 COMMENT '系统设计',
  `database_ability` DECIMAL(5,2) DEFAULT 0 COMMENT '数据库',
  `network_ability` DECIMAL(5,2) DEFAULT 0 COMMENT '计算机网络',
  `os_ability` DECIMAL(5,2) DEFAULT 0 COMMENT '操作系统',
  `language_skills` TEXT COMMENT '编程语言技能',
  `framework_skills` TEXT COMMENT '框架技能',
  `communication_ability` DECIMAL(5,2) DEFAULT 0,
  `teamwork_ability` DECIMAL(5,2) DEFAULT 0,
  `problem_solving_ability` DECIMAL(5,2) DEFAULT 0,
  `learning_ability` DECIMAL(5,2) DEFAULT 0,
  `overall_score` DECIMAL(5,2) DEFAULT 0,
  `level` ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') DEFAULT 'BEGINNER',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户能力模型表';

-- 教师信息表
CREATE TABLE IF NOT EXISTS `teacher_profile` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL UNIQUE COMMENT '用户ID',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `title` VARCHAR(100) COMMENT '职称',
  `organization` VARCHAR(100) COMMENT '所属机构',
  `department` VARCHAR(100) COMMENT '院系/部门',
  `expertise` TEXT COMMENT '专业领域（JSON数组）',
  `introduction` TEXT COMMENT '个人简介',
  `teaching_years` INT COMMENT '教学年限',
  `is_verified` BOOLEAN DEFAULT FALSE COMMENT '是否认证',
  `verify_status` ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
  `verify_time` DATETIME COMMENT '认证时间',
  `student_count` INT DEFAULT 0 COMMENT '学生数量',
  `course_count` INT DEFAULT 0 COMMENT '课程数量',
  `avg_rating` DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_verify_status` (`verify_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 课程表
CREATE TABLE IF NOT EXISTS `course` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `title` VARCHAR(200) NOT NULL COMMENT '课程标题',
  `description` TEXT COMMENT '课程描述',
  `cover_image` VARCHAR(255) COMMENT '课程封面',
  `category` VARCHAR(100) COMMENT '课程分类',
  `difficulty` ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') COMMENT '难度',
  `tags` VARCHAR(500) COMMENT '标签',
  `learning_path` LONGTEXT COMMENT '学习路径（JSON）',
  `status` ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED') DEFAULT 'DRAFT',
  `student_count` INT DEFAULT 0 COMMENT '学生人数',
  `rating` DECIMAL(3,2) DEFAULT 5.00 COMMENT '课程评分',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_teacher` (`teacher_id`),
  INDEX `idx_status` (`status`),
  FULLTEXT INDEX `ft_title_desc` (`title`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 课程章节表
CREATE TABLE IF NOT EXISTS `course_chapter` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `course_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `description` TEXT COMMENT '章节描述',
  `sort_order` INT NOT NULL COMMENT '排序',
  `parent_id` BIGINT COMMENT '父章节ID',
  `is_free` BOOLEAN DEFAULT FALSE COMMENT '是否免费',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`parent_id`) REFERENCES `course_chapter`(`id`) ON DELETE CASCADE,
  INDEX `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- 课程内容关联表
CREATE TABLE IF NOT EXISTS `course_content` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `course_id` BIGINT NOT NULL,
  `chapter_id` BIGINT COMMENT '章节ID',
  `content_type` ENUM('KNOWLEDGE_POINT', 'QUESTION', 'TUTORIAL', 'VIDEO') NOT NULL,
  `content_id` BIGINT NOT NULL COMMENT '内容ID',
  `sort_order` INT NOT NULL COMMENT '排序',
  `is_required` BOOLEAN DEFAULT TRUE COMMENT '是否必修',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`chapter_id`) REFERENCES `course_chapter`(`id`) ON DELETE CASCADE,
  INDEX `idx_course_chapter` (`course_id`, `chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程内容关联表';

-- 班级表
CREATE TABLE IF NOT EXISTS `class` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `course_id` BIGINT COMMENT '关联课程ID',
  `name` VARCHAR(100) NOT NULL COMMENT '班级名称',
  `description` TEXT COMMENT '班级描述',
  `invite_code` VARCHAR(20) UNIQUE NOT NULL COMMENT '邀请码',
  `max_students` INT DEFAULT 100 COMMENT '最大学生数',
  `student_count` INT DEFAULT 0 COMMENT '当前学生数',
  `start_date` DATE COMMENT '开班日期',
  `end_date` DATE COMMENT '结束日期',
  `status` ENUM('ACTIVE', 'CLOSED', 'ARCHIVED') DEFAULT 'ACTIVE',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE SET NULL,
  INDEX `idx_teacher` (`teacher_id`),
  INDEX `idx_invite_code` (`invite_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 班级成员表
CREATE TABLE IF NOT EXISTS `class_member` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `class_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role` ENUM('STUDENT', 'ASSISTANT') DEFAULT 'STUDENT',
  `join_date` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('ACTIVE', 'QUIT') DEFAULT 'ACTIVE',
  UNIQUE KEY `uk_class_user` (`class_id`, `user_id`),
  FOREIGN KEY (`class_id`) REFERENCES `class`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级成员表';

-- 作业任务表
CREATE TABLE IF NOT EXISTS `assignment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
  `class_id` BIGINT COMMENT '班级ID',
  `course_id` BIGINT COMMENT '课程ID',
  `title` VARCHAR(200) NOT NULL COMMENT '作业标题',
  `description` TEXT COMMENT '作业描述',
  `type` ENUM('PRACTICE', 'EXAM', 'PROJECT') DEFAULT 'PRACTICE',
  `question_ids` TEXT COMMENT '题目ID列表（JSON数组）',
  `total_score` INT DEFAULT 100 COMMENT '总分',
  `passing_score` INT DEFAULT 60 COMMENT '及格分',
  `time_limit` INT COMMENT '时间限制（分钟）',
  `start_time` DATETIME COMMENT '开始时间',
  `deadline` DATETIME COMMENT '截止时间',
  `allow_late_submit` BOOLEAN DEFAULT FALSE COMMENT '允许延迟提交',
  `auto_grade` BOOLEAN DEFAULT TRUE COMMENT '自动批改',
  `status` ENUM('DRAFT', 'PUBLISHED', 'CLOSED') DEFAULT 'DRAFT',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`class_id`) REFERENCES `class`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE SET NULL,
  INDEX `idx_teacher` (`teacher_id`),
  INDEX `idx_class` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业任务表';

-- 作业提交表
CREATE TABLE IF NOT EXISTS `assignment_submission` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `assignment_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `answers` LONGTEXT COMMENT '答案（JSON格式）',
  `score` DECIMAL(5,2) COMMENT '得分',
  `auto_score` DECIMAL(5,2) COMMENT '自动评分',
  `manual_score` DECIMAL(5,2) COMMENT '人工评分',
  `status` ENUM('SUBMITTED', 'GRADING', 'GRADED') DEFAULT 'SUBMITTED',
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `grade_time` DATETIME COMMENT '批改时间',
  `is_late` BOOLEAN DEFAULT FALSE COMMENT '是否延迟提交',
  `time_spent` INT COMMENT '用时（分钟）',
  UNIQUE KEY `uk_assignment_user` (`assignment_id`, `user_id`),
  FOREIGN KEY (`assignment_id`) REFERENCES `assignment`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- 批改记录表
CREATE TABLE IF NOT EXISTS `grading_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `submission_id` BIGINT NOT NULL,
  `question_id` BIGINT NOT NULL,
  `user_answer` TEXT COMMENT '学生答案',
  `is_correct` BOOLEAN COMMENT '是否正确',
  `score` DECIMAL(5,2) COMMENT '得分',
  `teacher_comment` TEXT COMMENT '教师评语',
  `graded_by` BIGINT COMMENT '批改教师ID',
  `graded_at` DATETIME COMMENT '批改时间',
  FOREIGN KEY (`submission_id`) REFERENCES `assignment_submission`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`question_id`) REFERENCES `question`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`graded_by`) REFERENCES `users`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批改记录表';

-- 内容审核表
CREATE TABLE IF NOT EXISTS `content_review` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `content_type` ENUM('QUESTION', 'KNOWLEDGE_POINT', 'TUTORIAL', 'COURSE') NOT NULL,
  `content_id` BIGINT NOT NULL COMMENT '内容ID',
  `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
  `reviewer_id` BIGINT COMMENT '审核者ID',
  `status` ENUM('PENDING', 'APPROVED', 'REJECTED', 'NEED_REVISION') DEFAULT 'PENDING',
  `review_comment` TEXT COMMENT '审核意见',
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `review_time` DATETIME COMMENT '审核时间',
  `revision_count` INT DEFAULT 0 COMMENT '修改次数',
  FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`reviewer_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_content` (`content_type`, `content_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核表';

-- 课程评价表
CREATE TABLE IF NOT EXISTS `course_rating` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `course_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `rating` INT NOT NULL COMMENT '评分（1-5）',
  `content_quality` INT COMMENT '内容质量',
  `teaching_method` INT COMMENT '教学方法',
  `practice_quality` INT COMMENT '练习质量',
  `comment` TEXT COMMENT '评价内容',
  `is_anonymous` BOOLEAN DEFAULT FALSE COMMENT '是否匿名',
  `is_verified` BOOLEAN DEFAULT FALSE COMMENT '是否已认证',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_course_user` (`course_id`, `user_id`),
  FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';

-- 教师答疑表
CREATE TABLE IF NOT EXISTS `teacher_qa` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `student_id` BIGINT NOT NULL,
  `teacher_id` BIGINT COMMENT '指定教师',
  `question_content` TEXT NOT NULL COMMENT '问题内容',
  `question_images` TEXT COMMENT '问题图片',
  `related_question_id` BIGINT COMMENT '相关题目ID',
  `related_knowledge_id` BIGINT COMMENT '相关知识点ID',
  `answer_content` TEXT COMMENT '回答内容',
  `answered_by` BIGINT COMMENT '回答教师ID',
  `status` ENUM('PENDING', 'ANSWERED', 'CLOSED') DEFAULT 'PENDING',
  `priority` ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
  `is_public` BOOLEAN DEFAULT TRUE COMMENT '是否公开',
  `helpful_count` INT DEFAULT 0 COMMENT '有帮助数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `answered_at` DATETIME COMMENT '回答时间',
  FOREIGN KEY (`student_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师答疑表';

-- 教学统计表
CREATE TABLE IF NOT EXISTS `teaching_statistics` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `teacher_id` BIGINT NOT NULL,
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_students` INT DEFAULT 0 COMMENT '总学生数',
  `active_students` INT DEFAULT 0 COMMENT '活跃学生数',
  `questions_created` INT DEFAULT 0 COMMENT '创建题目数',
  `courses_created` INT DEFAULT 0 COMMENT '创建课程数',
  `assignments_created` INT DEFAULT 0 COMMENT '布置作业数',
  `assignments_graded` INT DEFAULT 0 COMMENT '批改作业数',
  `student_avg_score` DECIMAL(5,2) COMMENT '学生平均分',
  `positive_feedback_count` INT DEFAULT 0 COMMENT '好评数',
  `total_feedback_count` INT DEFAULT 0 COMMENT '总评价数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_teacher_date` (`teacher_id`, `stat_date`),
  FOREIGN KEY (`teacher_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学统计表';

-- 帖子表
CREATE TABLE IF NOT EXISTS `post` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `type` ENUM('INTERVIEW_EXPERIENCE', 'QUESTION', 'ARTICLE', 'DISCUSSION') NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `content` LONGTEXT NOT NULL,
  `tags` VARCHAR(500) COMMENT '标签',
  `company` VARCHAR(100) COMMENT '相关公司',
  `position` VARCHAR(100) COMMENT '相关职位',
  `is_top` BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
  `is_essence` BOOLEAN DEFAULT FALSE COMMENT '是否精华',
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `comment_count` INT DEFAULT 0,
  `collect_count` INT DEFAULT 0,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_type` (`type`),
  INDEX `idx_company` (`company`),
  FULLTEXT INDEX `ft_title_content` (`title`, `content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `parent_id` BIGINT COMMENT '父评论ID',
  `content` TEXT NOT NULL,
  `like_count` INT DEFAULT 0,
  `is_author_reply` BOOLEAN DEFAULT FALSE COMMENT '是否作者回复',
  `is_best_answer` BOOLEAN DEFAULT FALSE COMMENT '是否最佳答案',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`post_id`) REFERENCES `post`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`parent_id`) REFERENCES `comment`(`id`) ON DELETE CASCADE,
  INDEX `idx_post` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 学习小组表
CREATE TABLE IF NOT EXISTS `study_group` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT,
  `avatar` VARCHAR(255),
  `creator_id` BIGINT NOT NULL,
  `member_count` INT DEFAULT 1,
  `max_member_count` INT DEFAULT 100,
  `target` VARCHAR(200) COMMENT '学习目标',
  `is_public` BOOLEAN DEFAULT TRUE,
  `status` ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习小组表';

-- 小组成员表
CREATE TABLE IF NOT EXISTS `group_member` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `group_id` BIGINT NOT NULL,
  `user_id` BIGINT NOT NULL,
  `role` ENUM('OWNER', 'ADMIN', 'MEMBER') DEFAULT 'MEMBER',
  `join_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`group_id`) REFERENCES `study_group`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  UNIQUE KEY `uk_group_user` (`group_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小组成员表';

-- 教程表
CREATE TABLE IF NOT EXISTS `tutorial` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `content` LONGTEXT,
  `category` VARCHAR(100),
  `difficulty` ENUM('EASY', 'MEDIUM', 'HARD'),
  `tags` VARCHAR(500),
  `video_url` VARCHAR(500),
  `duration` INT COMMENT '时长（秒）',
  `view_count` INT DEFAULT 0,
  `like_count` INT DEFAULT 0,
  `created_by` BIGINT,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`created_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,
  INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教程表';

-- 测试记录表
CREATE TABLE IF NOT EXISTS `test_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `test_type` VARCHAR(50),
  `total_score` DECIMAL(5,2),
  `pass_status` BOOLEAN,
  `duration` INT COMMENT '用时（分钟）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试记录表';
