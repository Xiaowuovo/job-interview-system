<template>
  <div class="knowledge-container">
    <el-card class="search-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-select v-model="searchCategory" placeholder="选择分类" clearable @change="loadKnowledgePoints">
            <el-option label="全部分类" value=""></el-option>
            <el-option label="Java基础" value="Java基础"></el-option>
            <el-option label="数据结构" value="数据结构"></el-option>
            <el-option label="算法" value="算法"></el-option>
            <el-option label="数据库" value="数据库"></el-option>
            <el-option label="操作系统" value="操作系统"></el-option>
            <el-option label="计算机网络" value="计算机网络"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select v-model="searchDifficulty" placeholder="选择难度" clearable @change="loadKnowledgePoints">
            <el-option label="全部难度" value=""></el-option>
            <el-option label="简单" value="EASY"></el-option>
            <el-option label="中等" value="MEDIUM"></el-option>
            <el-option label="困难" value="HARD"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索知识点"
            @keyup.enter.native="searchKnowledge">
            <el-button slot="append" icon="el-icon-search" @click="searchKnowledge"></el-button>
          </el-input>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="6" v-for="kp in knowledgePoints" :key="kp.id">
        <el-card class="knowledge-card" shadow="hover" @click.native="viewDetail(kp)">
          <div class="card-header">
            <h3>{{ kp.title }}</h3>
            <el-tag :type="getDifficultyType(kp.difficulty)" size="small">
              {{ getDifficultyText(kp.difficulty) }}
            </el-tag>
          </div>
          <div class="card-content">
            <p class="category">
              <i class="el-icon-folder-opened"></i>
              {{ kp.category }}
            </p>
            <p class="stats">
              <span>
                <i class="el-icon-view"></i>
                {{ kp.viewCount }}
              </span>
              <span style="margin-left: 15px;">
                <i class="el-icon-star-on"></i>
                重要度: {{ kp.importance }}
              </span>
            </p>
            <div class="progress-wrapper" v-if="getStudyProgress(kp.id)">
              <el-progress
                :percentage="getStudyProgress(kp.id).progress"
                :status="getStudyProgress(kp.id).progress === 100 ? 'success' : ''"
                :stroke-width="8">
              </el-progress>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 知识点详情对话框 -->
    <el-dialog
      title="知识点详情"
      :visible.sync="detailDialogVisible"
      width="70%"
      :before-close="handleClose">
      <div v-if="currentKnowledge" class="detail-content">
        <div class="detail-header">
          <h2>{{ currentKnowledge.title }}</h2>
          <div class="meta-info">
            <el-tag :type="getDifficultyType(currentKnowledge.difficulty)">
              {{ getDifficultyText(currentKnowledge.difficulty) }}
            </el-tag>
            <span class="category-tag">{{ currentKnowledge.category }}</span>
            <span class="importance-tag">重要度: {{ currentKnowledge.importance }}</span>
          </div>
        </div>

        <el-divider></el-divider>

        <div class="markdown-content" v-html="renderMarkdown(currentKnowledge.content)"></div>

        <el-divider></el-divider>

        <div class="study-actions">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="progress-section">
                <h4>学习进度</h4>
                <el-slider
                  v-model="studyProgress"
                  :marks="{ 0: '0%', 50: '50%', 100: '100%' }"
                  @change="updateProgress">
                </el-slider>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="time-section">
                <h4>本次学习时长</h4>
                <div class="time-display">{{ formatTime(studyTime) }}</div>
              </div>
            </el-col>
          </el-row>

          <el-button type="primary" @click="saveStudyRecord" style="margin-top: 20px;">
            保存学习记录
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()

export default {
  name: 'Knowledge',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      knowledgePoints: [],
      studyRecords: [],
      searchCategory: '',
      searchDifficulty: '',
      searchKeyword: '',
      detailDialogVisible: false,
      currentKnowledge: null,
      studyProgress: 0,
      studyTime: 0,
      studyTimer: null,
      studyStartTime: null
    }
  },
  mounted() {
    this.loadKnowledgePoints()
    this.loadStudyRecords()
  },
  beforeDestroy() {
    this.stopTimer()
  },
  methods: {
    loadKnowledgePoints() {
      let url = '/knowledge'

      if (this.searchCategory) {
        url = `/knowledge/category/${this.searchCategory}`
      } else if (this.searchDifficulty) {
        url = `/knowledge/difficulty/${this.searchDifficulty}`
      }

      this.$http.get(url).then(res => {
        if (res.data) {
          this.knowledgePoints = res.data
        }
      }).catch(() => {})
    },
    loadStudyRecords() {
      this.$http.get(`/knowledge/study/user/${this.user.id}`).then(res => {
        if (res.data) {
          this.studyRecords = res.data
        }
      }).catch(() => {})
    },
    searchKnowledge() {
      if (this.searchKeyword) {
        this.$http.get(`/knowledge/search?keyword=${this.searchKeyword}`).then(res => {
          if (res.data) {
            this.knowledgePoints = res.data
          }
        }).catch(() => {})
      } else {
        this.loadKnowledgePoints()
      }
    },
    viewDetail(kp) {
      this.currentKnowledge = kp
      this.detailDialogVisible = true

      // 增加浏览次数
      this.$http.post(`/knowledge/${kp.id}/view`)

      // 加载该知识点的学习记录
      this.$http.get(`/knowledge/study/${this.user.id}/${kp.id}`).then(res => {
        if (res.data) {
          this.studyProgress = res.data.progress
        } else {
          this.studyProgress = 0
        }
      }).catch(() => {
        this.studyProgress = 0
      })

      // 开始计时
      this.startTimer()
    },
    handleClose() {
      this.stopTimer()
      this.detailDialogVisible = false
      this.currentKnowledge = null
      this.studyProgress = 0
      this.studyTime = 0
    },
    startTimer() {
      this.studyStartTime = Date.now()
      this.studyTimer = setInterval(() => {
        this.studyTime = Math.floor((Date.now() - this.studyStartTime) / 1000)
      }, 1000)
    },
    stopTimer() {
      if (this.studyTimer) {
        clearInterval(this.studyTimer)
        this.studyTimer = null
      }
    },
    updateProgress(value) {
      this.studyProgress = value
    },
    saveStudyRecord() {
      const data = {
        userId: this.user.id,
        knowledgePointId: this.currentKnowledge.id,
        progress: this.studyProgress,
        studyTime: this.studyTime
      }

      this.$http.post('/knowledge/study', data).then(() => {
        this.$message.success('学习记录已保存')
        this.loadStudyRecords()
        this.handleClose()
      }).catch(() => {})
    },
    getStudyProgress(kpId) {
      return this.studyRecords.find(r => r.knowledgePointId === kpId)
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
    renderMarkdown(content) {
      return md.render(content || '')
    },
    formatTime(seconds) {
      const minutes = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${minutes}分${secs}秒`
    }
  }
}
</script>

<style scoped>
/* 现代化 Knowledge 页面 - 支持浅色/深色主题 */
.knowledge-container {
  padding: 0;
  animation: fadeInUp 0.4s ease;
}

.search-card {
  margin-bottom: 20px;
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
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

.knowledge-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: all var(--lc-transition);
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
}

.knowledge-card:hover {
  transform: translateY(-4px);
  border-color: var(--lc-primary) !important;
  box-shadow: var(--lc-shadow-lg);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: var(--lc-text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 10px;
}

.card-content {
  color: var(--lc-text-secondary);
}

.category {
  margin-bottom: 10px;
  font-size: 14px;
  color: var(--lc-text-muted);
}

.stats {
  font-size: 13px;
  color: var(--lc-text-muted);
  margin-bottom: 15px;
}

.progress-wrapper {
  margin-top: 10px;
}

/deep/ .el-progress-bar__outer {
  background: var(--lc-bg-tertiary);
}

/deep/ .el-progress-bar__inner {
  background: var(--lc-gradient-primary);
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

/* 详情对话框样式 */
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

/deep/ .el-dialog__body {
  color: var(--lc-text-primary);
}

.detail-content {
  padding: 20px;
}

.detail-header h2 {
  margin: 0 0 10px 0;
  color: var(--lc-text-primary);
}

.meta-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.category-tag,
.importance-tag {
  padding: 4px 12px;
  background: var(--lc-bg-tertiary);
  border-radius: var(--lc-radius);
  font-size: 13px;
  color: var(--lc-text-secondary);
}

.markdown-content {
  padding: 20px 0;
  line-height: 1.8;
  color: var(--lc-text-primary);
}

.markdown-content >>> h1,
.markdown-content >>> h2,
.markdown-content >>> h3 {
  margin-top: 20px;
  margin-bottom: 15px;
  color: var(--lc-text-primary);
}

.markdown-content >>> code {
  background: var(--lc-bg-tertiary);
  padding: 2px 6px;
  border-radius: 4px;
  color: var(--lc-primary);
  font-size: 14px;
}

.markdown-content >>> pre {
  background: var(--lc-bg-code);
  color: var(--lc-text-primary);
  padding: 16px;
  border-radius: var(--lc-radius);
  overflow-x: auto;
  border: 1px solid var(--lc-border);
}

.study-actions {
  margin-top: 20px;
}

.progress-section h4,
.time-section h4 {
  margin-bottom: 15px;
  color: var(--lc-text-primary);
}

.time-display {
  font-size: 32px;
  font-weight: bold;
  color: var(--lc-primary);
  text-align: center;
  padding: 20px 0;
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

/deep/ .el-slider__runway {
  background: var(--lc-bg-tertiary);
}

/deep/ .el-slider__bar {
  background: var(--lc-gradient-primary);
}

/deep/ .el-slider__button {
  border-color: var(--lc-primary);
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
