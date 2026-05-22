<template>
  <div class="interview-layout">
    <!-- 左侧：历史对话列表 -->
    <div class="history-panel">
      <div class="history-header">
        <span class="history-title">历史对话</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showNewChatDialog">
          新对话
        </el-button>
      </div>
      <div class="history-list">
        <div v-if="historyLoading" class="history-loading">
          <i class="el-icon-loading"></i>
        </div>
        <div
          v-for="session in historyList"
          :key="session.id"
          class="history-item"
          :class="{ active: currentSessionId === session.id }"
          @click="loadSession(session)">
          <div class="history-item-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="history-item-info">
            <div class="history-item-title">{{ session.position || 'AI问答' }}</div>
            <div class="history-item-date">{{ formatDate(session.startTime || session.createdAt) }}</div>
          </div>
        </div>
        <div v-if="!historyLoading && historyList.length === 0" class="history-empty">
          <p>暂无历史对话</p>
        </div>
      </div>
    </div>

    <!-- 右侧：对话区 -->
    <div class="chat-panel">
      <!-- 未开始：引导页 -->
      <div v-if="!sessionStarted" class="chat-welcome">
        <div class="welcome-icon"><i class="el-icon-service"></i></div>
        <h2>AI模拟问答</h2>
        <p>选择应聘岗位，开始您的AI模拟面试练习</p>
        <el-button type="primary" size="medium" icon="el-icon-plus" @click="showNewChatDialog">
          开始新对话
        </el-button>
      </div>

      <!-- 查看历史对话（只读） -->
      <div v-else-if="viewingHistory" class="chat-main">
        <div class="chat-header">
          <div class="chat-title">
            <i class="el-icon-chat-dot-round"></i>
            {{ currentPosition }} · 历史记录
          </div>
          <el-button size="small" @click="closeHistory">关闭</el-button>
        </div>
        <div class="chat-container" ref="chatContainer">
          <div v-for="(msg, idx) in messages" :key="idx"
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
      </div>

      <!-- 进行中的对话 -->
      <div v-else class="chat-main">
        <div class="chat-header">
          <div class="chat-title">
            <i class="el-icon-service"></i>
            {{ currentPosition }}
          </div>
          <el-button type="danger" size="small" @click="endInterview">结束对话</el-button>
        </div>
        <div class="chat-container" ref="chatContainer">
          <div v-if="messages.length === 0" class="chat-placeholder">
            <i class="el-icon-chat-dot-square"></i>
            <p>AI助手已准备就绪，请输入消息开始对话</p>
          </div>
          <div v-for="(msg, idx) in messages" :key="idx"
               :class="['message', msg.type === 'user' ? 'user-message' : 'ai-message']">
            <div class="message-avatar">
              <i :class="msg.type === 'user' ? 'el-icon-user' : 'el-icon-service'"></i>
            </div>
            <div class="message-content">
              <div class="message-bubble">{{ msg.content }}</div>
              <div class="message-time">{{ formatTime(msg.time) }}</div>
            </div>
          </div>
          <div v-if="sending" class="message ai-message">
            <div class="message-avatar"><i class="el-icon-service"></i></div>
            <div class="message-content">
              <div class="message-bubble typing">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>
        <div class="chat-input">
          <el-input
            v-model="inputMessage"
            placeholder="输入消息，Enter发送..."
            @keyup.enter.native="sendMessage"
            :disabled="sending">
          </el-input>
          <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
        </div>
      </div>
    </div>

    <!-- 新对话弹窗 -->
    <el-dialog title="开始新对话" :visible.sync="newChatDialogVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="应聘岗位">
          <el-select v-model="newChatPosition" placeholder="请选择岗位" style="width: 100%">
            <el-option label="Java开发工程师" value="Java开发工程师"></el-option>
            <el-option label="前端开发工程师" value="前端开发工程师"></el-option>
            <el-option label="测试工程师" value="测试工程师"></el-option>
            <el-option label="产品经理" value="产品经理"></el-option>
            <el-option label="后端开发工程师" value="后端开发工程师"></el-option>
            <el-option label="全栈开发工程师" value="全栈开发工程师"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="newChatDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="startInterview" :loading="starting">开始对话</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Interview',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      sessionStarted: false,
      viewingHistory: false,
      currentSessionId: null,
      currentPosition: '',
      messages: [],
      inputMessage: '',
      sending: false,
      starting: false,
      newChatDialogVisible: false,
      newChatPosition: '',
      historyList: [],
      historyLoading: false
    }
  },
  mounted() {
    this.loadHistory()
  },
  methods: {
    loadHistory() {
      this.historyLoading = true
      this.$http.get(`/interview/history/${this.user.id}`).then(res => {
        this.historyList = res.data || []
      }).catch(() => {
        this.historyList = []
      }).finally(() => {
        this.historyLoading = false
      })
    },
    showNewChatDialog() {
      this.newChatPosition = ''
      this.newChatDialogVisible = true
    },
    startInterview() {
      if (!this.newChatPosition) {
        this.$message.warning('请选择应聘岗位')
        return
      }
      this.starting = true
      this.$http.post('/interview/start', {
        userId: this.user.id,
        position: this.newChatPosition
      }).then(res => {
        if (res.data) {
          this.currentSessionId = res.data.id
          this.currentPosition = this.newChatPosition
          this.messages = []
          this.sessionStarted = true
          this.viewingHistory = false
          this.newChatDialogVisible = false
          this.addMessage('ai', `您好！欢迎参加${this.newChatPosition}的模拟面试练习。我是您的AI面试助手，请先做一下自我介绍。`)
          this.loadHistory()
        }
      }).catch(() => {
        this.$message.error('开始对话失败，请稍后重试')
      }).finally(() => {
        this.starting = false
      })
    },
    sendMessage() {
      if (!this.inputMessage.trim() || this.sending) return
      const userMessage = this.inputMessage.trim()
      this.addMessage('user', userMessage)
      this.inputMessage = ''
      this.sending = true
      this.$http.post('/interview/chat', {
        sessionId: this.currentSessionId,
        message: userMessage
      }).then(res => {
        if (res.data && res.data.aiReply) {
          this.addMessage('ai', res.data.aiReply)
        }
      }).catch(() => {
        this.$message.error('发送失败，请重试')
      }).finally(() => {
        this.sending = false
      })
    },
    endInterview() {
      this.$confirm('确定要结束本次对话吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post(`/interview/end/${this.currentSessionId}`).then(() => {
          this.$message.success('对话已结束')
          this.sessionStarted = false
          this.currentSessionId = null
          this.messages = []
          this.loadHistory()
        }).catch(() => {
          this.$message.error('结束对话失败')
        })
      }).catch(() => {})
    },
    loadSession(session) {
      this.currentSessionId = session.id
      this.currentPosition = session.position || 'AI问答'
      this.sessionStarted = true
      this.viewingHistory = true
      this.messages = []
      this.$http.get(`/interview/session/${session.id}`).then(res => {
        if (res.data && res.data.conversation) {
          try {
            const parsed = JSON.parse(res.data.conversation)
            if (Array.isArray(parsed)) {
              this.messages = parsed.map(m => ({
                type: (m.role || m.type || '').toLowerCase() === 'user' ? 'user' : 'ai',
                content: m.message || m.content || '',
                time: m.timestamp || m.time || null
              }))
            }
          } catch (e) {
            this.messages = []
          }
        }
      }).catch(() => {})
    },
    closeHistory() {
      this.sessionStarted = false
      this.viewingHistory = false
      this.currentSessionId = null
      this.messages = []
    },
    addMessage(type, content) {
      this.messages.push({ type, content, time: new Date() })
      this.$nextTick(() => {
        const c = this.$refs.chatContainer
        if (c) c.scrollTop = c.scrollHeight
      })
    },
    formatTime(time) {
      if (!time) return ''
      let d
      if (Array.isArray(time)) {
        d = new Date(time[0], time[1] - 1, time[2], time[3] || 0, time[4] || 0, time[5] || 0)
      } else {
        d = new Date(time)
      }
      if (isNaN(d.getTime())) return ''
      return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      let d
      if (Array.isArray(dateStr)) {
        d = new Date(dateStr[0], dateStr[1] - 1, dateStr[2], dateStr[3] || 0, dateStr[4] || 0, dateStr[5] || 0)
      } else {
        d = new Date(dateStr)
      }
      if (isNaN(d.getTime())) return ''
      return `${d.getMonth() + 1}/${d.getDate()} ${d.getHours()}:${String(d.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
/* 新布局：左侧历史 + 右侧对话 */
.interview-layout {
  display: flex;
  height: calc(100vh - 120px);
  gap: 0;
  background: var(--lc-bg-primary);
  border-radius: var(--lc-radius-xl);
  overflow: hidden;
  border: 1px solid var(--lc-border);
}

/* 左侧历史面板 */
.history-panel {
  width: 220px;
  flex-shrink: 0;
  border-right: 1px solid var(--lc-border);
  background: var(--lc-bg-card);
  display: flex;
  flex-direction: column;
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid var(--lc-border);
}

.history-title {
  font-weight: 600;
  font-size: 14px;
  color: var(--lc-text-primary);
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.history-loading {
  text-align: center;
  padding: 20px;
  color: var(--lc-text-muted);
}

.history-empty {
  text-align: center;
  padding: 30px 10px;
  color: var(--lc-text-muted);
  font-size: 13px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: var(--lc-radius-lg);
  cursor: pointer;
  transition: background var(--lc-transition);
}

.history-item:hover {
  background: var(--lc-bg-hover);
}

.history-item.active {
  background: var(--lc-primary-bg, rgba(255, 107, 0, 0.1));
}

.history-item-icon {
  color: var(--lc-primary);
  font-size: 16px;
  flex-shrink: 0;
}

.history-item-info {
  flex: 1;
  overflow: hidden;
}

.history-item-title {
  font-size: 13px;
  color: var(--lc-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.history-item-date {
  font-size: 11px;
  color: var(--lc-text-muted);
  margin-top: 2px;
}

/* 右侧对话面板 */
.chat-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 欢迎页 */
.chat-welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 40px;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--lc-gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #fff;
}

.chat-welcome h2 {
  margin: 0;
  font-size: 22px;
  color: var(--lc-text-primary);
}

.chat-welcome p {
  margin: 0;
  color: var(--lc-text-muted);
}

/* 对话主区域 */
.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  border-bottom: 1px solid var(--lc-border);
  background: var(--lc-bg-card);
  flex-shrink: 0;
}

.chat-title {
  font-weight: 600;
  font-size: 15px;
  color: var(--lc-text-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  background: var(--lc-bg-primary);
}

.chat-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--lc-text-muted);
  gap: 12px;
  font-size: 13px;
}

.chat-placeholder i {
  font-size: 40px;
  opacity: 0.4;
}

.message {
  display: flex;
  margin-bottom: 20px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--lc-radius-lg);
  background: var(--lc-gradient-purple);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: var(--lc-gradient-primary);
}

.message-content {
  margin: 0 10px;
  max-width: 72%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: var(--lc-radius-xl);
  background: var(--lc-bg-card);
  color: var(--lc-text-primary);
  line-height: 1.7;
  word-break: break-word;
  border: 1px solid var(--lc-border);
  font-size: 14px;
}

.user-message .message-bubble {
  background: var(--lc-gradient-primary);
  color: #fff;
  border: none;
}

.message-time {
  font-size: 11px;
  color: var(--lc-text-muted);
  margin-top: 5px;
}

/* 打字中动画 */
.typing {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 14px 18px;
}

.typing span {
  width: 8px;
  height: 8px;
  background: var(--lc-text-muted);
  border-radius: 50%;
  animation: typing-bounce 1.2s infinite;
}

.typing span:nth-child(2) { animation-delay: 0.2s; }
.typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing-bounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.4; }
  30% { transform: translateY(-6px); opacity: 1; }
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid var(--lc-border);
  background: var(--lc-bg-card);
  flex-shrink: 0;
}

.chat-input /deep/ .el-input__inner {
  background: var(--lc-bg-input);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
  border-radius: var(--lc-radius-lg);
  height: 44px;
}

.chat-input /deep/ .el-input__inner:focus {
  border-color: var(--lc-primary);
}

/deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: #fff;
  font-weight: 600;
}

/deep/ .el-button--danger {
  background: transparent;
  border: 1px solid var(--lc-danger);
  color: var(--lc-danger);
}

/deep/ .el-button--danger:hover {
  background: var(--lc-danger);
  color: #fff;
}

/deep/ .el-form-item__label {
  color: var(--lc-text-secondary);
}

/deep/ .el-select .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}
</style>
