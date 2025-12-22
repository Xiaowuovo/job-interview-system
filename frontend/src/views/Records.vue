<template>
  <div class="records">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="测试记录" name="test">
          <el-table :data="testRecords" style="width: 100%">
            <el-table-column prop="category" label="分类" width="120"></el-table-column>
            <el-table-column prop="totalQuestions" label="题目数" width="100"></el-table-column>
            <el-table-column prop="correctCount" label="正确数" width="100"></el-table-column>
            <el-table-column label="得分" width="100">
              <template slot-scope="scope">
                <el-tag :type="getScoreType(scope.row.score)">
                  {{ scope.row.score.toFixed(1) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="用时" width="100">
              <template slot-scope="scope">
                {{ formatDuration(scope.row.timeSpent) }}
              </template>
            </el-table-column>
            <el-table-column label="测试时间">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>

          <div class="statistics" v-if="testStats">
            <el-row :gutter="20" style="margin-top: 20px;">
              <el-col :span="8">
                <el-statistic title="总测试次数" :value="testStats.totalTests"></el-statistic>
              </el-col>
              <el-col :span="8">
                <el-statistic title="平均分数" :value="testStats.averageScore" :precision="1" suffix="分"></el-statistic>
              </el-col>
              <el-col :span="8">
                <el-statistic title="最高分" :value="highestScore" :precision="1" suffix="分"></el-statistic>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <el-tab-pane label="面试记录" name="interview">
          <el-table :data="interviewRecords" style="width: 100%">
            <el-table-column prop="type" label="面试类型" width="120"></el-table-column>
            <el-table-column label="综合得分" width="100">
              <template slot-scope="scope">
                <el-tag :type="getScoreType(scope.row.avgScore)">
                  {{ scope.row.avgScore }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="技术" width="80">
              <template slot-scope="scope">
                {{ scope.row.technicalScore }}
              </template>
            </el-table-column>
            <el-table-column label="逻辑" width="80">
              <template slot-scope="scope">
                {{ scope.row.logicalScore }}
              </template>
            </el-table-column>
            <el-table-column label="表达" width="80">
              <template slot-scope="scope">
                {{ scope.row.expressionScore }}
              </template>
            </el-table-column>
            <el-table-column label="问题数" width="80">
              <template slot-scope="scope">
                {{ scope.row.questionCount }}
              </template>
            </el-table-column>
            <el-table-column label="面试时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button
                  type="text"
                  size="small"
                  @click="viewInterviewReport(scope.row.id)">
                  <i class="el-icon-document"></i> 查看报告
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Records',
  data() {
    return {
      activeTab: 'test',
      testRecords: [],
      interviewRecords: [],
      testStats: null
    }
  },
  computed: {
    highestScore() {
      if (!this.testRecords.length) return 0
      return Math.max(...this.testRecords.map(r => r.score))
    }
  },
  created() {
    this.loadRecords()
  },
  methods: {
    loadRecords() {
      const user = JSON.parse(localStorage.getItem('user'))

      this.$http.get(`/test-records/user/${user.id}`).then(res => {
        if (res.data) {
          this.testRecords = res.data
        }
      }).catch(() => {})

      this.$http.get(`/test-records/statistics/${user.id}`).then(res => {
        if (res.data) {
          this.testStats = res.data
        }
      }).catch(() => {})

      this.$http.get(`/interview/history/${user.id}`).then(res => {
        if (res.data) {
          this.interviewRecords = res.data
        }
      }).catch(() => {})
    },
    getScoreType(score) {
      if (score >= 85) return 'success'
      if (score >= 70) return 'warning'
      return 'danger'
    },
    formatDate(date) {
      return new Date(date).toLocaleString('zh-CN')
    },
    viewInterviewReport(sessionId) {
      this.$router.push(`/home/interview-report/${sessionId}`)
    },
    formatDuration(seconds) {
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return `${m}分${s}秒`
    }
  }
}
</script>

<style scoped>
/* 现代化 Records 页面 - 支持浅色/深色主题 */
.records {
  padding: 0;
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

/deep/ .el-tabs__item {
  color: var(--lc-text-secondary);
}

/deep/ .el-tabs__item.is-active {
  color: var(--lc-primary);
}

/deep/ .el-tabs__active-bar {
  background: var(--lc-primary);
}

/deep/ .el-table {
  background: transparent !important;
  color: var(--lc-text-primary);
}

/deep/ .el-table tr {
  background: transparent !important;
}

/deep/ .el-table th {
  background: var(--lc-bg-tertiary) !important;
  color: var(--lc-text-secondary);
  border-bottom: 1px solid var(--lc-border);
}

/deep/ .el-table td {
  border-bottom: 1px solid var(--lc-border);
}

/deep/ .el-table--enable-row-hover .el-table__body tr:hover > td {
  background: var(--lc-bg-hover) !important;
}

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

/deep/ .el-button--text {
  color: var(--lc-primary);
}

/deep/ .el-button--text:hover {
  color: var(--lc-primary-light);
}

.statistics {
  margin-top: 30px;
  padding: 24px;
  background: var(--lc-bg-tertiary);
  border-radius: var(--lc-radius-xl);
  border: 1px solid var(--lc-border);
}

.statistics h4 {
  color: var(--lc-text-primary);
  margin-bottom: 16px;
}

/deep/ .el-descriptions__body {
  background: transparent;
  color: var(--lc-text-primary);
}

/deep/ .el-descriptions-item__label {
  color: var(--lc-text-secondary);
}

/deep/ .el-descriptions-item__content {
  color: var(--lc-text-primary);
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
