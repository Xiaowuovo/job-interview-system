<template>
  <div class="wrong-questions-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-warning stat-icon" style="color: #F56C6C;"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalWrongQuestions }}</div>
              <div class="stat-label">总错题数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-circle-check stat-icon" style="color: #67C23A;"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.masteredCount }}</div>
              <div class="stat-label">已掌握</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-refresh stat-icon" style="color: #E6A23C;"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.needReviewCount }}</div>
              <div class="stat-label">待复习</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <i class="el-icon-data-line stat-icon" style="color: #409EFF;"></i>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.masteryRate }}%</div>
              <div class="stat-label">掌握率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作栏 -->
    <el-card class="filter-card">
      <el-row :gutter="20" type="flex" align="middle">
        <el-col :span="5">
          <el-select v-model="filterStatus" placeholder="筛选状态" @change="loadWrongQuestions" style="width: 100%;">
            <el-option label="全部" value="all"></el-option>
            <el-option label="未掌握" value="unmastered"></el-option>
            <el-option label="需要复习" value="needReview"></el-option>
            <el-option label="已掌握" value="mastered"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterCategory" placeholder="筛选分类" @change="loadWrongQuestions" style="width: 100%;">
            <el-option label="全部分类" value=""></el-option>
            <el-option label="Java" value="Java"></el-option>
            <el-option label="前端" value="前端"></el-option>
            <el-option label="数据库" value="数据库"></el-option>
            <el-option label="算法" value="算法"></el-option>
            <el-option label="计算机网络" value="计算机网络"></el-option>
            <el-option label="操作系统" value="操作系统"></el-option>
            <el-option label="系统设计" value="系统设计"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterDifficulty" placeholder="筛选难度" @change="loadWrongQuestions" style="width: 100%;">
            <el-option label="全部难度" value=""></el-option>
            <el-option label="简单" value="EASY"></el-option>
            <el-option label="中等" value="MEDIUM"></el-option>
            <el-option label="困难" value="HARD"></el-option>
          </el-select>
        </el-col>
        <el-col :span="9">
          <el-button-group>
            <el-button icon="el-icon-refresh" @click="resetFilters">重置筛选</el-button>
            <el-button icon="el-icon-delete" @click="clearMastered">清空已掌握</el-button>
          </el-button-group>
        </el-col>
        <el-col :span="6" style="text-align: right;">
          <el-button type="primary" icon="el-icon-edit" @click="startReview" :disabled="wrongQuestions.length === 0">
            开始复习
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 错题列表 -->
    <el-card class="question-list-card">
      <div v-if="wrongQuestions.length === 0" class="empty-state">
        <i class="el-icon-success" style="font-size: 80px; color: #67C23A;"></i>
        <p>暂无错题，继续保持！</p>
      </div>

      <el-table
        v-else
        :data="wrongQuestions"
        stripe
        style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props">
            <div class="expand-content">
              <div class="question-detail">
                <h4>题目内容</h4>
                <p>{{ getQuestionContent(props.row.questionId) }}</p>
              </div>
              <div class="notes-section" v-if="props.row.notes">
                <h4>我的笔记</h4>
                <p>{{ props.row.notes }}</p>
              </div>
              <el-divider></el-divider>
              <div class="action-buttons">
                <el-button size="small" type="primary" @click="viewQuestion(props.row.questionId)">
                  查看详情
                </el-button>
                <el-button size="small" @click="editNotes(props.row)">
                  {{ props.row.notes ? '编辑笔记' : '添加笔记' }}
                </el-button>
                <el-button size="small" type="success" @click="markAsMastered(props.row)" v-if="!props.row.isMastered">
                  标记已掌握
                </el-button>
                <el-button size="small" type="danger" @click="removeFromBook(props.row)">
                  移除错题
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="题目ID" prop="questionId" width="100"></el-table-column>

        <el-table-column label="错误次数" width="100">
          <template slot-scope="scope">
            <el-tag type="danger" size="small">{{ scope.row.wrongCount }}次</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="复习次数" width="100">
          <template slot-scope="scope">
            <el-tag type="info" size="small">{{ scope.row.reviewCount }}次</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isMastered ? 'success' : 'warning'" size="small">
              {{ scope.row.isMastered ? '已掌握' : '未掌握' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="下次复习时间" width="180">
          <template slot-scope="scope">
            <span v-if="scope.row.nextReviewAt && !scope.row.isMastered">
              {{ formatDate(scope.row.nextReviewAt) }}
              <el-tag v-if="needReview(scope.row.nextReviewAt)" type="danger" size="mini" style="margin-left: 5px;">
                需复习
              </el-tag>
            </span>
            <span v-else style="color: #909399;">-</span>
          </template>
        </el-table-column>

        <el-table-column label="最后复习" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.lastReviewAt) }}
          </template>
        </el-table-column>

        <el-table-column label="添加时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 笔记编辑对话框 -->
    <el-dialog
      title="编辑笔记"
      :visible.sync="notesDialogVisible"
      width="50%">
      <el-input
        type="textarea"
        :rows="6"
        placeholder="记录你对这道题的理解、解题思路、易错点等..."
        v-model="editingNotes">
      </el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="notesDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNotes">保存</el-button>
      </span>
    </el-dialog>

    <!-- 复习对话框 -->
    <el-dialog
      title="错题复习"
      :visible.sync="reviewDialogVisible"
      width="70%"
      :before-close="handleReviewClose">
      <div v-if="currentReviewQuestion" class="review-content">
        <div class="question-info">
          <h3>题目 {{ currentReviewIndex + 1 }} / {{ reviewList.length }}</h3>
          <p class="question-text">{{ getQuestionContent(currentReviewQuestion.questionId) }}</p>
        </div>
        <el-divider></el-divider>
        <div class="review-actions">
          <p style="margin-bottom: 20px;">这道题你现在掌握了吗？</p>
          <el-button-group>
            <el-button type="danger" @click="reviewAnswer(false)">
              <i class="el-icon-close"></i> 还没掌握
            </el-button>
            <el-button type="success" @click="reviewAnswer(true)">
              <i class="el-icon-check"></i> 已经掌握
            </el-button>
          </el-button-group>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'WrongQuestions',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      wrongQuestions: [],
      statistics: {
        totalWrongQuestions: 0,
        masteredCount: 0,
        unmasteredCount: 0,
        needReviewCount: 0,
        masteryRate: 0
      },
      filterStatus: 'all',
      filterCategory: '',
      filterDifficulty: '',
      notesDialogVisible: false,
      editingNotes: '',
      currentEditingRow: null,
      reviewDialogVisible: false,
      reviewList: [],
      currentReviewIndex: 0,
      currentReviewQuestion: null
    }
  },
  mounted() {
    this.loadWrongQuestions()
    this.loadStatistics()
  },
  methods: {
    loadWrongQuestions() {
      let url = `/wrong-questions/user/${this.user.id}`

      if (this.filterStatus === 'unmastered') {
        url = `/wrong-questions/user/${this.user.id}/unmastered`
      } else if (this.filterStatus === 'needReview') {
        url = `/wrong-questions/user/${this.user.id}/need-review`
      }

      this.$http.get(url).then(res => {
        if (res.data) {
          let questions = res.data

          // 如果是筛选已掌握，需要手动过滤
          if (this.filterStatus === 'mastered') {
            questions = questions.filter(q => q.isMastered)
          }

          // 分类筛选
          if (this.filterCategory) {
            questions = questions.filter(q =>
              q.question && q.question.category === this.filterCategory
            )
          }

          // 难度筛选
          if (this.filterDifficulty) {
            questions = questions.filter(q =>
              q.question && q.question.difficulty === this.filterDifficulty
            )
          }

          this.wrongQuestions = questions
        }
      })
    },
    resetFilters() {
      this.filterStatus = 'all'
      this.filterCategory = ''
      this.filterDifficulty = ''
      this.loadWrongQuestions()
    },
    loadStatistics() {
      this.$http.get(`/wrong-questions/statistics/${this.user.id}`).then(res => {
        if (res.data) {
          this.statistics = res.data
        }
      })
    },
    getQuestionContent(questionId) {
      // 这里简化处理，实际应该从题库获取题目详情
      return `题目ID: ${questionId} 的详细内容...`
    },
    viewQuestion(questionId) {
      // 跳转到题目详情页面
      this.$router.push(`/home/question/${questionId}`)
    },
    editNotes(row) {
      this.currentEditingRow = row
      this.editingNotes = row.notes || ''
      this.notesDialogVisible = true
    },
    saveNotes() {
      const data = {
        userId: this.user.id,
        questionId: this.currentEditingRow.questionId,
        notes: this.editingNotes
      }

      this.$http.post('/wrong-questions/notes', data).then(res => {
        this.$message.success('笔记保存成功')
        this.notesDialogVisible = false
        this.loadWrongQuestions()
      }).catch(() => {})
    },
    markAsMastered(row) {
      this.$http.post('/wrong-questions/review', {
        userId: this.user.id,
        questionId: row.questionId,
        mastered: true
      }).then(() => {
        this.$message.success('已标记为掌握')
        this.loadWrongQuestions()
        this.loadStatistics()
      }).catch(() => {})
    },
    removeFromBook(row) {
      this.$confirm('确定要从错题本中移除这道题吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/wrong-questions/remove?userId=${this.user.id}&questionId=${row.questionId}`)
          .then(() => {
            this.$message.success('已移除')
            this.loadWrongQuestions()
            this.loadStatistics()
          }).catch(() => {})
      }).catch(() => {})
    },
    clearMastered() {
      if (this.statistics.masteredCount === 0) {
        this.$message.info('没有已掌握的错题')
        return
      }

      this.$confirm(`确定要清空 ${this.statistics.masteredCount} 道已掌握的错题吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/wrong-questions/clear-mastered/${this.user.id}`)
          .then(res => {
            this.$message.success(`已清空 ${res.data || 0} 道已掌握的错题`)
            this.loadWrongQuestions()
            this.loadStatistics()
          }).catch(() => {})
      }).catch(() => {})
    },
    startReview() {
      // 开始复习模式
      this.reviewList = this.wrongQuestions.filter(q => !q.isMastered)

      if (this.reviewList.length === 0) {
        this.$message.info('没有需要复习的错题')
        return
      }

      this.currentReviewIndex = 0
      this.currentReviewQuestion = this.reviewList[0]
      this.reviewDialogVisible = true
    },
    reviewAnswer(mastered) {
      // 记录复习结果
      this.$http.post('/wrong-questions/review', {
        userId: this.user.id,
        questionId: this.currentReviewQuestion.questionId,
        mastered: mastered
      }).then(() => {
        // 下一题
        if (this.currentReviewIndex < this.reviewList.length - 1) {
          this.currentReviewIndex++
          this.currentReviewQuestion = this.reviewList[this.currentReviewIndex]
        } else {
          // 复习完成
          this.$message.success('复习完成！')
          this.reviewDialogVisible = false
          this.loadWrongQuestions()
          this.loadStatistics()
        }
      }).catch(() => {})
    },
    handleReviewClose() {
      this.$confirm('确定要结束复习吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.reviewDialogVisible = false
        this.loadWrongQuestions()
        this.loadStatistics()
      }).catch(() => {})
    },
    needReview(nextReviewAt) {
      if (!nextReviewAt) return false
      return new Date(nextReviewAt) <= new Date()
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
/* 现代化 WrongQuestions 页面 - 支持浅色/深色主题 */
.wrong-questions-container {
  padding: 0;
  animation: fadeInUp 0.4s ease;
}

/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all var(--lc-transition);
}

.stat-card:hover {
  transform: translateY(-4px);
  border-color: var(--lc-primary) !important;
  box-shadow: var(--lc-shadow-lg);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--lc-text-primary);
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--lc-text-muted);
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 20px;
}

/deep/ .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-select .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/* 表格样式 */
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

/* 题目列表 */
.question-list-card {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: var(--lc-text-muted);
}

.empty-state p {
  margin-top: 20px;
  font-size: 16px;
}

/* 展开内容 */
.expand-content {
  padding: 20px 50px;
  background: var(--lc-bg-primary);
}

.question-detail h4,
.notes-section h4 {
  margin-bottom: 10px;
  color: var(--lc-text-primary);
  font-size: 16px;
}

.question-detail p,
.notes-section p {
  color: var(--lc-text-secondary);
  line-height: 1.8;
  margin-bottom: 15px;
}

.action-buttons {
  text-align: right;
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

/deep/ .el-button--success {
  background: var(--lc-success-bg);
  border: 1px solid var(--lc-success);
  color: var(--lc-success);
}

/deep/ .el-button--danger {
  background: var(--lc-danger-bg);
  border: 1px solid var(--lc-danger);
  color: var(--lc-danger);
}

/* 复习对话框 */
/deep/ .el-dialog {
  background: var(--lc-bg-card);
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-dialog__header {
  border-bottom: 1px solid var(--lc-border);
}

/deep/ .el-dialog__title {
  color: var(--lc-text-primary);
}

.review-content {
  padding: 20px;
}

.question-info h3 {
  margin-bottom: 20px;
  color: var(--lc-text-primary);
}

.question-text {
  font-size: 16px;
  line-height: 1.8;
  color: var(--lc-text-secondary);
  padding: 20px;
  background: var(--lc-bg-tertiary);
  border-radius: var(--lc-radius);
  min-height: 100px;
  border: 1px solid var(--lc-border);
}

.review-actions {
  text-align: center;
  padding: 20px 0;
}

/* 动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
