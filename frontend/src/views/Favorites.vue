<template>
  <div class="favorites-container">
    <el-card shadow="hover">
      <div slot="header" class="clearfix">
        <span class="card-title">
          <i class="el-icon-star-on"></i> 我的收藏（{{ filteredFavorites.length }}）
        </span>
        <div style="float: right;">
          <el-radio-group v-model="filterType" size="small" @change="loadFavorites" style="margin-right: 10px;">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="question">题目</el-radio-button>
            <el-radio-button label="wrong">错题</el-radio-button>
            <el-radio-button label="tutorial">教程</el-radio-button>
            <el-radio-button label="knowledge">知识</el-radio-button>
          </el-radio-group>
          <el-button
            type="danger"
            size="small"
            @click="clearAll"
            v-if="filteredFavorites.length > 0">
            清空收藏
          </el-button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredFavorites.length === 0" class="empty-state">
        <i class="el-icon-star-off" style="font-size: 80px; color: #DCDFE6;"></i>
        <p>还没有收藏内容</p>
        <el-button type="primary" @click="$router.push('/home/dashboard')">
          去浏览
        </el-button>
      </div>

      <!-- 收藏列表 -->
      <el-table
        v-else
        :data="filteredFavorites"
        style="width: 100%"
        v-loading="loading"
        :row-key="row => row.id"
        ref="favoritesTable">
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
                <el-button size="small" type="primary" @click="viewItem(props.row)">
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

        <el-table-column label="类型" width="100">
          <template slot-scope="scope">
            <el-tag size="small" :type="getTypeTagType(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="标题" min-width="200">
          <template slot-scope="scope">
            <span class="question-title" @click="viewItem(scope.row)">
              {{ getItemTitle(scope.row) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="分类" width="120">
          <template slot-scope="scope">
            <el-tag size="small" type="info">
              {{ getItemCategory(scope.row) }}
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
            <el-button size="mini" type="text" @click="viewItem(scope.row)">
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
      filterType: 'all', // all, question, wrong, tutorial, knowledge
      loading: false,
      notesDialogVisible: false,
      editingNotes: '',
      currentEditingRow: null
    }
  },
  computed: {
    filteredFavorites() {
      if (this.filterType === 'all') {
        return this.favorites
      }
      return this.favorites.filter(f => f.type === this.filterType)
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
          // 为每个收藏项添加type字段（如果后端没有提供）
          this.favorites = res.data.map(item => {
            if (!item.type) {
              // 根据字段判断类型
              if (item.questionId) item.type = 'question'
              else if (item.tutorialId) item.type = 'tutorial'
              else if (item.knowledgeId) item.type = 'knowledge'
              else if (item.wrongQuestionId) item.type = 'wrong'
            }
            return item
          })
          // 加载每个收藏的详情
          this.loadItemDetails()
        }
      }).finally(() => {
        this.loading = false
      })
    },
    loadItemDetails() {
      this.favorites.forEach(favorite => {
        if (favorite.questionId) {
          this.$http.get(`/questions/${favorite.questionId}`).then(res => {
            if (res.data) {
              this.$set(favorite, 'question', res.data)
            }
          }).catch(() => {})
        } else if (favorite.tutorialId) {
          this.$http.get(`/tutorials/${favorite.tutorialId}`).then(res => {
            if (res.data) {
              this.$set(favorite, 'tutorial', res.data)
            }
          }).catch(() => {})
        } else if (favorite.knowledgeId) {
          this.$http.get(`/knowledge/${favorite.knowledgeId}`).then(res => {
            if (res.data) {
              this.$set(favorite, 'knowledge', res.data)
            }
          }).catch(() => {})
        } else if (favorite.wrongQuestionId) {
          this.$http.get(`/wrong-questions/${favorite.wrongQuestionId}`).then(res => {
            if (res.data) {
              this.$set(favorite, 'wrongQuestion', res.data)
              // 加载错题对应的题目
              if (res.data.questionId) {
                this.$http.get(`/questions/${res.data.questionId}`).then(qRes => {
                  if (qRes.data) {
                    this.$set(favorite, 'question', qRes.data)
                  }
                }).catch(() => {})
              }
            }
          }).catch(() => {})
        }
      })
    },
    viewItem(item) {
      // 根据类型跳转到不同的页面
      if (item.type === 'question' && item.questionId) {
        // 展开表格行显示详情
        if (this.$refs.favoritesTable) {
          this.$refs.favoritesTable.toggleRowExpansion(item, true)
        }
      } else if (item.type === 'wrong' && item.wrongQuestion) {
        // 跳转到错题本
        this.$router.push('/home/wrong-questions')
      } else if (item.type === 'tutorial' && item.tutorialId) {
        // 跳转到教程详情
        this.$router.push(`/home/tutorial/${item.tutorialId}`)
      } else if (item.type === 'knowledge' && item.knowledgeId) {
        // 展开知识点详情（可以打开对话框或跳转）
        this.$message.info('正在打开知识点...')
        this.$router.push('/home/knowledge')
      } else {
        this.$message.info('内容加载中...')
      }
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
    },
    getTypeText(type) {
      const types = {
        'question': '题目',
        'wrong': '错题',
        'tutorial': '教程',
        'knowledge': '知识'
      }
      return types[type] || '未知'
    },
    getTypeTagType(type) {
      const types = {
        'question': 'primary',
        'wrong': 'danger',
        'tutorial': 'success',
        'knowledge': 'warning'
      }
      return types[type] || 'info'
    },
    getItemTitle(item) {
      if (item.question) return item.question.title
      if (item.tutorial) return item.tutorial.title
      if (item.knowledge) return item.knowledge.title
      if (item.wrongQuestion && item.wrongQuestion.question) return item.wrongQuestion.question.title
      return '加载中...'
    },
    getItemCategory(item) {
      if (item.question) return item.question.category
      if (item.tutorial) return item.tutorial.category
      if (item.knowledge) return item.knowledge.category
      if (item.wrongQuestion && item.wrongQuestion.question) return item.wrongQuestion.question.category
      return '-'
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
