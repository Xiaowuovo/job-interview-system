<template>
  <div class="interview">
    <el-card v-if="!sessionStarted">
      <h2>AI模拟面试</h2>
      <p>AI面试官将根据您选择的岗位进行专业的模拟面试</p>
      <el-form :model="form" label-width="100px" style="max-width: 500px; margin: 30px auto;">
        <el-form-item label="应聘岗位">
          <el-select v-model="form.position" placeholder="请选择岗位">
            <el-option label="Java开发工程师" value="Java开发工程师"></el-option>
            <el-option label="前端开发工程师" value="前端开发工程师"></el-option>
            <el-option label="测试工程师" value="测试工程师"></el-option>
            <el-option label="产品经理" value="产品经理"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="startInterview">开始面试</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-else>
      <div class="chat-header">
        <h3>AI模拟面试 - {{ form.position }}</h3>
        <div class="interview-info">
          <el-tag type="info" size="small">
            <i class="el-icon-time"></i> {{ formatDuration(interviewDuration) }}
          </el-tag>
          <el-tag type="success" size="small">
            <i class="el-icon-chat-dot-round"></i> {{ questionCount }}/{{ maxQuestions }} 问题
          </el-tag>
          <el-button type="danger" size="small" @click="endInterview">结束面试</el-button>
        </div>
      </div>

      <div class="chat-container" ref="chatContainer">
        <div v-for="(msg, index) in messages" :key="index"
             :class="['message', msg.type === 'user' ? 'user-message' : 'ai-message']">
          <div class="message-avatar">
            <i :class="msg.type === 'user' ? 'el-icon-user' : 'el-icon-service'"></i>
          </div>
          <div class="message-content">
            <div class="message-bubble">{{ msg.content }}</div>
            <div class="message-time">{{ formatTime(msg.time) }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          placeholder="请输入您的回答..."
          @keyup.enter.native="sendMessage"
          :disabled="sending">
        </el-input>
        <el-button type="primary" @click="sendMessage" :loading="sending">
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Interview',
  data() {
    return {
      sessionStarted: false,
      sessionId: null,
      sessionEnded: false, // 防止重复结束
      form: {
        position: ''
      },
      messages: [],
      inputMessage: '',
      sending: false,
      questionCount: 0, // 已问问题数
      maxQuestions: 8, // 最大问题数
      interviewTimer: null, // 面试计时器
      interviewDuration: 0, // 面试时长(秒)
      maxDuration: 30 * 60 // 最大时长30分钟
    }
  },
  methods: {
    startInterview() {
      if (!this.form.position) {
        this.$message.warning('请选择应聘岗位')
        return
      }

      const user = JSON.parse(localStorage.getItem('user'))
      this.$http.post('/interview/start', {
        userId: user.id,
        position: this.form.position
      }).then(res => {
        if (res.data) {
          this.sessionId = res.data.id
          this.sessionStarted = true
          this.sessionEnded = false
          this.questionCount = 1 // 第一个问题
          this.interviewDuration = 0
          this.addMessage('ai', `您好！欢迎参加${this.form.position}的面试。我是您的AI面试官。首先，请做一下自我介绍。`)
          
          // 启动面试计时器
          this.startInterviewTimer()
        }
      }).catch(err => {
        console.error('开始面试失败:', err)
        this.$message.error('开始面试失败，请稍后重试')
      })
    },
    startInterviewTimer() {
      this.interviewTimer = setInterval(() => {
        this.interviewDuration++
        // 超过最大时长自动结束
        if (this.interviewDuration >= this.maxDuration) {
          this.$message.warning('面试时间已到，自动结束面试')
          this.autoEndInterview()
        }
      }, 1000)
    },
    sendMessage() {
      if (!this.inputMessage.trim() || this.sending || this.sessionEnded) return

      const userMessage = this.inputMessage
      this.addMessage('user', userMessage)
      this.inputMessage = ''
      this.sending = true

      this.$http.post('/interview/chat', {
        sessionId: this.sessionId,
        message: userMessage
      }).then(res => {
        if (res.data) {
          this.questionCount++
          
          // 检查是否达到最大问题数
          if (this.questionCount >= this.maxQuestions) {
            this.addMessage('ai', res.data.aiReply + '\n\n感谢您的参与，本次面试即将结束。')
            setTimeout(() => {
              this.autoEndInterview()
            }, 2000)
          } else {
            this.addMessage('ai', res.data.aiReply)
          }
        }
      }).finally(() => {
        this.sending = false
      })
    },
    addMessage(type, content) {
      this.messages.push({
        type,
        content,
        time: new Date()
      })
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },
    scrollToBottom() {
      const container = this.$refs.chatContainer
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    },
    formatTime(time) {
      return new Date(time).toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatDuration(seconds) {
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
    },
    endInterview() {
      if (this.sessionEnded) return
      
      this.$confirm('确定要结束面试吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.doEndInterview()
      }).catch(() => {})
    },
    autoEndInterview() {
      if (this.sessionEnded) return
      this.doEndInterview()
    },
    doEndInterview() {
      if (this.sessionEnded) return
      this.sessionEnded = true
      
      // 停止计时器
      if (this.interviewTimer) {
        clearInterval(this.interviewTimer)
        this.interviewTimer = null
      }
      
      // 根据问答数量和时长计算分数
      const baseScore = 60
      const questionBonus = Math.min(this.questionCount * 3, 20)
      const durationBonus = Math.min(Math.floor(this.interviewDuration / 60) * 2, 10)
      const randomBonus = Math.floor(Math.random() * 10)
      const score = Math.min(baseScore + questionBonus + durationBonus + randomBonus, 100)
      
      const feedback = this.generateFeedback(score)

      this.$http.post(`/interview/end/${this.sessionId}`, {
        conversation: JSON.stringify(this.messages),
        score: score,
        feedback: feedback,
        duration: Math.floor(this.interviewDuration / 60),
        questionCount: this.questionCount
      }).then(() => {
        this.$message.success(`面试结束！您的得分: ${score}分`)
        
        // 发送面试完成事件
        this.$bus.$emit(this.$events.INTERVIEW_COMPLETED, {
          score,
          questionCount: this.questionCount,
          duration: this.interviewDuration
        })
        
        this.$router.push('/home/records')
      }).catch(() => {
        this.sessionEnded = false // 允许重试
        this.$message.error('保存面试记录失败')
      })
    },
    generateFeedback(score) {
      if (score >= 85) {
        return '表现优秀！回答问题思路清晰，专业知识扎实，沟通能力强。'
      } else if (score >= 70) {
        return '表现良好。基本能够回答问题，但在某些专业知识点上还需要加强。'
      } else {
        return '需要继续努力。建议加强专业知识学习，多做面试练习。'
      }
    }
  },
  beforeDestroy() {
    // 清理计时器
    if (this.interviewTimer) {
      clearInterval(this.interviewTimer)
      this.interviewTimer = null
    }
  }
}
</script>

<style scoped>
/* 现代化 Interview 页面 - 支持浅色/深色主题 */
.interview {
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

/* 表单样式 */
/deep/ .el-form-item__label {
  color: var(--lc-text-secondary);
}

/deep/ .el-select .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-select-dropdown {
  background: var(--lc-bg-card);
  border-color: var(--lc-border);
}

/deep/ .el-select-dropdown__item {
  color: var(--lc-text-primary);
}

/deep/ .el-select-dropdown__item.hover,
/deep/ .el-select-dropdown__item:hover {
  background: var(--lc-bg-hover);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--lc-border);
}

.chat-header h3 {
  color: var(--lc-text-primary);
  font-size: 18px;
  margin: 0;
}

.interview-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.interview-info /deep/ .el-tag {
  background: var(--lc-bg-tertiary);
  border-color: var(--lc-border);
  color: var(--lc-text-secondary);
}

.interview-info /deep/ .el-tag--success {
  background: var(--lc-success-bg);
  border-color: transparent;
  color: var(--lc-success);
}

.chat-container {
  height: 500px;
  overflow-y: auto;
  padding: 24px;
  background: var(--lc-bg-primary);
  border-radius: var(--lc-radius-lg);
  margin-bottom: 20px;
  border: 1px solid var(--lc-border);
}

.message {
  display: flex;
  margin-bottom: 24px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--lc-radius-lg);
  background: var(--lc-gradient-purple);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: var(--lc-gradient-primary);
  color: var(--lc-text-inverse);
}

.message-content {
  margin: 0 12px;
  max-width: 70%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 14px 18px;
  border-radius: var(--lc-radius-xl);
  background: var(--lc-bg-card);
  color: var(--lc-text-primary);
  line-height: 1.7;
  word-break: break-word;
  border: 1px solid var(--lc-border);
}

.user-message .message-bubble {
  background: var(--lc-gradient-primary);
  color: var(--lc-text-inverse);
  border: none;
}

.message-time {
  font-size: 11px;
  color: var(--lc-text-muted);
  margin-top: 6px;
}

.chat-input {
  display: flex;
  gap: 12px;
}

.chat-input /deep/ .el-input__inner {
  background: var(--lc-bg-input);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
  border-radius: var(--lc-radius-lg);
  height: 48px;
}

.chat-input /deep/ .el-input__inner:focus {
  border-color: var(--lc-primary);
}

.chat-input /deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: var(--lc-text-inverse);
  font-weight: 600;
  border-radius: var(--lc-radius-lg);
  height: 48px;
  padding: 0 24px;
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

/deep/ .el-button--danger:hover {
  background: var(--lc-danger);
  color: #fff;
}

/deep/ .el-button--default {
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-button--default:hover {
  border-color: var(--lc-primary);
  color: var(--lc-primary);
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
