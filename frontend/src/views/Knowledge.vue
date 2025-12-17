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
        if (res.data.code === 200) {
          this.knowledgePoints = res.data.data
        }
      })
    },
    loadStudyRecords() {
      this.$http.get(`/knowledge/study/user/${this.user.id}`).then(res => {
        if (res.data.code === 200) {
          this.studyRecords = res.data.data
        }
      })
    },
    searchKnowledge() {
      if (this.searchKeyword) {
        this.$http.get(`/knowledge/search?keyword=${this.searchKeyword}`).then(res => {
          if (res.data.code === 200) {
            this.knowledgePoints = res.data.data
          }
        })
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
        if (res.data.code === 200) {
          this.studyProgress = res.data.data.progress
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

      this.$http.post('/knowledge/study', data).then(res => {
        if (res.data.code === 200) {
          this.$message.success('学习记录已保存')
          this.loadStudyRecords()
          this.handleClose()
        }
      })
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
.knowledge-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.knowledge-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.knowledge-card:hover {
  transform: translateY(-5px);
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
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  margin-right: 10px;
}

.card-content {
  color: #606266;
}

.category {
  margin-bottom: 10px;
  font-size: 14px;
  color: #909399;
}

.stats {
  font-size: 13px;
  color: #909399;
  margin-bottom: 15px;
}

.progress-wrapper {
  margin-top: 10px;
}

/* 详情对话框样式 */
.detail-content {
  padding: 20px;
}

.detail-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.meta-info {
  display: flex;
  gap: 10px;
  align-items: center;
}

.category-tag,
.importance-tag {
  padding: 4px 12px;
  background: #f0f2f5;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
}

.markdown-content {
  padding: 20px 0;
  line-height: 1.8;
  color: #303133;
}

.markdown-content >>> h1,
.markdown-content >>> h2,
.markdown-content >>> h3 {
  margin-top: 20px;
  margin-bottom: 15px;
  color: #303133;
}

.markdown-content >>> code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e83e8c;
  font-size: 14px;
}

.markdown-content >>> pre {
  background: #282c34;
  color: #abb2bf;
  padding: 15px;
  border-radius: 5px;
  overflow-x: auto;
}

.study-actions {
  margin-top: 20px;
}

.progress-section h4,
.time-section h4 {
  margin-bottom: 15px;
  color: #303133;
}

.time-display {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  padding: 20px 0;
}
</style>
