<template>
  <div class="practice">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>题目练习</h1>
      <p>选择一个分类开始你的练习之旅</p>
    </div>

    <!-- 分类卡片 -->
    <div class="category-grid">
      <div 
        class="category-card" 
        v-for="category in categories" 
        :key="category.name"
        @click="startTest(category.name)">
        <div class="card-content">
          <div class="category-icon" :style="{background: category.gradient}">
            <i :class="category.icon"></i>
          </div>
          <div class="category-info">
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
            <div class="category-stats">
              <span class="stat-item">
                <i class="el-icon-document"></i>
                {{ category.questionCount || 50 }}+ 题目
              </span>
              <span class="stat-item">
                <i class="el-icon-user"></i>
                {{ category.userCount || 1000 }}+ 练习
              </span>
            </div>
          </div>
        </div>
        <div class="card-action">
          <span class="start-btn">
            开始练习
            <i class="el-icon-arrow-right"></i>
          </span>
        </div>
      </div>
    </div>

    <!-- 快速入口 -->
    <div class="quick-section">
      <h2>快速入口</h2>
      <div class="quick-grid">
        <div class="quick-card" @click="$router.push('/home/wrong-questions')">
          <div class="quick-icon" style="background: rgba(239, 71, 67, 0.15); color: #ef4743;">
            <i class="el-icon-warning-outline"></i>
          </div>
          <div class="quick-info">
            <h4>错题本</h4>
            <p>复习录错题目</p>
          </div>
        </div>
        <div class="quick-card" @click="$router.push('/home/favorites')">
          <div class="quick-icon" style="background: rgba(255, 161, 22, 0.15); color: #ffa116;">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="quick-info">
            <h4>收藏夹</h4>
            <p>查看收藏题目</p>
          </div>
        </div>
        <div class="quick-card" @click="$router.push('/home/records')">
          <div class="quick-icon" style="background: rgba(0, 184, 163, 0.15); color: #00b8a3;">
            <i class="el-icon-data-line"></i>
          </div>
          <div class="quick-info">
            <h4>练习记录</h4>
            <p>查看历史成绩</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Practice',
  data() {
    return {
      categories: [
        {
          name: 'Java',
          description: 'Java基础、集合框架、多线程并发、JVM等',
          icon: 'el-icon-cpu',
          gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          questionCount: 120,
          userCount: 2500
        },
        {
          name: '前端',
          description: 'HTML/CSS、JavaScript、Vue/React、浏览器原理',
          icon: 'el-icon-monitor',
          gradient: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
          questionCount: 85,
          userCount: 1800
        },
        {
          name: '数据库',
          description: 'MySQL、Redis、索引优化、事务处理',
          icon: 'el-icon-coin',
          gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
          questionCount: 65,
          userCount: 1200
        },
        {
          name: '算法',
          description: '数据结构、排序算法、动态规划、图论',
          icon: 'el-icon-set-up',
          gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
          questionCount: 150,
          userCount: 3200
        }
      ]
    }
  },
  mounted() {
    this.loadDailyQuestion()
  },
  methods: {
    loadDailyQuestion() {
      this.$http.get('/daily-question/today').then(res => {
        if (res.data) {
          this.dailyQuestion = res.data.dailyQuestion
          if (this.dailyQuestion) {
            this.dailyQuestion.question = res.data.question
          }
        }
      }).catch(err => {
        console.error('加载每日一题失败', err)
      })
    },
    viewDailyQuestion() {
      if (this.dailyQuestion && this.dailyQuestion.questionId) {
        this.$message.info('题目详情页开发中，即将开放！')
        // 未来可以跳转到题目详情页
        // this.$router.push(`/home/question/${this.dailyQuestion.questionId}`)
      }
    },
    getDifficultyType(difficulty) {
      const types = {
        'EASY': 'success',
        'MEDIUM': 'warning',
        'HARD': 'danger'
      }
      return types[difficulty] || 'info'
    },
    startTest(category) {
      this.$router.push(`/home/test/${category}`)
    }
  }
}
</script>

<style scoped>
/* 现代化 Practice 页面 - 支持浅色/深色主题 */
.practice {
  padding: 0;
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeInUp 0.4s ease;
}

/* 页面标题 */
.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--lc-text-primary);
  margin-bottom: 8px;
  letter-spacing: -0.3px;
}

.page-header p {
  font-size: 15px;
  color: var(--lc-text-muted);
}

/* 分类卡片网格 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 40px;
}

.category-card {
  background: var(--lc-bg-card);
  border: 1px solid var(--lc-border);
  border-radius: var(--lc-radius-xl);
  padding: 24px;
  cursor: pointer;
  transition: all var(--lc-transition);
  display: flex;
  flex-direction: column;
}

.category-card:hover {
  border-color: var(--lc-primary);
  transform: translateY(-4px);
  box-shadow: var(--lc-shadow-lg);
}

.card-content {
  display: flex;
  gap: 20px;
  flex: 1;
}

.category-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--lc-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  flex-shrink: 0;
  box-shadow: var(--lc-shadow-sm);
}

.category-info {
  flex: 1;
}

.category-info h3 {
  font-size: 20px;
  font-weight: 600;
  color: var(--lc-text-primary);
  margin-bottom: 8px;
}

.category-info p {
  font-size: 14px;
  color: var(--lc-text-secondary);
  line-height: 1.6;
  margin-bottom: 12px;
}

.category-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  font-size: 12px;
  color: var(--lc-text-muted);
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item i {
  font-size: 14px;
}

.card-action {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--lc-border);
}

.start-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--lc-primary);
  font-size: 14px;
  font-weight: 600;
  transition: all var(--lc-transition);
}

.category-card:hover .start-btn {
  gap: 12px;
}

/* 快速入口 */
.quick-section {
  margin-top: 40px;
}

.quick-section h2 {
  font-size: 18px;
  font-weight: 600;
  color: var(--lc-text-primary);
  margin-bottom: 20px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.quick-card {
  background: var(--lc-bg-card);
  border: 1px solid var(--lc-border);
  border-radius: var(--lc-radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all var(--lc-transition);
}

.quick-card:hover {
  border-color: var(--lc-primary);
  background: var(--lc-bg-hover);
  transform: translateY(-2px);
  box-shadow: var(--lc-shadow);
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--lc-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;
}

.quick-info h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--lc-text-primary);
  margin-bottom: 4px;
}

.quick-info p {
  font-size: 13px;
  color: var(--lc-text-muted);
}

/* 响应式 */
@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-grid {
    grid-template-columns: 1fr;
  }
}

/* 动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
