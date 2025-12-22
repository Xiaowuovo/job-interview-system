package com.interview.config;

import com.interview.entity.*;
import com.interview.repository.*;
import com.interview.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TutorialRepository tutorialRepository;
    private final QuestionRepository questionRepository;
    private final UserAbilityModelRepository abilityModelRepository;
    private final KnowledgePointRepository knowledgePointRepository;

    @Override
    public void run(String... args) {
        // 初始化用户
        if (userRepository.count() == 0) {
            log.info("开始初始化默认用户...");
            
            User student = new User();
            student.setUsername("student");
            // 使用加密密码
            String studentSalt = PasswordUtil.generateSalt();
            student.setPassword(PasswordUtil.encryptPassword("123456", studentSalt));
            student.setSalt(studentSalt);
            student.setRole(User.Role.STUDENT);
            student.setEmail("student@example.com");
            student.setNickname("学生用户");
            student.setTargetPosition("Java开发工程师");
            User savedStudent = userRepository.save(student);

            // 创建学生的能力模型
            UserAbilityModel studentAbility = new UserAbilityModel();
            studentAbility.setUserId(savedStudent.getId());
            studentAbility.setAlgorithmAbility(60.0);
            studentAbility.setCodingAbility(70.0);
            studentAbility.setDatabaseAbility(65.0);
            abilityModelRepository.save(studentAbility);

            User teacher = new User();
            teacher.setUsername("teacher");
            // 使用加密密码
            String teacherSalt = PasswordUtil.generateSalt();
            teacher.setPassword(PasswordUtil.encryptPassword("123456", teacherSalt));
            teacher.setSalt(teacherSalt);
            teacher.setRole(User.Role.TEACHER);
            teacher.setEmail("teacher@example.com");
            teacher.setNickname("教师用户");
            User savedTeacher = userRepository.save(teacher);

            // 创建教师的能力模型
            UserAbilityModel teacherAbility = new UserAbilityModel();
            teacherAbility.setUserId(savedTeacher.getId());
            teacherAbility.setAlgorithmAbility(90.0);
            teacherAbility.setCodingAbility(95.0);
            teacherAbility.setDatabaseAbility(92.0);
            teacherAbility.setSystemDesignAbility(88.0);
            abilityModelRepository.save(teacherAbility);
        }

        // 初始化教程
        if (tutorialRepository.count() == 0) {
            initTutorials();
        }

        // 初始化题目
        if (questionRepository.count() == 0) {
            initQuestions();
        }

        // 初始化知识点
        if (knowledgePointRepository.count() == 0) {
            initKnowledgePoints();
        }

        log.info("✅ 数据初始化完成！题目: {}, 教程: {}, 知识点: {}", 
            questionRepository.count(), tutorialRepository.count(), knowledgePointRepository.count());
    }

    private void initTutorials() {
        log.info("初始化教程数据...");
        
        // 基础指导类教程
        saveTutorial("面试前的准备工作", "基础指导", 
            "## 面试前的准备工作\n\n### 1. 了解公司背景\n- 公司主营业务和产品\n- 企业文化和价值观\n- 近期新闻和发展动态\n\n### 2. 准备自我介绍\n- 控制在1-3分钟\n- 突出与岗位相关的经历\n- 展示个人优势和特点\n\n### 3. 整理项目经验\n- 准备2-3个重点项目\n- 明确自己的角色和贡献\n- 量化项目成果\n\n### 4. 常见问题准备\n- 为什么选择我们公司？\n- 你的优缺点是什么？\n- 职业规划是什么？\n\n### 5. 面试当天\n- 着装得体，商务休闲为宜\n- 提前15分钟到达\n- 带好简历和相关材料");
        
        saveTutorial("简历撰写技巧", "基础指导",
            "## 简历撰写技巧\n\n### 1. 基本原则\n- 一页纸原则\n- 简洁明了，重点突出\n- 无错别字和语法错误\n\n### 2. 必备模块\n- 个人信息（姓名、电话、邮箱）\n- 教育背景\n- 工作/实习经历\n- 项目经验\n- 技能清单\n\n### 3. 项目描述STAR法则\n- **S**ituation：项目背景\n- **T**ask：你的任务\n- **A**ction：采取的行动\n- **R**esult：取得的成果\n\n### 4. 量化成果\n- 性能提升了50%\n- 用户增长了30%\n- 节省成本20万\n\n### 5. 注意事项\n- 针对不同岗位调整简历\n- 使用专业术语但不过度\n- PDF格式投递");
        
        saveTutorial("如何做好自我介绍", "基础指导",
            "## 如何做好自我介绍\n\n### 1. 结构化表达\n```\n我是XXX，毕业于XXX大学XXX专业。\n我有X年XXX经验，主要负责XXX。\n我的优势是XXX，曾经XXX。\n我对贵公司的XXX岗位很感兴趣，希望能加入团队。\n```\n\n### 2. 时间控制\n- 简短版：30秒-1分钟\n- 标准版：1-2分钟\n- 详细版：2-3分钟\n\n### 3. 突出亮点\n- 与岗位匹配的技能\n- 独特的项目经验\n- 个人成就和荣誉\n\n### 4. 避免的问题\n- 不要背诵简历\n- 不要说无关信息\n- 不要过于谦虚或自大\n\n### 5. 练习建议\n- 对着镜子练习\n- 录音回放改进\n- 请朋友模拟面试");

        // 技术面试类教程
        saveTutorial("技术面试技巧", "技术面试",
            "## 技术面试技巧\n\n### 1. 听题技巧\n- 认真听完整个问题\n- 不确定时请求澄清\n- 复述问题确认理解\n\n### 2. 答题思路\n- 先说思路，再写代码\n- 从简单解法开始\n- 逐步优化方案\n\n### 3. 编码规范\n- 变量命名有意义\n- 代码结构清晰\n- 添加必要注释\n\n### 4. 边界处理\n- 空值检查\n- 边界条件\n- 异常情况\n\n### 5. 复杂度分析\n- 时间复杂度\n- 空间复杂度\n- 优化空间\n\n### 6. 沟通技巧\n- 边写边解释\n- 主动说明思考过程\n- 虚心接受建议");
        
        saveTutorial("算法面试攻略", "技术面试",
            "## 算法面试攻略\n\n### 1. 必备数据结构\n- 数组和字符串\n- 链表\n- 栈和队列\n- 树和图\n- 哈希表\n\n### 2. 常见算法\n- 排序算法（快排、归并）\n- 二分查找\n- DFS/BFS\n- 动态规划\n- 贪心算法\n\n### 3. 解题步骤\n1. 理解题意，确认输入输出\n2. 想出暴力解法\n3. 优化时间/空间复杂度\n4. 编写代码\n5. 测试用例验证\n\n### 4. 刷题建议\n- LeetCode Hot 100\n- 剑指Offer\n- 按类型专项突破\n\n### 5. 面试技巧\n- 不会也要说思路\n- 主动与面试官沟通\n- 代码写完主动测试");
        
        saveTutorial("系统设计面试指南", "技术面试",
            "## 系统设计面试指南\n\n### 1. 面试流程\n- 需求澄清（5分钟）\n- 高层设计（10分钟）\n- 详细设计（15分钟）\n- 总结优化（5分钟）\n\n### 2. 设计要点\n- 功能需求\n- 非功能需求（性能、可用性）\n- 容量估算\n- API设计\n- 数据模型\n- 架构图\n\n### 3. 常见系统\n- URL短链接服务\n- 消息队列\n- 分布式缓存\n- 秒杀系统\n- 社交网络Feed流\n\n### 4. 核心概念\n- 负载均衡\n- 数据库分片\n- 缓存策略\n- 消息队列\n- 微服务架构\n\n### 5. 回答技巧\n- 先问清楚需求\n- 从简单方案开始\n- 逐步深入细节\n- 讨论trade-off");
        
        saveTutorial("Java面试重点", "技术面试",
            "## Java面试重点\n\n### 1. Java基础\n- 基本数据类型\n- String不可变性\n- ==和equals区别\n- 重载和重写\n\n### 2. 集合框架\n- ArrayList vs LinkedList\n- HashMap原理\n- ConcurrentHashMap\n- 红黑树\n\n### 3. 多线程\n- 线程创建方式\n- synchronized原理\n- volatile关键字\n- 线程池\n\n### 4. JVM\n- 内存模型\n- 垃圾回收算法\n- 类加载机制\n- JVM调优\n\n### 5. Spring\n- IOC和AOP\n- Bean生命周期\n- 事务管理\n- Spring Boot自动配置");
        
        saveTutorial("前端面试要点", "技术面试",
            "## 前端面试要点\n\n### 1. HTML/CSS\n- 语义化标签\n- 盒模型\n- Flex布局\n- 响应式设计\n\n### 2. JavaScript\n- 闭包\n- 原型链\n- 事件循环\n- Promise/async-await\n\n### 3. Vue.js\n- 生命周期\n- 响应式原理\n- 组件通信\n- Vuex状态管理\n\n### 4. 性能优化\n- 懒加载\n- 防抖节流\n- 虚拟列表\n- 代码分割\n\n### 5. 工程化\n- Webpack配置\n- Babel转译\n- ESLint规范\n- CI/CD");

        // 行为面试类教程
        saveTutorial("行为面试应对策略", "行为面试",
            "## 行为面试应对策略\n\n### 1. STAR法则\n- **S**ituation：描述背景情境\n- **T**ask：说明你的任务\n- **A**ction：详述采取的行动\n- **R**esult：展示最终结果\n\n### 2. 常见问题\n- 请举例说明你如何解决冲突\n- 描述一次失败的经历\n- 你如何处理压力\n- 团队合作的例子\n\n### 3. 准备技巧\n- 准备5-8个故事\n- 涵盖不同能力维度\n- 练习简洁表达\n\n### 4. 回答要点\n- 具体而非笼统\n- 突出个人贡献\n- 量化成果\n- 展示成长\n\n### 5. 注意事项\n- 不要编造故事\n- 不要说同事坏话\n- 保持积极态度");
        
        saveTutorial("如何展示领导力", "行为面试",
            "## 如何展示领导力\n\n### 1. 领导力维度\n- 目标设定\n- 团队激励\n- 决策能力\n- 冲突解决\n- 结果导向\n\n### 2. 故事准备\n- 带领团队完成项目\n- 推动变革或改进\n- 指导新人成长\n- 跨部门协调\n\n### 3. 表达技巧\n- 强调你的角色\n- 说明决策过程\n- 展示影响力\n- 量化团队成果\n\n### 4. 常见问题\n- 描述你带领团队的经历\n- 如何激励团队成员\n- 如何处理团队冲突\n- 如何做出困难决定\n\n### 5. 注意事项\n- 不要独揽功劳\n- 承认团队贡献\n- 展示学习能力");
        
        saveTutorial("薪资谈判技巧", "行为面试",
            "## 薪资谈判技巧\n\n### 1. 前期准备\n- 了解市场行情\n- 明确自己底线\n- 准备谈判筹码\n\n### 2. 谈判时机\n- 等对方先出价\n- 拿到offer后再谈\n- 不要在初面谈薪资\n\n### 3. 谈判策略\n- 给出合理范围\n- 强调自身价值\n- 考虑总体package\n\n### 4. 常用话术\n- 根据我的经验和能力...\n- 考虑到市场行情...\n- 我期望的范围是...\n\n### 5. 注意事项\n- 不要撒谎\n- 保持专业态度\n- 留有余地\n- 书面确认");
        
        log.info("教程初始化完成，共 {} 条", tutorialRepository.count());
    }
    
    private void saveTutorial(String title, String category, String content) {
        Tutorial t = new Tutorial();
        t.setTitle(title);
        t.setCategory(category);
        t.setContent(content);
        t.setCreatedBy(2L);
        tutorialRepository.save(t);
    }

    private void initQuestions() {
        log.info("初始化题目数据...");
        
        // ========== Java题目 ==========
        saveChoiceQuestion("Java", "基础知识", Question.Difficulty.EASY,
            "String数据类型", "Java中，String是基本数据类型吗？",
            "是", "否", "取决于使用场景", "JDK版本不同结果不同", "B",
            "String不是基本数据类型，而是引用类型。Java的8种基本数据类型是：byte、short、int、long、float、double、char、boolean。");
        
        saveChoiceQuestion("Java", "集合框架", Question.Difficulty.MEDIUM,
            "线程安全的集合类", "以下哪个集合类是线程安全的？",
            "ArrayList", "HashMap", "Vector", "LinkedList", "C",
            "Vector是线程安全的，其方法都使用synchronized修饰。ArrayList、HashMap、LinkedList都不是线程安全的。");
        
        saveChoiceQuestion("Java", "集合框架", Question.Difficulty.EASY,
            "ArrayList和LinkedList区别", "ArrayList和LinkedList的主要区别是什么？",
            "ArrayList基于数组，查询快；LinkedList基于链表，增删快", 
            "ArrayList基于链表，LinkedList基于数组", 
            "两者性能完全相同", 
            "ArrayList线程安全，LinkedList线程不安全", "A",
            "ArrayList底层是动态数组，支持随机访问O(1)，增删O(n)。LinkedList是双向链表，增删O(1)，查询O(n)。");
        
        saveChoiceQuestion("Java", "JVM", Question.Difficulty.MEDIUM,
            "JVM内存模型", "Java虚拟机的内存区域包括哪些？",
            "堆、栈、方法区", "堆、栈、方法区、程序计数器、本地方法栈", 
            "新生代、老年代、永久代", "Eden、Survivor、Old", "B",
            "JVM内存分为：堆、虚拟机栈、本地方法栈、方法区、程序计数器五大区域。");
        
        saveChoiceQuestion("Java", "多线程", Question.Difficulty.MEDIUM,
            "创建线程的方式", "Java中创建线程有几种方式？",
            "1种", "2种", "3种", "4种", "D",
            "创建线程的方式：1.继承Thread类 2.实现Runnable接口 3.实现Callable接口 4.使用线程池");
        
        saveChoiceQuestion("Java", "多线程", Question.Difficulty.HARD,
            "synchronized原理", "synchronized关键字的底层实现原理是什么？",
            "使用Lock接口", "使用Monitor监视器锁", "使用volatile", "使用CAS", "B",
            "synchronized是通过Monitor监视器锁实现的，每个对象都有一个Monitor，线程进入同步代码块时获取Monitor锁。");
        
        saveChoiceQuestion("Java", "Spring", Question.Difficulty.MEDIUM,
            "Spring IOC", "Spring的IOC容器的作用是什么？",
            "管理对象的创建和依赖关系", "提供Web服务", "数据库连接", "日志管理", "A",
            "IOC(控制反转)容器负责创建对象、管理对象的生命周期和依赖注入。");
        
        saveChoiceQuestion("Java", "Spring", Question.Difficulty.MEDIUM,
            "Spring AOP", "Spring AOP的实现方式有哪些？",
            "只有JDK动态代理", "只有CGLIB代理", "JDK动态代理和CGLIB代理", "静态代理", "C",
            "Spring AOP默认使用JDK动态代理（基于接口），如果目标类没有实现接口则使用CGLIB代理（基于继承）。");
        
        saveChoiceQuestion("Java", "基础知识", Question.Difficulty.EASY,
            "final关键字", "final关键字可以修饰什么？",
            "只能修饰类", "只能修饰方法", "只能修饰变量", "类、方法、变量都可以", "D",
            "final可以修饰类（不能被继承）、方法（不能被重写）、变量（不能被修改）。");
        
        saveChoiceQuestion("Java", "基础知识", Question.Difficulty.MEDIUM,
            "equals和==的区别", "equals()和==的区别是什么？",
            "完全相同", "==比较值，equals比较引用", "==比较引用，equals比较内容", "没有区别", "C",
            "==比较的是引用地址，equals()默认也是比较地址，但String等类重写了equals()方法来比较内容。");

        // ========== 前端题目 ==========
        saveChoiceQuestion("前端", "Vue.js", Question.Difficulty.EASY,
            "Vue指令对比", "在Vue中，v-if和v-show的区别是什么？",
            "没有区别", "v-if是条件渲染，v-show是通过CSS控制显示隐藏", 
            "v-show性能更差", "v-if只能用于组件", "B",
            "v-if是真正的条件渲染，会销毁和重建DOM；v-show只是通过CSS的display属性控制显示隐藏。");
        
        saveChoiceQuestion("前端", "Vue.js", Question.Difficulty.MEDIUM,
            "Vue生命周期", "Vue组件中，数据请求应该放在哪个生命周期？",
            "beforeCreate", "created", "mounted", "beforeMount", "C",
            "通常在mounted中进行数据请求，此时DOM已经渲染完成。如果不需要操作DOM，也可以在created中请求。");
        
        saveChoiceQuestion("前端", "JavaScript", Question.Difficulty.MEDIUM,
            "闭包的概念", "什么是JavaScript闭包？",
            "一种数据类型", "函数和其词法环境的组合", "一种设计模式", "异步编程方式", "B",
            "闭包是指函数能够访问其词法作用域中的变量，即使函数在其词法作用域之外执行。");
        
        saveChoiceQuestion("前端", "JavaScript", Question.Difficulty.EASY,
            "var和let的区别", "var和let声明变量的主要区别是什么？",
            "没有区别", "let有块级作用域，var没有", "var有块级作用域，let没有", "let不能重复声明", "B",
            "let有块级作用域，不存在变量提升，不能重复声明；var是函数作用域，存在变量提升。");
        
        saveChoiceQuestion("前端", "CSS", Question.Difficulty.EASY,
            "盒模型", "CSS盒模型包括哪些部分？",
            "只有content", "content和padding", "content、padding、border", "content、padding、border、margin", "D",
            "CSS盒模型由内到外依次是：content（内容）、padding（内边距）、border（边框）、margin（外边距）。");
        
        saveChoiceQuestion("前端", "HTML", Question.Difficulty.EASY,
            "语义化标签", "以下哪个是HTML5新增的语义化标签？",
            "div", "span", "header", "table", "C",
            "HTML5新增的语义化标签包括：header、footer、nav、article、section、aside等。");

        // ========== 数据库题目 ==========
        saveChoiceQuestion("数据库", "MySQL", Question.Difficulty.MEDIUM,
            "数据库索引类型", "数据库中，以下哪种索引查询效率最高？",
            "主键索引", "唯一索引", "普通索引", "全文索引", "A",
            "主键索引是聚簇索引，数据和索引在一起，查询效率最高。");
        
        saveChoiceQuestion("数据库", "MySQL", Question.Difficulty.MEDIUM,
            "事务隔离级别", "MySQL默认的事务隔离级别是什么？",
            "读未提交", "读已提交", "可重复读", "串行化", "C",
            "MySQL InnoDB默认的隔离级别是可重复读(Repeatable Read)。");
        
        saveChoiceQuestion("数据库", "MySQL", Question.Difficulty.HARD,
            "索引失效场景", "以下哪种情况会导致索引失效？",
            "使用主键查询", "使用等值查询", "在索引列上使用函数", "使用覆盖索引", "C",
            "在索引列上使用函数、类型转换、or连接、like以%开头等都会导致索引失效。");
        
        saveChoiceQuestion("数据库", "Redis", Question.Difficulty.MEDIUM,
            "Redis数据类型", "Redis支持的数据类型不包括以下哪个？",
            "String", "List", "Table", "Set", "C",
            "Redis支持String、List、Set、Hash、Sorted Set五种基本数据类型，不支持Table。");
        
        saveChoiceQuestion("数据库", "Redis", Question.Difficulty.MEDIUM,
            "Redis持久化", "Redis的RDB和AOF持久化方式的区别是什么？",
            "RDB是快照，AOF是日志追加", "RDB是日志，AOF是快照", "两者完全相同", "Redis不支持持久化", "A",
            "RDB是某一时刻的数据快照，AOF是记录每次写操作的日志。");

        // ========== 算法题目 ==========
        saveChoiceQuestion("算法", "排序算法", Question.Difficulty.EASY,
            "快速排序复杂度", "快速排序的平均时间复杂度是？",
            "O(n)", "O(n log n)", "O(n²)", "O(log n)", "B",
            "快速排序的平均时间复杂度为O(n log n)，最坏情况下为O(n²)。");
        
        saveChoiceQuestion("算法", "排序算法", Question.Difficulty.MEDIUM,
            "稳定排序", "以下哪个排序算法是稳定的？",
            "快速排序", "堆排序", "归并排序", "选择排序", "C",
            "稳定排序：冒泡、插入、归并。不稳定排序：选择、快速、堆排序。");
        
        saveChoiceQuestion("算法", "数据结构", Question.Difficulty.EASY,
            "栈的特点", "栈的特点是什么？",
            "先进先出", "后进先出", "随机访问", "双端操作", "B",
            "栈是后进先出(LIFO)的数据结构，只能在栈顶进行插入和删除操作。");
        
        saveChoiceQuestion("算法", "数据结构", Question.Difficulty.MEDIUM,
            "二叉搜索树", "二叉搜索树的中序遍历结果是什么？",
            "随机顺序", "递增顺序", "递减顺序", "层序顺序", "B",
            "二叉搜索树的中序遍历结果是递增有序的。");
        
        saveChoiceQuestion("算法", "数据结构", Question.Difficulty.MEDIUM,
            "哈希表冲突", "哈希表解决冲突的方法不包括？",
            "链地址法", "开放地址法", "再哈希法", "二分查找法", "D",
            "哈希冲突解决方法：链地址法、开放地址法、再哈希法、建立公共溢出区。");

        // ========== 计算机网络题目 ==========
        saveChoiceQuestion("计算机网络", "HTTP", Question.Difficulty.EASY,
            "HTTP状态码", "HTTP状态码200表示什么？",
            "请求成功", "重定向", "客户端错误", "服务器错误", "A",
            "2xx成功，3xx重定向，4xx客户端错误，5xx服务器错误。200表示请求成功。");
        
        saveChoiceQuestion("计算机网络", "HTTP", Question.Difficulty.MEDIUM,
            "GET和POST区别", "GET和POST请求的主要区别是什么？",
            "GET有请求体，POST没有", "GET参数在URL中，POST在请求体中", 
            "GET更安全", "没有区别", "B",
            "GET参数在URL中，有长度限制；POST参数在请求体中，相对更安全。");
        
        saveChoiceQuestion("计算机网络", "TCP", Question.Difficulty.MEDIUM,
            "TCP三次握手", "TCP建立连接需要几次握手？",
            "1次", "2次", "3次", "4次", "C",
            "TCP建立连接需要三次握手：SYN、SYN+ACK、ACK。");
        
        saveChoiceQuestion("计算机网络", "TCP", Question.Difficulty.MEDIUM,
            "TCP四次挥手", "TCP断开连接需要几次挥手？",
            "2次", "3次", "4次", "5次", "C",
            "TCP断开连接需要四次挥手：FIN、ACK、FIN、ACK。");

        // ========== 系统设计题目 ==========
        saveChoiceQuestion("系统设计", "高并发", Question.Difficulty.HARD,
            "秒杀系统设计", "秒杀系统中，防止超卖的最佳方案是？",
            "数据库悲观锁", "Redis预减库存+消息队列", "单线程处理", "不做处理", "B",
            "使用Redis预减库存可以减少数据库压力，配合消息队列异步处理订单，防止超卖。");
        
        saveChoiceQuestion("系统设计", "分布式", Question.Difficulty.HARD,
            "分布式锁实现", "以下哪种方式不能实现分布式锁？",
            "Redis SETNX", "Zookeeper临时节点", "数据库唯一索引", "本地synchronized", "D",
            "synchronized只能在单机环境下使用，无法实现分布式锁。");
        
        log.info("题目初始化完成，共 {} 道", questionRepository.count());
    }
    
    private void saveChoiceQuestion(String category, String subCategory, Question.Difficulty difficulty,
            String title, String content, String optionA, String optionB, String optionC, String optionD,
            String correctAnswer, String explanation) {
        Question q = new Question();
        q.setTitle(title);
        q.setContent(content);
        q.setType(Question.QuestionType.CHOICE);
        q.setCategory(category);
        q.setSubCategory(subCategory);
        q.setDifficulty(difficulty);
        q.setOptionA(optionA);
        q.setOptionB(optionB);
        q.setOptionC(optionC);
        q.setOptionD(optionD);
        q.setCorrectAnswer(correctAnswer);
        q.setExplanation(explanation);
        q.setCreatedBy(2L);
        questionRepository.save(q);
    }

    private void initKnowledgePoints() {
        log.info("初始化知识点数据...");
        
        // Java知识点
        saveKnowledgePoint("Java基础", "Java基本数据类型", KnowledgePoint.Difficulty.EASY, 5,
            "## Java基本数据类型\n\nJava有8种基本数据类型：\n\n### 整数类型\n- `byte`: 8位，范围-128到127\n- `short`: 16位\n- `int`: 32位（最常用）\n- `long`: 64位\n\n### 浮点类型\n- `float`: 32位单精度\n- `double`: 64位双精度\n\n### 其他\n- `char`: 16位Unicode字符\n- `boolean`: true或false\n\n### 注意\nString不是基本类型，是引用类型！");
        
        saveKnowledgePoint("Java基础", "面向对象三大特性", KnowledgePoint.Difficulty.MEDIUM, 5,
            "## 面向对象三大特性\n\n### 1. 封装\n将数据和方法绑定，隐藏内部实现。\n\n### 2. 继承\n子类继承父类的属性和方法。\n- 关键字：extends\n- Java单继承\n\n### 3. 多态\n同一引用类型表现不同行为。\n- 方法重载\n- 方法重写\n- 接口实现");
        
        saveKnowledgePoint("Java集合", "ArrayList vs LinkedList", KnowledgePoint.Difficulty.MEDIUM, 4,
            "## ArrayList vs LinkedList\n\n### ArrayList\n- 底层：动态数组\n- 查询：O(1)\n- 增删：O(n)\n\n### LinkedList\n- 底层：双向链表\n- 查询：O(n)\n- 增删：O(1)\n\n### 选择\n- 查询多：ArrayList\n- 增删多：LinkedList");
        
        saveKnowledgePoint("Java集合", "HashMap原理", KnowledgePoint.Difficulty.HARD, 5,
            "## HashMap原理\n\n### 数据结构\nJDK8: 数组 + 链表 + 红黑树\n\n### 关键参数\n- 初始容量：16\n- 负载因子：0.75\n- 树化阈值：8\n\n### put流程\n1. 计算hash\n2. 找到桶位置\n3. 处理冲突\n4. 判断扩容\n\n### 扩容\n容量翻倍，重新hash");
        
        saveKnowledgePoint("JVM", "JVM内存模型", KnowledgePoint.Difficulty.MEDIUM, 5,
            "## JVM内存模型\n\n### 线程私有\n- 程序计数器\n- 虚拟机栈\n- 本地方法栈\n\n### 线程共享\n- 堆（对象实例）\n- 方法区（类信息、常量）\n\n### 堆内存分代\n- 新生代：Eden + S0 + S1\n- 老年代");
        
        saveKnowledgePoint("JVM", "垃圾回收算法", KnowledgePoint.Difficulty.HARD, 4,
            "## 垃圾回收算法\n\n### 1. 标记-清除\n标记垃圾对象，统一清除\n缺点：内存碎片\n\n### 2. 复制算法\n将存活对象复制到另一块\n缺点：空间浪费\n\n### 3. 标记-整理\n标记后整理存活对象\n适合老年代\n\n### 4. 分代收集\n新生代：复制算法\n老年代：标记-整理");
        
        saveKnowledgePoint("多线程", "线程安全", KnowledgePoint.Difficulty.MEDIUM, 5,
            "## 线程安全\n\n### 实现方式\n1. synchronized关键字\n2. Lock接口\n3. volatile关键字\n4. 原子类\n5. ThreadLocal\n\n### synchronized\n- 修饰方法\n- 修饰代码块\n- 底层：Monitor锁\n\n### volatile\n- 保证可见性\n- 禁止重排序\n- 不保证原子性");
        
        // 数据库知识点
        saveKnowledgePoint("数据库", "MySQL索引", KnowledgePoint.Difficulty.MEDIUM, 5,
            "## MySQL索引\n\n### 索引类型\n- B+树索引（最常用）\n- 哈希索引\n- 全文索引\n\n### B+树特点\n- 所有数据在叶子节点\n- 叶子节点有指针连接\n- 适合范围查询\n\n### 索引优化\n1. 选择区分度高的列\n2. 遵循最左前缀\n3. 避免索引失效");
        
        saveKnowledgePoint("数据库", "事务ACID", KnowledgePoint.Difficulty.MEDIUM, 5,
            "## 事务ACID\n\n### A - 原子性\n事务不可分割，全部成功或全部失败\n\n### C - 一致性\n事务前后数据保持一致\n\n### I - 隔离性\n多个事务互不干扰\n\n### D - 持久性\n事务提交后永久保存\n\n### 隔离级别\n1. 读未提交\n2. 读已提交\n3. 可重复读（MySQL默认）\n4. 串行化");
        
        saveKnowledgePoint("数据库", "Redis基础", KnowledgePoint.Difficulty.MEDIUM, 4,
            "## Redis基础\n\n### 数据类型\n- String：字符串\n- List：列表\n- Set：集合\n- Hash：哈希\n- Sorted Set：有序集合\n\n### 持久化\n- RDB：快照\n- AOF：日志追加\n\n### 应用场景\n- 缓存\n- 分布式锁\n- 计数器\n- 排行榜");
        
        // 算法知识点
        saveKnowledgePoint("算法", "时间复杂度", KnowledgePoint.Difficulty.EASY, 5,
            "## 时间复杂度\n\n### 常见复杂度\n从快到慢：\n1. O(1) - 常数\n2. O(log n) - 对数\n3. O(n) - 线性\n4. O(n log n) - 线性对数\n5. O(n²) - 平方\n6. O(2ⁿ) - 指数\n\n### 计算规则\n- 只看最高次项\n- 去掉系数\n- 嵌套循环相乘");
        
        saveKnowledgePoint("算法", "排序算法", KnowledgePoint.Difficulty.MEDIUM, 5,
            "## 排序算法\n\n### 比较类排序\n- 冒泡排序：O(n²)\n- 选择排序：O(n²)\n- 插入排序：O(n²)\n- 快速排序：O(n log n)\n- 归并排序：O(n log n)\n- 堆排序：O(n log n)\n\n### 稳定性\n稳定：冒泡、插入、归并\n不稳定：选择、快速、堆");
        
        saveKnowledgePoint("算法", "二叉树", KnowledgePoint.Difficulty.MEDIUM, 4,
            "## 二叉树\n\n### 遍历方式\n- 前序：根-左-右\n- 中序：左-根-右\n- 后序：左-右-根\n- 层序：BFS\n\n### 特殊二叉树\n- 满二叉树\n- 完全二叉树\n- 二叉搜索树\n- 平衡二叉树\n- 红黑树");
        
        // 计算机网络知识点
        saveKnowledgePoint("计算机网络", "HTTP协议", KnowledgePoint.Difficulty.MEDIUM, 4,
            "## HTTP协议\n\n### 请求方法\n- GET：获取资源\n- POST：提交数据\n- PUT：更新资源\n- DELETE：删除资源\n\n### 状态码\n- 2xx：成功\n- 3xx：重定向\n- 4xx：客户端错误\n- 5xx：服务器错误\n\n### HTTP vs HTTPS\nHTTPS = HTTP + SSL/TLS加密");
        
        saveKnowledgePoint("计算机网络", "TCP协议", KnowledgePoint.Difficulty.MEDIUM, 4,
            "## TCP协议\n\n### 特点\n- 面向连接\n- 可靠传输\n- 流量控制\n- 拥塞控制\n\n### 三次握手\n1. SYN\n2. SYN+ACK\n3. ACK\n\n### 四次挥手\n1. FIN\n2. ACK\n3. FIN\n4. ACK");
        
        log.info("知识点初始化完成，共 {} 个", knowledgePointRepository.count());
    }
    
    private void saveKnowledgePoint(String category, String title, KnowledgePoint.Difficulty difficulty, 
            int importance, String content) {
        KnowledgePoint kp = new KnowledgePoint();
        kp.setTitle(title);
        kp.setCategory(category);
        kp.setCategoryId(getCategoryId(category));
        kp.setContent(content);
        kp.setDifficulty(difficulty);
        kp.setImportance(importance);
        kp.setCreatedBy(2L);
        knowledgePointRepository.save(kp);
    }
    
    private Long getCategoryId(String category) {
        switch (category) {
            case "Java基础":
            case "Java集合":
            case "JVM":
            case "多线程":
                return 1L;
            case "算法":
            case "数据结构":
                return 2L;
            case "数据库":
                return 3L;
            case "计算机网络":
                return 4L;
            default:
                return 1L;
        }
    }
}
