<template>
  <div class="test">
    <el-card v-if="!started">
      <h2>{{ category }} 题目练习</h2>
      <p>本次测试将随机抽取 10 道题目</p>
      <el-form label-width="100px">
        <el-form-item label="题目数量">
          <el-input-number v-model="questionCount" :min="5" :max="20"></el-input-number>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="startTest">开始测试</el-button>
      <el-button @click="$router.back()">返回</el-button>
    </el-card>

    <el-card v-else-if="!finished">
      <div class="test-header">
        <span>题目 {{ currentIndex + 1 }} / {{ questions.length }}</span>
        <span>剩余时间: {{ formatTime(timeLeft) }}</span>
      </div>

      <el-progress :percentage="progress" :color="progressColor"></el-progress>

      <div class="question" v-if="currentQuestion">
        <div class="question-header">
          <h3>{{ currentIndex + 1 }}. {{ currentQuestion.content }}</h3>
          <el-button
            :type="isFavorited(currentQuestion.id) ? 'warning' : 'default'"
            :icon="isFavorited(currentQuestion.id) ? 'el-icon-star-on' : 'el-icon-star-off'"
            size="small"
            circle
            @click="toggleFavorite(currentQuestion)">
          </el-button>
        </div>
        <el-radio-group v-model="answers[currentIndex]" class="options">
          <el-radio label="A" border>A. {{ currentQuestion.optionA }}</el-radio>
          <el-radio label="B" border>B. {{ currentQuestion.optionB }}</el-radio>
          <el-radio label="C" border>C. {{ currentQuestion.optionC }}</el-radio>
          <el-radio label="D" border>D. {{ currentQuestion.optionD }}</el-radio>
        </el-radio-group>
      </div>

      <div class="test-actions">
        <el-button @click="prevQuestion" :disabled="currentIndex === 0">上一题</el-button>
        <el-button type="primary" @click="nextQuestion" v-if="currentIndex < questions.length - 1">
          下一题
        </el-button>
        <el-button type="success" @click="submitTest" v-else>提交</el-button>
      </div>
    </el-card>

    <el-card v-else>
      <el-result icon="success" title="测试完成" :sub-title="`得分: ${score}分`">
        <template slot="extra">
          <el-button type="primary" @click="viewAnswers">查看答案</el-button>
          <el-button @click="$router.push('/home/practice')">返回</el-button>
        </template>
      </el-result>

      <div class="result-detail" v-if="showAnswers">
        <h3>答题详情</h3>
        <div v-for="(question, index) in questions" :key="index" class="answer-item">
          <p><strong>{{ index + 1 }}. {{ question.content }}</strong></p>
          <p>
            <span>你的答案: </span>
            <el-tag :type="answers[index] === question.correctAnswer ? 'success' : 'danger'">
              {{ answers[index] || '未作答' }}
            </el-tag>
          </p>
          <p v-if="answers[index] !== question.correctAnswer">
            <span>正确答案: </span>
            <el-tag type="success">{{ question.correctAnswer }}</el-tag>
          </p>
          <p class="explanation">{{ question.explanation }}</p>
          <el-divider></el-divider>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Test',
  data() {
    return {
      category: '',
      questionCount: 10,
      started: false,
      finished: false,
      questions: [],
      currentIndex: 0,
      answers: [],
      timeLeft: 0,
      timer: null,
      startTime: null,
      score: 0,
      showAnswers: false,
      favoriteIds: new Set()
    }
  },
  computed: {
    currentQuestion() {
      return this.questions[this.currentIndex]
    },
    progress() {
      return ((this.currentIndex + 1) / this.questions.length) * 100
    },
    progressColor() {
      return this.progress < 50 ? '#F56C6C' : this.progress < 80 ? '#E6A23C' : '#67C23A'
    }
  },
  created() {
    this.category = this.$route.params.category
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    startTest() {
      this.$http.get('/questions/random', {
        params: {
          category: this.category,
          count: this.questionCount
        }
      }).then(res => {
        // 响应拦截器已经返回response.data，所以直接用res
        if (!res.data || res.data.length === 0) {
          this.$message.warning('暂无题目，请联系管理员添加题目')
          return
        }
        this.questions = res.data
        this.answers = new Array(this.questions.length).fill('')
        this.started = true
        this.startTime = Date.now()
        this.timeLeft = this.questionCount * 60
        this.startTimer()
        this.loadFavorites()
      }).catch(err => {
        console.error('获取题目失败:', err)
        this.$message.error('获取题目失败，请稍后重试')
      })
    },
    loadFavorites() {
      const user = JSON.parse(localStorage.getItem('user'))
      this.$http.get(`/favorites/user/${user.id}`).then(res => {
        if (res.data) {
          this.favoriteIds = new Set(res.data.map(f => f.questionId))
        }
      }).catch(() => {})
    },
    isFavorited(questionId) {
      return this.favoriteIds.has(questionId)
    },
    toggleFavorite(question) {
      const user = JSON.parse(localStorage.getItem('user'))

      if (this.isFavorited(question.id)) {
        // 取消收藏
        this.$http.delete(`/favorites/remove?userId=${user.id}&questionId=${question.id}`)
          .then(() => {
            this.favoriteIds.delete(question.id)
            this.$message.success('已取消收藏')
          }).catch(() => {})
      } else {
        // 添加收藏
        this.$http.post('/favorites/add', {
          userId: user.id,
          questionId: question.id,
          notes: ''
        }).then(() => {
          this.favoriteIds.add(question.id)
          this.$message.success('收藏成功')
        }).catch(() => {})
      }
    },
    startTimer() {
      this.timer = setInterval(() => {
        this.timeLeft--
        if (this.timeLeft <= 0) {
          this.submitTest()
        }
      }, 1000)
    },
    formatTime(seconds) {
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return `${m}:${s.toString().padStart(2, '0')}`
    },
    prevQuestion() {
      if (this.currentIndex > 0) {
        this.currentIndex--
      }
    },
    nextQuestion() {
      if (this.currentIndex < this.questions.length - 1) {
        this.currentIndex++
      }
    },
    submitTest() {
      clearInterval(this.timer)

      let correctCount = 0
      const wrongQuestionIds = []
      
      this.questions.forEach((q, index) => {
        if (this.answers[index] === q.correctAnswer) {
          correctCount++
        } else {
          // 记录错题
          wrongQuestionIds.push(q.id)
        }
      })

      this.score = Math.round((correctCount / this.questions.length) * 100)
      const timeSpent = Math.floor((Date.now() - this.startTime) / 1000)

      const user = JSON.parse(localStorage.getItem('user'))
      const record = {
        userId: user.id,
        category: this.category,
        totalQuestions: this.questions.length,
        correctCount: correctCount,
        timeSpent: timeSpent
      }

      // 保存测试记录
      this.$http.post('/test-records', record).then(() => {
        this.finished = true
        
        // 发送测试完成事件，通知其他组件刷新数据
        this.$bus.$emit(this.$events.TEST_COMPLETED, {
          score: this.score,
          correctCount,
          totalQuestions: this.questions.length,
          category: this.category
        })
        
        // 保存错题到错题本
        if (wrongQuestionIds.length > 0) {
          this.saveWrongQuestions(user.id, wrongQuestionIds)
        }
      })
    },
    saveWrongQuestions(userId, questionIds) {
      // 批量添加错题
      questionIds.forEach(questionId => {
        this.$http.post('/wrong-questions/add', {
          userId: userId,
          questionId: questionId,
          wrongAnswer: this.answers[this.questions.findIndex(q => q.id === questionId)] || '未作答'
        }).catch(() => {})
      })
      
      // 发送错题变化事件
      this.$bus.$emit(this.$events.WRONG_QUESTION_CHANGED)
    },
    viewAnswers() {
      this.showAnswers = !this.showAnswers
    }
  }
}
</script>

<style scoped>
/* 现代化 Test 页面 - 支持浅色/深色主题 */
.test {
  padding: 0;
  max-width: 900px;
  margin: 0 auto;
  animation: fadeInUp 0.4s ease;
}

/* 卡片样式 */
/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-card h2 {
  color: var(--lc-text-primary);
  font-size: 24px;
  margin-bottom: 12px;
}

/deep/ .el-card p {
  color: var(--lc-text-secondary);
}

.test-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: var(--lc-bg-tertiary);
  border-radius: var(--lc-radius-lg);
  font-size: 15px;
  font-weight: 600;
  color: var(--lc-text-primary);
}

/* 进度条 */
/deep/ .el-progress-bar__outer {
  background: var(--lc-bg-tertiary);
}

/deep/ .el-progress-bar__inner {
  background: var(--lc-gradient-primary);
}

.question {
  margin: 30px 0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.question-header h3 {
  flex: 1;
  margin: 0;
  line-height: 1.8;
  color: var(--lc-text-primary);
  font-size: 18px;
}

/* 选项样式 */
.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.options /deep/ .el-radio {
  margin: 0;
  padding: 16px 20px;
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  border-radius: var(--lc-radius-lg);
  width: 100%;
  transition: all var(--lc-transition);
}

.options /deep/ .el-radio:hover {
  border-color: var(--lc-primary);
  background: var(--lc-bg-hover);
}

.options /deep/ .el-radio.is-checked {
  border-color: var(--lc-primary);
  background: var(--lc-primary-bg);
}

.options /deep/ .el-radio__label {
  color: var(--lc-text-primary);
  font-size: 15px;
}

.options /deep/ .el-radio__input.is-checked .el-radio__inner {
  background: var(--lc-primary);
  border-color: var(--lc-primary);
}

.test-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}

.test-actions /deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: var(--lc-text-inverse);
  font-weight: 600;
  padding: 12px 32px;
  border-radius: var(--lc-radius-lg);
}

.test-actions /deep/ .el-button--success {
  background: var(--lc-gradient-success);
  border: none;
  color: #fff;
  font-weight: 600;
  padding: 12px 32px;
  border-radius: var(--lc-radius-lg);
}

.test-actions /deep/ .el-button--default {
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
  padding: 12px 32px;
  border-radius: var(--lc-radius-lg);
}

.test-actions /deep/ .el-button--default:hover {
  border-color: var(--lc-primary);
  color: var(--lc-primary);
}

/* 结果页面 */
/deep/ .el-result__title {
  color: var(--lc-text-primary) !important;
}

/deep/ .el-result__subtitle {
  color: var(--lc-primary) !important;
  font-size: 24px !important;
  font-weight: 700 !important;
}

.result-detail {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--lc-border);
}

.result-detail h3 {
  color: var(--lc-text-primary);
  margin-bottom: 20px;
}

.answer-item {
  margin-bottom: 24px;
  padding: 20px;
  background: var(--lc-bg-tertiary);
  border-radius: var(--lc-radius-lg);
  border: 1px solid var(--lc-border);
}

.answer-item p {
  margin: 8px 0;
  line-height: 1.6;
  color: var(--lc-text-primary);
}

.answer-item strong {
  color: var(--lc-text-primary);
}

.explanation {
  color: var(--lc-text-secondary);
  padding: 12px 16px;
  background: var(--lc-bg-card);
  border-radius: var(--lc-radius);
  border-left: 3px solid var(--lc-primary);
  margin-top: 12px;
}

/* 收藏按钮 */
/deep/ .el-button--warning {
  background: var(--lc-primary-bg);
  border-color: var(--lc-primary);
  color: var(--lc-primary);
}

/* 分割线 */
/deep/ .el-divider {
  background: var(--lc-border);
}

/* 标签 */
/deep/ .el-tag--success {
  background: var(--lc-success-bg);
  border-color: transparent;
  color: var(--lc-success);
}

/deep/ .el-tag--danger {
  background: var(--lc-danger-bg);
  border-color: transparent;
  color: var(--lc-danger);
}

/* 输入框 */
/deep/ .el-input-number {
  background: var(--lc-bg-tertiary);
}

/deep/ .el-input-number .el-input__inner {
  background: var(--lc-bg-tertiary);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-form-item__label {
  color: var(--lc-text-secondary);
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
