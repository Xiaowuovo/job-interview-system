package com.interview.config;

import com.interview.entity.*;
import com.interview.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TutorialRepository tutorialRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserAbilityModelRepository abilityModelRepository;

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Override
    public void run(String... args) {
        // 初始化用户
        if (userRepository.count() == 0) {
            User student = new User();
            student.setUsername("student");
            student.setPassword("123456");
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
            teacher.setPassword("123456");
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
            Tutorial t1 = new Tutorial();
            t1.setTitle("面试前的准备工作");
            t1.setContent("1. 了解公司背景和企业文化\n2. 准备自我介绍\n3. 整理项目经验\n4. 准备常见问题的回答\n5. 着装得体，提前到达");
            t1.setCategory("基础指导");
            t1.setCreatedBy(2L);
            tutorialRepository.save(t1);

            Tutorial t2 = new Tutorial();
            t2.setTitle("技术面试技巧");
            t2.setContent("1. 认真听题，不要急于回答\n2. 思路清晰，先说思路再写代码\n3. 考虑边界条件和异常情况\n4. 代码规范，注意命名\n5. 时间复杂度和空间复杂度分析");
            t2.setCategory("技术面试");
            t2.setCreatedBy(2L);
            tutorialRepository.save(t2);

            Tutorial t3 = new Tutorial();
            t3.setTitle("行为面试应对策略");
            t3.setContent("使用STAR法则：\nS - Situation（情境）\nT - Task（任务）\nA - Action（行动）\nR - Result（结果）\n\n用具体事例展示你的能力和成就。");
            t3.setCategory("行为面试");
            t3.setCreatedBy(2L);
            tutorialRepository.save(t3);
        }

        // 初始化题目
        if (questionRepository.count() == 0) {
            Question q1 = new Question();
            q1.setTitle("String数据类型");
            q1.setContent("Java中，String是基本数据类型吗？");
            q1.setType(Question.QuestionType.CHOICE);
            q1.setCategory("Java");
            q1.setSubCategory("基础知识");
            q1.setDifficulty(Question.Difficulty.EASY);
            q1.setTags("Java基础,数据类型");
            q1.setOptionA("是");
            q1.setOptionB("否");
            q1.setOptionC("取决于使用场景");
            q1.setOptionD("JDK版本不同结果不同");
            q1.setCorrectAnswer("B");
            q1.setExplanation("String不是基本数据类型，而是引用类型。Java的8种基本数据类型是：byte、short、int、long、float、double、char、boolean。");
            q1.setKeyPoints("Java基础,数据类型");
            q1.setCreatedBy(2L);
            questionRepository.save(q1);

            Question q2 = new Question();
            q2.setTitle("线程安全的集合类");
            q2.setContent("以下哪个集合类是线程安全的？");
            q2.setType(Question.QuestionType.CHOICE);
            q2.setCategory("Java");
            q2.setSubCategory("集合框架");
            q2.setDifficulty(Question.Difficulty.MEDIUM);
            q2.setTags("Java集合,多线程");
            q2.setOptionA("ArrayList");
            q2.setOptionB("HashMap");
            q2.setOptionC("Vector");
            q2.setOptionD("LinkedList");
            q2.setCorrectAnswer("C");
            q2.setExplanation("Vector是线程安全的，其方法都使用synchronized修饰。ArrayList、HashMap、LinkedList都不是线程安全的。");
            q2.setReferenceAnswer("在实际开发中，应优先使用ConcurrentHashMap、CopyOnWriteArrayList等并发容器，而不是Vector。");
            q2.setKeyPoints("集合框架,线程安全");
            q2.setCreatedBy(2L);
            questionRepository.save(q2);

            Question q3 = new Question();
            q3.setTitle("Vue指令对比");
            q3.setContent("在Vue中，v-if和v-show的区别是什么？");
            q3.setType(Question.QuestionType.CHOICE);
            q3.setCategory("前端");
            q3.setSubCategory("Vue.js");
            q3.setDifficulty(Question.Difficulty.EASY);
            q3.setTags("Vue,指令");
            q3.setOptionA("没有区别");
            q3.setOptionB("v-if是条件渲染，v-show是通过CSS控制显示隐藏");
            q3.setOptionC("v-show性能更差");
            q3.setOptionD("v-if只能用于组件");
            q3.setCorrectAnswer("B");
            q3.setExplanation("v-if是真正的条件渲染，会销毁和重建DOM；v-show只是通过CSS的display属性控制显示隐藏，DOM元素始终存在。频繁切换使用v-show，否则使用v-if。");
            q3.setKeyPoints("Vue指令,性能优化");
            q3.setCreatedBy(2L);
            questionRepository.save(q3);

            Question q4 = new Question();
            q4.setTitle("数据库索引类型");
            q4.setContent("数据库中，以下哪种索引查询效率最高？");
            q4.setType(Question.QuestionType.CHOICE);
            q4.setCategory("数据库");
            q4.setSubCategory("MySQL");
            q4.setDifficulty(Question.Difficulty.MEDIUM);
            q4.setTags("MySQL,索引优化");
            q4.setOptionA("主键索引");
            q4.setOptionB("唯一索引");
            q4.setOptionC("普通索引");
            q4.setOptionD("全文索引");
            q4.setCorrectAnswer("A");
            q4.setExplanation("主键索引是聚簇索引，数据和索引在一起，查询效率最高。唯一索引和普通索引都是非聚簇索引，需要回表查询。");
            q4.setKeyPoints("索引类型,查询优化");
            q4.setCreatedBy(2L);
            questionRepository.save(q4);

            Question q5 = new Question();
            q5.setTitle("快速排序复杂度");
            q5.setContent("快速排序的平均时间复杂度是？");
            q5.setType(Question.QuestionType.CHOICE);
            q5.setCategory("算法");
            q5.setSubCategory("排序算法");
            q5.setDifficulty(Question.Difficulty.EASY);
            q5.setTags("排序,时间复杂度");
            q5.setOptionA("O(n)");
            q5.setOptionB("O(n log n)");
            q5.setOptionC("O(n²)");
            q5.setOptionD("O(log n)");
            q5.setCorrectAnswer("B");
            q5.setExplanation("快速排序的平均时间复杂度为O(n log n)，最坏情况下为O(n²)，空间复杂度为O(log n)。");
            q5.setReferenceAnswer("快速排序的核心思想是分治法，通过一趟排序将待排序记录分隔成独立的两部分，其中一部分记录的关键字均比另一部分的关键字小，再分别对这两部分记录继续进行排序。");
            q5.setKeyPoints("排序算法,算法复杂度");
            q5.setCreatedBy(2L);
            questionRepository.save(q5);

            // 添加更多题目
            Question q6 = new Question();
            q6.setTitle("Spring Boot自动配置");
            q6.setContent("Spring Boot如何实现自动配置？");
            q6.setType(Question.QuestionType.SHORT_ANSWER);
            q6.setCategory("Java");
            q6.setSubCategory("Spring Boot");
            q6.setDifficulty(Question.Difficulty.HARD);
            q6.setTags("Spring Boot,自动配置");
            q6.setExplanation("Spring Boot通过@EnableAutoConfiguration注解实现自动配置。它会扫描classpath下的META-INF/spring.factories文件，加载其中配置的自动配置类。");
            q6.setReferenceAnswer("主要流程：\n1. @SpringBootApplication包含@EnableAutoConfiguration\n2. @EnableAutoConfiguration导入AutoConfigurationImportSelector\n3. AutoConfigurationImportSelector从spring.factories读取配置类\n4. 根据@Conditional注解判断是否加载\n5. 自动配置类生效，完成Bean的自动装配");
            q6.setKeyPoints("Spring Boot,自动配置原理");
            q6.setCreatedBy(2L);
            questionRepository.save(q6);

            Question q7 = new Question();
            q7.setTitle("LRU缓存实现");
            q7.setContent("请用Java实现一个LRU(Least Recently Used)缓存");
            q7.setType(Question.QuestionType.CODING);
            q7.setCategory("算法");
            q7.setSubCategory("数据结构");
            q7.setDifficulty(Question.Difficulty.MEDIUM);
            q7.setTags("算法,LRU,缓存");
            q7.setExplanation("LRU缓存需要支持O(1)时间复杂度的get和put操作，可以使用HashMap + 双向链表实现。");
            q7.setReferenceAnswer("```java\nclass LRUCache {\n    private Map<Integer, Node> map;\n    private DoubleLinkedList cache;\n    private int capacity;\n    \n    public LRUCache(int capacity) {\n        this.capacity = capacity;\n        map = new HashMap<>();\n        cache = new DoubleLinkedList();\n    }\n    \n    public int get(int key) {\n        if (!map.containsKey(key)) return -1;\n        makeRecently(key);\n        return map.get(key).val;\n    }\n    \n    public void put(int key, int val) {\n        if (map.containsKey(key)) {\n            deleteKey(key);\n            addRecently(key, val);\n            return;\n        }\n        if (capacity == cache.size()) {\n            removeLeastRecently();\n        }\n        addRecently(key, val);\n    }\n}\n```");
            q7.setKeyPoints("LRU算法,HashMap,双向链表");
            q7.setCreatedBy(2L);
            questionRepository.save(q7);

            Question q8 = new Question();
            q8.setTitle("秒杀系统设计");
            q8.setContent("请设计一个能够支持10万QPS的秒杀系统");
            q8.setType(Question.QuestionType.SYSTEM_DESIGN);
            q8.setCategory("系统设计");
            q8.setSubCategory("高并发");
            q8.setDifficulty(Question.Difficulty.HARD);
            q8.setTags("系统设计,高并发,秒杀");
            q8.setExplanation("秒杀系统需要解决高并发读写、超卖、恶意请求等问题。");
            q8.setReferenceAnswer("核心方案：\n1. 前端：页面静态化，按钮控制\n2. 网关：限流、黑名单\n3. 缓存：Redis预减库存\n4. 队列：消息队列异步处理\n5. 数据库：乐观锁防止超卖\n6. 监控：实时监控系统状态");
            q8.setKeyPoints("系统设计,高并发,缓存,消息队列");
            q8.setCreatedBy(2L);
            questionRepository.save(q8);

            // 继续添加更多题目
            Question q9 = new Question();
            q9.setTitle("HTTP和HTTPS的区别");
            q9.setContent("请说明HTTP和HTTPS的主要区别");
            q9.setType(Question.QuestionType.SHORT_ANSWER);
            q9.setCategory("计算机网络");
            q9.setSubCategory("HTTP");
            q9.setDifficulty(Question.Difficulty.EASY);
            q9.setTags("HTTP,HTTPS,网络协议");
            q9.setExplanation("HTTPS是在HTTP基础上加入了SSL/TLS加密层，提供了安全的通信方式。");
            q9.setReferenceAnswer("主要区别：\n1. 安全性：HTTPS使用SSL/TLS加密，HTTP是明文传输\n2. 端口：HTTP使用80端口，HTTPS使用443端口\n3. 证书：HTTPS需要CA证书，有一定费用\n4. 性能：HTTPS握手过程复杂，速度稍慢\n5. SEO：搜索引擎更青睐HTTPS网站");
            q9.setKeyPoints("HTTP,HTTPS,SSL,加密");
            q9.setCreatedBy(2L);
            questionRepository.save(q9);

            Question q10 = new Question();
            q10.setTitle("Redis持久化方式");
            q10.setContent("Redis有哪几种持久化方式？它们的优缺点是什么？");
            q10.setType(Question.QuestionType.SHORT_ANSWER);
            q10.setCategory("数据库");
            q10.setSubCategory("Redis");
            q10.setDifficulty(Question.Difficulty.MEDIUM);
            q10.setTags("Redis,持久化,RDB,AOF");
            q10.setExplanation("Redis提供RDB和AOF两种持久化方式，以及混合持久化。");
            q10.setReferenceAnswer("两种方式：\n\n**1. RDB (Redis Database)**\n- 优点：文件小，恢复快，性能好\n- 缺点：可能丢失最后一次快照后的数据\n\n**2. AOF (Append Only File)**\n- 优点：数据更完整，可读性好\n- 缺点：文件大，恢复慢\n\n**3. 混合持久化（4.0+）**\n结合RDB和AOF的优点，先RDB后AOF");
            q10.setKeyPoints("Redis,RDB,AOF,持久化");
            q10.setCreatedBy(2L);
            questionRepository.save(q10);

            Question q11 = new Question();
            q11.setTitle("JVM内存模型");
            q11.setContent("Java虚拟机的内存区域包括哪些？");
            q11.setType(Question.QuestionType.CHOICE);
            q11.setCategory("Java");
            q11.setSubCategory("JVM");
            q11.setDifficulty(Question.Difficulty.MEDIUM);
            q11.setTags("JVM,内存模型");
            q11.setOptionA("堆、栈、方法区");
            q11.setOptionB("堆、栈、方法区、程序计数器、本地方法栈");
            q11.setOptionC("新生代、老年代、永久代");
            q11.setOptionD("Eden、Survivor、Old");
            q11.setCorrectAnswer("B");
            q11.setExplanation("JVM内存分为：堆、虚拟机栈、本地方法栈、方法区、程序计数器五大区域。选项C和D是堆内存的细分。");
            q11.setKeyPoints("JVM,内存模型,堆栈");
            q11.setCreatedBy(2L);
            questionRepository.save(q11);

            Question q12 = new Question();
            q12.setTitle("数据库事务ACID");
            q12.setContent("数据库事务的ACID特性是什么？");
            q12.setType(Question.QuestionType.SHORT_ANSWER);
            q12.setCategory("数据库");
            q12.setSubCategory("事务");
            q12.setDifficulty(Question.Difficulty.EASY);
            q12.setTags("数据库,事务,ACID");
            q12.setExplanation("ACID是数据库事务的四大特性，确保数据的可靠性。");
            q12.setReferenceAnswer("**A - 原子性(Atomicity)**\n事务是不可分割的最小单位，要么全部成功，要么全部失败\n\n**C - 一致性(Consistency)**\n事务执行前后，数据库从一个一致性状态到另一个一致性状态\n\n**I - 隔离性(Isolation)**\n多个事务并发执行时，互不干扰\n\n**D - 持久性(Durability)**\n事务一旦提交，对数据库的改变是永久的");
            q12.setKeyPoints("数据库,事务,ACID");
            q12.setCreatedBy(2L);
            questionRepository.save(q12);

            Question q13 = new Question();
            q13.setTitle("二叉树遍历");
            q13.setContent("请写出二叉树的前序、中序、后序遍历代码");
            q13.setType(Question.QuestionType.CODING);
            q13.setCategory("算法");
            q13.setSubCategory("树");
            q13.setDifficulty(Question.Difficulty.MEDIUM);
            q13.setTags("算法,二叉树,遍历");
            q13.setExplanation("二叉树遍历有递归和迭代两种方式实现。");
            q13.setReferenceAnswer("```java\n// 前序遍历（根-左-右）\nvoid preorder(TreeNode root) {\n    if (root == null) return;\n    System.out.print(root.val + \" \");\n    preorder(root.left);\n    preorder(root.right);\n}\n\n// 中序遍历（左-根-右）\nvoid inorder(TreeNode root) {\n    if (root == null) return;\n    inorder(root.left);\n    System.out.print(root.val + \" \");\n    inorder(root.right);\n}\n\n// 后序遍历（左-右-根）\nvoid postorder(TreeNode root) {\n    if (root == null) return;\n    postorder(root.left);\n    postorder(root.right);\n    System.out.print(root.val + \" \");\n}\n```");
            q13.setKeyPoints("二叉树,遍历,递归");
            q13.setCreatedBy(2L);
            questionRepository.save(q13);

            Question q14 = new Question();
            q14.setTitle("Vue生命周期");
            q14.setContent("Vue组件的生命周期钩子有哪些？");
            q14.setType(Question.QuestionType.SHORT_ANSWER);
            q14.setCategory("前端");
            q14.setSubCategory("Vue.js");
            q14.setDifficulty(Question.Difficulty.EASY);
            q14.setTags("Vue,生命周期");
            q14.setExplanation("Vue组件从创建到销毁的完整过程中，会触发一系列生命周期钩子。");
            q14.setReferenceAnswer("Vue2生命周期钩子：\n\n**创建阶段**\n- beforeCreate：实例初始化后\n- created：实例创建完成\n\n**挂载阶段**\n- beforeMount：挂载开始前\n- mounted：挂载完成\n\n**更新阶段**\n- beforeUpdate：数据更新前\n- updated：数据更新后\n\n**销毁阶段**\n- beforeDestroy：销毁前\n- destroyed：销毁后");
            q14.setKeyPoints("Vue,生命周期,钩子函数");
            q14.setCreatedBy(2L);
            questionRepository.save(q14);

            Question q15 = new Question();
            q15.setTitle("ArrayList和LinkedList区别");
            q15.setContent("ArrayList和LinkedList的区别是什么？分别适用于什么场景？");
            q15.setType(Question.QuestionType.CHOICE);
            q15.setCategory("Java");
            q15.setSubCategory("集合框架");
            q15.setDifficulty(Question.Difficulty.EASY);
            q15.setTags("Java,集合,ArrayList,LinkedList");
            q15.setOptionA("ArrayList基于数组，查询快；LinkedList基于链表，增删快");
            q15.setOptionB("ArrayList基于链表，查询快；LinkedList基于数组，增删快");
            q15.setOptionC("ArrayList和LinkedList性能完全相同");
            q15.setOptionD("ArrayList线程安全，LinkedList线程不安全");
            q15.setCorrectAnswer("A");
            q15.setExplanation("ArrayList底层是动态数组，支持随机访问，查询O(1)，增删O(n)。LinkedList是双向链表，增删O(1)，查询O(n)。");
            q15.setKeyPoints("Java集合,ArrayList,LinkedList");
            q15.setCreatedBy(2L);
            questionRepository.save(q15);
        }

        // 初始化知识点
        if (knowledgePointRepository.count() == 0) {
            // Java基础知识点
            KnowledgePoint kp1 = new KnowledgePoint();
            kp1.setTitle("Java基本数据类型");
            kp1.setContent("## Java基本数据类型\n\nJava有8种基本数据类型：\n\n### 整数类型\n- `byte`: 8位，范围-128到127\n- `short`: 16位，范围-32768到32767\n- `int`: 32位，范围约±21亿\n- `long`: 64位，范围更大\n\n### 浮点类型\n- `float`: 32位单精度\n- `double`: 64位双精度\n\n### 字符和布尔\n- `char`: 16位Unicode字符\n- `boolean`: true或false\n\n### 注意事项\n1. String不是基本数据类型，是引用类型\n2. 基本类型存储在栈中，速度快\n3. 每种基本类型都有对应的包装类");
            kp1.setCategoryId(1L);
            kp1.setCategory("Java基础");
            kp1.setDifficulty(KnowledgePoint.Difficulty.EASY);
            kp1.setImportance(5);
            kp1.setTags("Java,基础,数据类型");
            kp1.setCreatedBy(2L);
            knowledgePointRepository.save(kp1);

            KnowledgePoint kp2 = new KnowledgePoint();
            kp2.setTitle("Java面向对象三大特性");
            kp2.setContent("## 面向对象三大特性\n\n### 1. 封装（Encapsulation）\n将数据和操作数据的方法绑定在一起，隐藏内部实现细节。\n\n**优点：**\n- 提高安全性\n- 隐藏实现细节\n- 提高代码可维护性\n\n### 2. 继承（Inheritance）\n子类继承父类的属性和方法，实现代码复用。\n\n**关键字：** `extends`\n\n### 3. 多态（Polymorphism）\n同一个引用类型，在不同情况下表现出不同的行为。\n\n**实现方式：**\n- 方法重载（Overload）\n- 方法重写（Override）\n- 接口实现");
            kp2.setCategoryId(1L);
            kp2.setCategory("Java基础");
            kp2.setDifficulty(KnowledgePoint.Difficulty.MEDIUM);
            kp2.setImportance(5);
            kp2.setTags("Java,OOP,封装,继承,多态");
            kp2.setCreatedBy(2L);
            knowledgePointRepository.save(kp2);

            KnowledgePoint kp3 = new KnowledgePoint();
            kp3.setTitle("ArrayList vs LinkedList");
            kp3.setContent("## ArrayList vs LinkedList\n\n### ArrayList\n- **底层实现：** 动态数组\n- **随机访问：** O(1)\n- **插入删除：** O(n)（需要移动元素）\n- **内存占用：** 连续内存空间\n\n### LinkedList\n- **底层实现：** 双向链表\n- **随机访问：** O(n)\n- **插入删除：** O(1)（已知位置）\n- **内存占用：** 不连续，额外的指针开销\n\n### 使用场景\n- **ArrayList：** 查询多，增删少\n- **LinkedList：** 增删多，查询少\n\n### 注意事项\n1. ArrayList扩容机制：1.5倍\n2. LinkedList实现了Deque接口，可当队列使用\n3. 频繁增删建议使用LinkedList");
            kp3.setCategoryId(1L);
            kp3.setCategory("Java");
            kp3.setDifficulty(KnowledgePoint.Difficulty.MEDIUM);
            kp3.setImportance(4);
            kp3.setTags("Java,集合,ArrayList,LinkedList");
            kp3.setCreatedBy(2L);
            knowledgePointRepository.save(kp3);

            // 数据结构知识点
            KnowledgePoint kp4 = new KnowledgePoint();
            kp4.setTitle("二叉树遍历");
            kp4.setContent("## 二叉树的四种遍历方式\n\n### 1. 前序遍历（Pre-order）\n顺序：**根 → 左 → 右**\n\n### 2. 中序遍历（In-order）\n顺序：**左 → 根 → 右**\n- 二叉搜索树的中序遍历是有序的\n\n### 3. 后序遍历（Post-order）\n顺序：**左 → 右 → 根**\n\n### 4. 层序遍历（Level-order）\n按层从上到下，从左到右遍历\n- 使用队列实现\n\n### 代码实现\n```java\n// 递归实现中序遍历\nvoid inorder(TreeNode root) {\n    if (root == null) return;\n    inorder(root.left);\n    System.out.println(root.val);\n    inorder(root.right);\n}\n```");
            kp4.setCategoryId(2L);
            kp4.setCategory("数据结构");
            kp4.setDifficulty(KnowledgePoint.Difficulty.MEDIUM);
            kp4.setImportance(5);
            kp4.setTags("数据结构,二叉树,遍历");
            kp4.setCreatedBy(2L);
            knowledgePointRepository.save(kp4);

            KnowledgePoint kp5 = new KnowledgePoint();
            kp5.setTitle("时间复杂度分析");
            kp5.setContent("## 时间复杂度（Time Complexity）\n\n### 常见时间复杂度\n从快到慢：\n1. **O(1)** - 常数时间\n2. **O(log n)** - 对数时间（二分查找）\n3. **O(n)** - 线性时间（遍历）\n4. **O(n log n)** - 线性对数（快排、归并）\n5. **O(n²)** - 平方时间（冒泡排序）\n6. **O(2ⁿ)** - 指数时间（斐波那契递归）\n7. **O(n!)** - 阶乘时间（全排列）\n\n### 计算规则\n1. 只看最高次项\n2. 去掉系数\n3. 嵌套循环相乘\n\n### 优化建议\n- 避免不必要的循环\n- 使用合适的数据结构\n- 空间换时间");
            kp5.setCategoryId(2L);
            kp5.setCategory("算法");
            kp5.setDifficulty(KnowledgePoint.Difficulty.EASY);
            kp5.setImportance(5);
            kp5.setTags("算法,时间复杂度,性能");
            kp5.setCreatedBy(2L);
            knowledgePointRepository.save(kp5);

            // 数据库知识点
            KnowledgePoint kp6 = new KnowledgePoint();
            kp6.setTitle("数据库索引原理");
            kp6.setContent("## 数据库索引\n\n### 索引的作用\n- 加快数据检索速度\n- 类似书的目录\n\n### MySQL索引类型\n1. **B+树索引**（最常用）\n   - 所有数据在叶子节点\n   - 叶子节点间有指针连接\n   - 范围查询效率高\n\n2. **哈希索引**\n   - 等值查询快\n   - 不支持范围查询\n\n3. **全文索引**\n   - 用于文本搜索\n\n### 索引优化建议\n1. **选择合适的列**\n   - 经常作为查询条件的列\n   - 区分度高的列\n\n2. **避免索引失效**\n   - 不要在索引列上使用函数\n   - 避免隐式类型转换\n   - Like不要以%开头\n\n3. **联合索引遵循最左前缀原则**");
            kp6.setCategoryId(3L);
            kp6.setCategory("数据库");
            kp6.setDifficulty(KnowledgePoint.Difficulty.MEDIUM);
            kp6.setImportance(5);
            kp6.setTags("数据库,索引,MySQL,B+树");
            kp6.setCreatedBy(2L);
            knowledgePointRepository.save(kp6);

            KnowledgePoint kp7 = new KnowledgePoint();
            kp7.setTitle("数据库事务ACID");
            kp7.setContent("## 数据库事务的ACID特性\n\n### A - 原子性（Atomicity）\n事务是不可分割的最小工作单元，要么全部成功，要么全部失败。\n\n**实现：** undo log（回滚日志）\n\n### C - 一致性（Consistency）\n事务执行前后，数据库从一个一致性状态转换到另一个一致性状态。\n\n**示例：** 转账操作，A减少的钱=B增加的钱\n\n### I - 隔离性（Isolation）\n多个事务并发执行时，互不干扰。\n\n**隔离级别：**\n1. 读未提交（Read Uncommitted）\n2. 读已提交（Read Committed）\n3. 可重复读（Repeatable Read）- MySQL默认\n4. 串行化（Serializable）\n\n### D - 持久性（Durability）\n事务一旦提交，对数据库的改变是永久性的。\n\n**实现：** redo log（重做日志）");
            kp7.setCategoryId(3L);
            kp7.setCategory("数据库");
            kp7.setDifficulty(KnowledgePoint.Difficulty.MEDIUM);
            kp7.setImportance(5);
            kp7.setTags("数据库,事务,ACID");
            kp7.setCreatedBy(2L);
            knowledgePointRepository.save(kp7);

            // 计算机网络知识点
            KnowledgePoint kp8 = new KnowledgePoint();
            kp8.setTitle("HTTP与HTTPS的区别");
            kp8.setContent("## HTTP vs HTTPS\n\n### HTTP（HyperText Transfer Protocol）\n- **端口：** 80\n- **安全性：** 明文传输，不安全\n- **速度：** 快\n\n### HTTPS（HTTP Secure）\n- **端口：** 443\n- **安全性：** SSL/TLS加密，安全\n- **速度：** 相对较慢（加密解密开销）\n- **证书：** 需要CA证书\n\n### HTTPS工作原理\n1. 客户端请求HTTPS连接\n2. 服务器返回公钥证书\n3. 客户端验证证书\n4. 生成对称密钥，用公钥加密发送\n5. 服务器用私钥解密获得对称密钥\n6. 后续通信使用对称密钥加密\n\n### 使用场景\n- **HTTP：** 普通网页\n- **HTTPS：** 登录、支付等敏感操作");
            kp8.setCategoryId(4L);
            kp8.setCategory("计算机网络");
            kp8.setDifficulty(KnowledgePoint.Difficulty.MEDIUM);
            kp8.setImportance(4);
            kp8.setTags("网络,HTTP,HTTPS,SSL");
            kp8.setCreatedBy(2L);
            knowledgePointRepository.save(kp8);

            System.out.println("✅ 数据初始化完成！共初始化 " + knowledgePointRepository.count() + " 个知识点");
        }
    }
}
