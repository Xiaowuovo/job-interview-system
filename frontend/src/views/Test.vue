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
        if (res.data.code === 200) {
          this.questions = res.data.data
          this.answers = new Array(this.questions.length).fill('')
          this.started = true
          this.startTime = Date.now()
          this.timeLeft = this.questionCount * 60
          this.startTimer()
          this.loadFavorites()
        }
      })
    },
    loadFavorites() {
      const user = JSON.parse(localStorage.getItem('user'))
      this.$http.get(`/favorites/user/${user.id}`).then(res => {
        if (res.data.code === 200) {
          this.favoriteIds = new Set(res.data.data.map(f => f.questionId))
        }
      })
    },
    isFavorited(questionId) {
      return this.favoriteIds.has(questionId)
    },
    toggleFavorite(question) {
      const user = JSON.parse(localStorage.getItem('user'))

      if (this.isFavorited(question.id)) {
        // 取消收藏
        this.$http.delete(`/favorites/remove?userId=${user.id}&questionId=${question.id}`)
          .then(res => {
            if (res.data.code === 200) {
              this.favoriteIds.delete(question.id)
              this.$message.success('已取消收藏')
            }
          })
      } else {
        // 添加收藏
        this.$http.post('/favorites/add', {
          userId: user.id,
          questionId: question.id,
          notes: ''
        }).then(res => {
          if (res.data.code === 200) {
            this.favoriteIds.add(question.id)
            this.$message.success('收藏成功')
          }
        })
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
      this.questions.forEach((q, index) => {
        if (this.answers[index] === q.correctAnswer) {
          correctCount++
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

      this.$http.post('/test-records', record).then(() => {
        this.finished = true
      })
    },
    viewAnswers() {
      this.showAnswers = !this.showAnswers
    }
  }
}
</script>

<style scoped>
.test {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.test-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
}

.question {
  margin: 30px 0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.question-header h3 {
  flex: 1;
  margin: 0;
  line-height: 1.8;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.options .el-radio {
  margin: 0;
  padding: 15px;
}

.test-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.result-detail {
  margin-top: 30px;
}

.answer-item {
  margin-bottom: 20px;
}

.answer-item p {
  margin: 8px 0;
  line-height: 1.6;
}

.explanation {
  color: #909399;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
