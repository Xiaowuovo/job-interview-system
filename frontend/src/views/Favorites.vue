<template>
  <div class="favorites-container">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span class="card-title">
          <i class="el-icon-star-on"></i> 我的收藏（{{ favorites.length }}）
        </span>
        <el-button
          style="float: right;"
          type="danger"
          size="small"
          @click="clearAll"
          v-if="favorites.length > 0">
          清空收藏
        </el-button>
      </div>

      <!-- 空状态 -->
      <div v-if="favorites.length === 0" class="empty-state">
        <i class="el-icon-star-off" style="font-size: 80px; color: #DCDFE6;"></i>
        <p>还没有收藏任何题目</p>
        <el-button type="primary" @click="$router.push('/home/practice')">
          去刷题
        </el-button>
      </div>

      <!-- 收藏列表 -->
      <el-table
        v-else
        :data="favorites"
        style="width: 100%"
        v-loading="loading">
        <el-table-column type="expand">
          <template slot-scope="props">
            <div class="expand-content">
              <div class="question-detail" v-if="props.row.question">
                <h4>题目内容</h4>
                <p>{{ props.row.question.content }}</p>

                <div v-if="props.row.question.type === 'CHOICE'" class="options">
                  <p><strong>A.</strong> {{ props.row.question.optionA }}</p>
                  <p><strong>B.</strong> {{ props.row.question.optionB }}</p>
                  <p><strong>C.</strong> {{ props.row.question.optionC }}</p>
                  <p><strong>D.</strong> {{ props.row.question.optionD }}</p>
                </div>
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
                <el-button size="small" type="danger" @click="removeFavorite(props.row)">
                  取消收藏
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="题目" prop="question.title" min-width="200">
          <template slot-scope="scope">
            <span class="question-title" @click="viewQuestion(scope.row.questionId)">
              {{ scope.row.question ? scope.row.question.title : '加载中...' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="分类" width="120">
          <template slot-scope="scope">
            <el-tag size="small" type="info">
              {{ scope.row.question ? scope.row.question.category : '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="难度" width="100">
          <template slot-scope="scope">
            <el-tag
              v-if="scope.row.question"
              :type="getDifficultyType(scope.row.question.difficulty)"
              size="small">
              {{ getDifficultyText(scope.row.question.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="收藏时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="viewQuestion(scope.row.questionId)">
              查看
            </el-button>
            <el-button size="mini" type="text" @click="editNotes(scope.row)">
              笔记
            </el-button>
            <el-button size="mini" type="text" style="color: #F56C6C;" @click="removeFavorite(scope.row)">
              删除
            </el-button>
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
  </div>
</template>

<script>
export default {
  name: 'Favorites',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      favorites: [],
      loading: false,
      notesDialogVisible: false,
      editingNotes: '',
      currentEditingRow: null
    }
  },
  mounted() {
    this.loadFavorites()
  },
  methods: {
    loadFavorites() {
      this.loading = true
      this.$http.get(`/favorites/user/${this.user.id}`).then(res => {
        if (res.data) {
          this.favorites = res.data
          // 加载每个收藏的题目详情
          this.loadQuestionDetails()
        }
      }).finally(() => {
        this.loading = false
      })
    },
    loadQuestionDetails() {
      this.favorites.forEach(favorite => {
        this.$http.get(`/questions/${favorite.questionId}`).then(res => {
          if (res.data) {
            this.$set(favorite, 'question', res.data)
          }
        }).catch(() => {})
      })
    },
    viewQuestion(questionId) {
      // 跳转到题目详情
      this.$message.info('题目详情功能开发中...')
    },
    editNotes(row) {
      this.currentEditingRow = row
      this.editingNotes = row.notes || ''
      this.notesDialogVisible = true
    },
    saveNotes() {
      this.$http.post('/favorites/add', {
        userId: this.user.id,
        questionId: this.currentEditingRow.questionId,
        notes: this.editingNotes
      }).then(() => {
        this.$message.success('笔记保存成功')
        this.notesDialogVisible = false
        this.loadFavorites()
      }).catch(() => {})
    },
    removeFavorite(row) {
      this.$confirm('确定要取消收藏这道题吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/favorites/remove?userId=${this.user.id}&questionId=${row.questionId}`)
          .then(() => {
            this.$message.success('已取消收藏')
            this.loadFavorites()
          }).catch(() => {})
      }).catch(() => {})
    },
    clearAll() {
      this.$confirm(`确定要清空所有收藏（${this.favorites.length}道题）吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const questionIds = this.favorites.map(f => f.questionId)
        this.$http.post('/favorites/remove-batch', {
          userId: this.user.id,
          questionIds: questionIds
        }).then(res => {
          this.$message.success(`已清空 ${res.data || questionIds.length} 个收藏`)
          this.loadFavorites()
        }).catch(() => {})
      }).catch(() => {})
    },
    getDifficultyType(difficulty) {
      const types = {
        'EASY': 'success',
        'MEDIUM': 'warning',
        'HARD': 'danger'
      }
      return types[difficulty] || 'info'
    },
    getDifficultyText(difficulty) {
      const texts = {
        'EASY': '简单',
        'MEDIUM': '中等',
        'HARD': '困难'
      }
      return texts[difficulty] || '未知'
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
/* 现代化 Favorites 页面 - 支持浅色/深色主题 */
.favorites-container {
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
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--lc-text-primary);
}

.card-title i {
  color: var(--lc-primary);
  margin-right: 8px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 0;
  color: var(--lc-text-muted);
}

.empty-state p {
  margin: 20px 0;
  font-size: 16px;
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

/* 题目标题 */
.question-title {
  cursor: pointer;
  color: var(--lc-primary);
  transition: color var(--lc-transition);
}

.question-title:hover {
  color: var(--lc-primary-light);
  text-decoration: underline;
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

.options p {
  margin: 8px 0;
  padding-left: 10px;
  color: var(--lc-text-secondary);
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

/deep/ .el-button--danger {
  background: var(--lc-danger-bg);
  border: 1px solid var(--lc-danger);
  color: var(--lc-danger);
}

/deep/ .el-button--text {
  color: var(--lc-primary);
}

/* 对话框 */
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

/deep/ .el-textarea__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/* 动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
