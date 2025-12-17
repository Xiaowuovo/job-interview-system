<template>
  <div class="interview-report">
    <el-page-header @back="goBack" content="面试报告详情"></el-page-header>

    <div v-if="loading" class="loading-container">
      <i class="el-icon-loading"></i>
      <p>加载中...</p>
    </div>

    <div v-else-if="session" class="report-content">
      <!-- 面试基本信息 -->
      <el-card shadow="hover" class="info-card">
        <div slot="header">
          <i class="el-icon-document"></i> 面试概览
        </div>
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="info-item">
              <div class="label">面试类型</div>
              <div class="value">{{ session.type }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <div class="label">面试时长</div>
              <div class="value">{{ session.duration }}分钟</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <div class="label">问题数量</div>
              <div class="value">{{ session.questionCount }}题</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <div class="label">面试时间</div>
              <div class="value">{{ formatDate(session.createdAt) }}</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 综合评分 -->
      <el-card shadow="hover" class="score-card">
        <div slot="header">
          <i class="el-icon-medal"></i> 综合评分
        </div>
        <div class="score-container">
          <div class="total-score">
            <div class="score-circle" :class="getScoreLevel(session.avgScore)">
              <span class="score-value">{{ session.avgScore }}</span>
              <span class="score-label">综合得分</span>
            </div>
            <div class="score-level">{{ getScoreLevelText(session.avgScore) }}</div>
          </div>

          <div class="score-breakdown">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="score-item">
                  <div class="score-title">技术能力</div>
                  <el-progress
                    :percentage="session.technicalScore"
                    :color="getScoreColor(session.technicalScore)">
                  </el-progress>
                  <div class="score-text">{{ session.technicalScore }}/100</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="score-item">
                  <div class="score-title">逻辑思维</div>
                  <el-progress
                    :percentage="session.logicalScore"
                    :color="getScoreColor(session.logicalScore)">
                  </el-progress>
                  <div class="score-text">{{ session.logicalScore }}/100</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="score-item">
                  <div class="score-title">表达能力</div>
                  <el-progress
                    :percentage="session.expressionScore"
                    :color="getScoreColor(session.expressionScore)">
                  </el-progress>
                  <div class="score-text">{{ session.expressionScore }}/100</div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </el-card>

      <!-- 面试对话记录 -->
      <el-card shadow="hover" class="conversation-card">
        <div slot="header">
          <i class="el-icon-chat-line-round"></i> 面试对话记录
        </div>
        <div class="conversation-list">
          <div
            v-for="(msg, index) in conversationHistory"
            :key="index"
            class="message-item"
            :class="msg.role">
            <div class="message-header">
              <div class="avatar">
                <i :class="msg.role === 'interviewer' ? 'el-icon-user' : 'el-icon-user-solid'"></i>
              </div>
              <div class="sender">{{ msg.role === 'interviewer' ? 'AI面试官' : '我' }}</div>
              <div class="time">{{ msg.timestamp }}</div>
            </div>
            <div class="message-content">
              {{ msg.content }}
            </div>
            <!-- 如果是面试官的问题，显示AI评分 -->
            <div v-if="msg.role === 'interviewer' && msg.score" class="message-score">
              <el-tag :type="getScoreTagType(msg.score)" size="small">
                AI评分: {{ msg.score }}/100
              </el-tag>
              <span class="score-comment">{{ msg.comment }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- AI反馈与建议 -->
      <el-card shadow="hover" class="feedback-card">
        <div slot="header">
          <i class="el-icon-warning-outline"></i> AI反馈与建议
        </div>
        <div class="feedback-content">
          <div class="feedback-section">
            <h4><i class="el-icon-success"></i> 优势表现</h4>
            <ul>
              <li v-for="(item, index) in strengths" :key="index">{{ item }}</li>
            </ul>
          </div>
          <el-divider></el-divider>
          <div class="feedback-section">
            <h4><i class="el-icon-warning"></i> 需要改进</h4>
            <ul>
              <li v-for="(item, index) in weaknesses" :key="index">{{ item }}</li>
            </ul>
          </div>
          <el-divider></el-divider>
          <div class="feedback-section">
            <h4><i class="el-icon-thumb"></i> 学习建议</h4>
            <ul>
              <li v-for="(item, index) in suggestions" :key="index">{{ item }}</li>
            </ul>
          </div>
        </div>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="exportReport">
          <i class="el-icon-download"></i> 导出报告
        </el-button>
        <el-button @click="startNewInterview">
          <i class="el-icon-refresh-right"></i> 再来一次
        </el-button>
        <el-button @click="goBack">
          <i class="el-icon-back"></i> 返回列表
        </el-button>
      </div>
    </div>

    <div v-else class="empty-state">
      <i class="el-icon-document-delete" style="font-size: 80px; color: #DCDFE6;"></i>
      <p>未找到面试记录</p>
      <el-button type="primary" @click="goBack">返回</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'InterviewReport',
  data() {
    return {
      sessionId: null,
      session: null,
      loading: false,
      conversationHistory: [],
      strengths: [],
      weaknesses: [],
      suggestions: []
    }
  },
  mounted() {
    this.sessionId = this.$route.params.id
    this.loadReport()
  },
  methods: {
    loadReport() {
      this.loading = true
      this.$http.get(`/interview/session/${this.sessionId}`).then(res => {
        if (res.data.code === 200) {
          this.session = res.data.data
          this.parseConversation()
          this.generateFeedback()
        }
      }).finally(() => {
        this.loading = false
      })
    },
    parseConversation() {
      // 解析对话历史
      if (!this.session.conversationHistory) return

      try {
        const history = JSON.parse(this.session.conversationHistory)
        this.conversationHistory = history.map(msg => {
          return {
            role: msg.role || 'interviewer',
            content: msg.content || msg.message || '',
            timestamp: msg.timestamp || new Date().toLocaleTimeString(),
            score: msg.score || null,
            comment: msg.comment || ''
          }
        })
      } catch (e) {
        console.error('解析对话历史失败', e)
      }
    },
    generateFeedback() {
      // 根据分数生成反馈
      const avg = this.session.avgScore

      // 优势
      this.strengths = []
      if (this.session.technicalScore >= 80) {
        this.strengths.push('技术基础扎实，对核心概念理解深入')
      }
      if (this.session.logicalScore >= 80) {
        this.strengths.push('逻辑思维清晰，问题分析有条理')
      }
      if (this.session.expressionScore >= 80) {
        this.strengths.push('表达能力出色，沟通流畅自然')
      }
      if (this.session.duration >= 15) {
        this.strengths.push('面试时长充足，回答详细完整')
      }
      if (this.strengths.length === 0) {
        this.strengths.push('积极参与面试，态度认真')
      }

      // 需要改进
      this.weaknesses = []
      if (this.session.technicalScore < 70) {
        this.weaknesses.push('技术知识掌握不够全面，建议加强基础学习')
      }
      if (this.session.logicalScore < 70) {
        this.weaknesses.push('逻辑表达有待提升，建议多练习结构化思维')
      }
      if (this.session.expressionScore < 70) {
        this.weaknesses.push('沟通表达需要改进，建议多进行口语练习')
      }
      if (this.session.questionCount < 3) {
        this.weaknesses.push('面试问题数量较少，建议增加练习时长')
      }

      // 学习建议
      this.suggestions = []
      if (avg >= 85) {
        this.suggestions.push('整体表现优秀！建议挑战更高难度的面试')
        this.suggestions.push('可以尝试系统设计类问题，提升架构思维')
      } else if (avg >= 70) {
        this.suggestions.push('表现良好，继续保持！建议针对薄弱环节专项练习')
        this.suggestions.push('多阅读技术博客和开源项目源码')
      } else {
        this.suggestions.push('需要加强基础知识学习，建议系统复习核心概念')
        this.suggestions.push('多做题目练习，积累实战经验')
        this.suggestions.push('观看优秀面试视频，学习表达技巧')
      }
      this.suggestions.push('定期进行模拟面试，保持面试状态')
    },
    getScoreLevel(score) {
      if (score >= 85) return 'excellent'
      if (score >= 70) return 'good'
      if (score >= 60) return 'average'
      return 'poor'
    },
    getScoreLevelText(score) {
      if (score >= 85) return '优秀'
      if (score >= 70) return '良好'
      if (score >= 60) return '及格'
      return '需努力'
    },
    getScoreColor(score) {
      if (score >= 80) return '#67C23A'
      if (score >= 60) return '#E6A23C'
      return '#F56C6C'
    },
    getScoreTagType(score) {
      if (score >= 80) return 'success'
      if (score >= 60) return 'warning'
      return 'danger'
    },
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    exportReport() {
      this.$message.info('报告导出功能开发中...')
    },
    startNewInterview() {
      this.$router.push('/home/interview')
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.interview-report {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.loading-container, .empty-state {
  text-align: center;
  padding: 100px 0;
  color: #909399;
}

.loading-container i {
  font-size: 60px;
  animation: rotating 2s linear infinite;
}

@keyframes rotating {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.report-content {
  margin-top: 20px;
}

.el-card {
  margin-bottom: 20px;
}

/* 信息卡片 */
.info-item {
  text-align: center;
  padding: 10px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-item .value {
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

/* 评分卡片 */
.score-container {
  display: flex;
  gap: 40px;
}

.total-score {
  flex-shrink: 0;
  text-align: center;
}

.score-circle {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  position: relative;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.score-circle.excellent {
  background: linear-gradient(135deg, #67C23A 0%, #85CE61 100%);
}

.score-circle.good {
  background: linear-gradient(135deg, #409EFF 0%, #66B1FF 100%);
}

.score-circle.average {
  background: linear-gradient(135deg, #E6A23C 0%, #F6C66B 100%);
}

.score-circle.poor {
  background: linear-gradient(135deg, #F56C6C 0%, #F78989 100%);
}

.score-value {
  font-size: 48px;
  font-weight: 700;
  color: white;
  line-height: 1;
}

.score-label {
  font-size: 14px;
  color: rgba(255,255,255,0.9);
  margin-top: 5px;
}

.score-level {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.score-breakdown {
  flex: 1;
}

.score-item {
  text-align: center;
  padding: 15px;
}

.score-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.score-text {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

/* 对话记录 */
.conversation-list {
  max-height: 600px;
  overflow-y: auto;
}

.message-item {
  margin-bottom: 20px;
  padding: 15px;
  border-radius: 8px;
  background: #f9fafc;
}

.message-item.interviewer {
  border-left: 3px solid #409EFF;
}

.message-item.candidate {
  border-left: 3px solid #67C23A;
  background: #f0f9ff;
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
}

.message-item.candidate .avatar {
  background: #67C23A;
}

.sender {
  font-weight: 600;
  color: #303133;
  margin-right: 10px;
}

.time {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.message-content {
  color: #606266;
  line-height: 1.8;
  padding-left: 42px;
}

.message-score {
  margin-top: 10px;
  padding-left: 42px;
}

.score-comment {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

/* 反馈卡片 */
.feedback-section h4 {
  color: #303133;
  font-size: 16px;
  margin-bottom: 15px;
}

.feedback-section h4 i {
  margin-right: 5px;
}

.feedback-section ul {
  padding-left: 20px;
}

.feedback-section li {
  color: #606266;
  line-height: 2;
  list-style-type: disc;
}

/* 操作按钮 */
.action-buttons {
  text-align: center;
  margin-top: 30px;
  padding-bottom: 20px;
}
</style>
