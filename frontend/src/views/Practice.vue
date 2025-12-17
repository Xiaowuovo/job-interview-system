<template>
  <div class="practice">
    <el-card>
      <div slot="header">
        <span>题目练习</span>
      </div>

      <el-row :gutter="20">
        <el-col :span="6" v-for="category in categories" :key="category.name">
          <el-card class="category-card" @click.native="startTest(category.name)">
            <div class="category-icon" :style="{background: category.color}">
              <i :class="category.icon"></i>
            </div>
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
            <el-button type="primary" size="small">开始练习</el-button>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
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
          description: 'Java基础、集合、多线程等',
          icon: 'el-icon-cpu',
          color: '#409EFF'
        },
        {
          name: '前端',
          description: 'HTML、CSS、JavaScript、Vue等',
          icon: 'el-icon-monitor',
          color: '#67C23A'
        },
        {
          name: '数据库',
          description: 'MySQL、Redis、索引优化等',
          icon: 'el-icon-coin',
          color: '#E6A23C'
        },
        {
          name: '算法',
          description: '数据结构、排序、动态规划等',
          icon: 'el-icon-set-up',
          color: '#F56C6C'
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
        if (res.data.code === 200) {
          this.dailyQuestion = res.data.data.dailyQuestion
          this.dailyQuestion.question = res.data.data.question
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
.practice {
  padding: 20px;
}

/* 每日一题卡片 */
.daily-card {
  margin-bottom: 20px;
  border: 2px solid #F56C6C;
}

.daily-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.daily-info {
  flex: 1;
}

.daily-info h3 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 18px;
}

.daily-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.complete-count {
  color: #909399;
  font-size: 14px;
  margin-left: 10px;
}

.complete-count i {
  margin-right: 5px;
}

.category-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.category-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 16px rgba(0,0,0,0.2);
}

.category-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: white;
}

.category-card h3 {
  margin: 15px 0;
  color: #303133;
}

.category-card p {
  color: #909399;
  margin-bottom: 15px;
  min-height: 40px;
}
</style>
