<template>
  <div class="study-report">
    <el-page-header @back="goBack" content="学习报告"></el-page-header>

    <!-- 时间周期选择 -->
    <el-card shadow="never" class="period-selector">
      <el-radio-group v-model="period" @change="loadReport" size="medium">
        <el-radio-button label="">累计</el-radio-button>
        <el-radio-button label="month">本月</el-radio-button>
        <el-radio-button label="week">本周</el-radio-button>
      </el-radio-group>
    </el-card>

    <div v-loading="loading">
      <el-row :gutter="20" v-if="report">
        <!-- 学习时长统计 -->
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #409EFF;">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ report.totalStudyTime }}分钟</div>
              <div class="stat-label">学习时长</div>
              <div class="stat-detail">
                学习{{ report.studyDays }}天 · 日均{{ report.avgStudyTime }}分钟
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 刷题统计 -->
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #67C23A;">
              <i class="el-icon-edit"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ report.totalQuestions }}题</div>
              <div class="stat-label">完成题目</div>
              <div class="stat-detail">
                正确{{ report.correctQuestions }}题 · 正确率{{ report.accuracy }}%
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 综合得分 -->
        <el-col :span="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #E6A23C;">
              <i class="el-icon-medal"></i>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ report.avgTestScore }}分</div>
              <div class="stat-label">平均成绩</div>
              <div class="stat-detail">
                测试{{ report.testCount }}次 · 面试{{ report.interviewCount }}次
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>


      <!-- 分类统计 -->
      <el-card shadow="hover" style="margin-top: 20px;" v-if="report && report.categoryStats">
        <div slot="header">
          <i class="el-icon-pie-chart"></i> 分类统计
        </div>
        <el-table :data="categoryTableData" style="width: 100%">
          <el-table-column label="分类" prop="category" width="150"></el-table-column>
          <el-table-column label="题目数" prop="total" width="100"></el-table-column>
          <el-table-column label="正确数" prop="correct" width="100"></el-table-column>
          <el-table-column label="正确率" width="200">
            <template slot-scope="scope">
              <el-progress
                :percentage="scope.row.accuracy"
                :color="getAccuracyColor(scope.row.accuracy)">
              </el-progress>
            </template>
          </el-table-column>
          <el-table-column label="掌握程度">
            <template slot-scope="scope">
              <el-tag :type="getMasteryType(scope.row.accuracy)" size="small">
                {{ getMasteryText(scope.row.accuracy) }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 错题本统计 -->
      <el-card shadow="hover" style="margin-top: 20px;" v-if="report">
        <div slot="header">
          <i class="el-icon-warning-outline"></i> 错题本统计
        </div>
        <div class="wrong-stats">
          <div class="wrong-item">
            <div class="wrong-label">错题总数</div>
            <div class="wrong-value">{{ report.wrongCount }}</div>
          </div>
          <div class="wrong-item">
            <div class="wrong-label">已掌握</div>
            <div class="wrong-value" style="color: #67C23A;">{{ report.masteredCount }}</div>
          </div>
          <div class="wrong-item">
            <div class="wrong-label">掌握率</div>
            <div class="wrong-value">
              {{ report.wrongCount > 0 ? ((report.masteredCount / report.wrongCount * 100).toFixed(1)) : 0 }}%
            </div>
          </div>
        </div>
        <el-divider></el-divider>
        <div style="text-align: center;">
          <el-button type="primary" size="small" @click="goToWrongQuestions">
            去错题本复习
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StudyReport',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      period: '',
      report: null,
      loading: false
    }
  },
  computed: {
    categoryTableData() {
      if (!this.report || !this.report.categoryStats) return []

      return Object.entries(this.report.categoryStats).map(([category, stats]) => ({
        category,
        total: stats.total,
        correct: stats.correct,
        accuracy: Math.round(stats.accuracy)
      }))
    }
  },
  mounted() {
    this.loadReport()
  },
  methods: {
    loadReport() {
      this.loading = true
      const params = this.period ? `?period=${this.period}` : ''

      this.$http.get(`/reports/study/${this.user.id}${params}`).then(res => {
        if (res.data) {
          this.report = {
            totalStudyTime: res.data.totalStudyTime || 0,
            studyDays: res.data.studyDays || 0,
            avgStudyTime: res.data.avgStudyTime || 0,
            totalQuestions: res.data.totalQuestions || 0,
            correctQuestions: res.data.correctQuestions || 0,
            accuracy: res.data.accuracy || '0.0',
            avgTestScore: res.data.avgTestScore || '0.0',
            testCount: res.data.testCount || 0,
            interviewCount: res.data.interviewCount || 0,
            wrongCount: res.data.wrongCount || 0,
            masteredCount: res.data.masteredCount || 0,
            categoryStats: res.data.categoryStats || {}
          }
        }
      }).catch(() => {
        this.report = {
          totalStudyTime: 0, studyDays: 0, avgStudyTime: 0,
          totalQuestions: 0, correctQuestions: 0, accuracy: '0.0',
          avgTestScore: '0.0', testCount: 0, interviewCount: 0,
          wrongCount: 0, masteredCount: 0, categoryStats: {}
        }
      }).finally(() => {
        this.loading = false
      })
    },
    getAccuracyColor(accuracy) {
      if (accuracy >= 80) return '#67C23A'
      if (accuracy >= 60) return '#E6A23C'
      return '#F56C6C'
    },
    getMasteryType(accuracy) {
      if (accuracy >= 80) return 'success'
      if (accuracy >= 60) return 'warning'
      return 'danger'
    },
    getMasteryText(accuracy) {
      if (accuracy >= 80) return '熟练'
      if (accuracy >= 60) return '一般'
      return '薄弱'
    },
    goToWrongQuestions() {
      this.$router.push('/home/wrong-questions')
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
/* 现代化 StudyReport 页面 - 支持浅色/深色主题 */
.study-report {
  padding: 0;
  max-width: 1400px;
  margin: 0 auto;
  animation: fadeInUp 0.4s ease;
}

/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-card__header {
  border-bottom: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
}

.period-selector {
  margin: 20px 0;
}

/deep/ .el-radio-button__inner {
  background: var(--lc-bg-tertiary);
  border-color: var(--lc-border);
  color: var(--lc-text-secondary);
}

/deep/ .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: var(--lc-gradient-primary);
  border-color: var(--lc-primary);
  color: var(--lc-text-inverse);
}

/* 统计卡片 */
.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--lc-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin-right: 20px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--lc-text-primary);
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--lc-text-muted);
  margin-bottom: 5px;
}

.stat-detail {
  font-size: 12px;
  color: var(--lc-text-muted);
}

/* 错题统计 */
.wrong-stats {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.wrong-item {
  text-align: center;
}

.wrong-label {
  font-size: 14px;
  color: var(--lc-text-muted);
  margin-bottom: 10px;
}

.wrong-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--lc-text-primary);
}

/* 成就徽章 */
.achievements {
  min-height: 100px;
  padding: 10px;
}

.empty-achievements {
  text-align: center;
  padding: 40px 0;
  color: var(--lc-text-muted);
}

/* 学习建议 */
.suggestions {
  padding: 10px;
}

.suggestion-item {
  padding: 12px 16px;
  margin-bottom: 10px;
  background: var(--lc-bg-tertiary);
  border-left: 3px solid var(--lc-primary);
  border-radius: var(--lc-radius);
}

.suggestion-item i {
  color: var(--lc-primary);
  margin-right: 8px;
}

.suggestion-item span {
  color: var(--lc-text-secondary);
}

/* 标签样式 */
/deep/ .el-tag--success {
  background: var(--lc-success-bg);
  border-color: transparent;
  color: var(--lc-success);
}

/deep/ .el-tag--warning {
  background: var(--lc-warning-bg);
  border-color: transparent;
  color: var(--lc-warning);
}

/deep/ .el-tag--danger {
  background: var(--lc-danger-bg);
  border-color: transparent;
  color: var(--lc-danger);
}

/* 按钮样式 */
/deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: var(--lc-text-inverse);
  font-weight: 600;
}

/deep/ .el-button--default {
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-progress-bar__outer {
  background: var(--lc-bg-tertiary);
}

/deep/ .el-progress-bar__inner {
  background: var(--lc-gradient-primary);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
