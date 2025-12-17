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
        <el-button type="danger" size="small" @click="endInterview">结束面试</el-button>
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
      form: {
        position: ''
      },
      messages: [],
      inputMessage: '',
      sending: false
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
        if (res.data.code === 200) {
          this.sessionId = res.data.data.id
          this.sessionStarted = true
          this.addMessage('ai', `您好！欢迎参加${this.form.position}的面试。我是您的AI面试官。首先，请做一下自我介绍。`)
        }
      })
    },
    sendMessage() {
      if (!this.inputMessage.trim() || this.sending) return

      const userMessage = this.inputMessage
      this.addMessage('user', userMessage)
      this.inputMessage = ''
      this.sending = true

      this.$http.post('/interview/chat', {
        sessionId: this.sessionId,
        message: userMessage
      }).then(res => {
        if (res.data.code === 200) {
          this.addMessage('ai', res.data.data.aiReply)
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
    endInterview() {
      this.$confirm('确定要结束面试吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const score = Math.floor(Math.random() * 20) + 70
        const feedback = this.generateFeedback(score)

        this.$http.post(`/interview/end/${this.sessionId}`, {
          conversation: JSON.stringify(this.messages),
          score: score,
          feedback: feedback
        }).then(() => {
          this.$message.success(`面试结束！您的得分: ${score}分`)
          this.$router.push('/home/records')
        })
      }).catch(() => {})
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
  }
}
</script>

<style scoped>
.interview {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.chat-container {
  height: 500px;
  overflow-y: auto;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 20px;
}

.message {
  display: flex;
  margin-bottom: 20px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: #67C23A;
}

.message-content {
  margin: 0 10px;
  max-width: 60%;
}

.user-message .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 8px;
  background: white;
  line-height: 1.6;
  word-break: break-word;
}

.user-message .message-bubble {
  background: #409EFF;
  color: white;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.chat-input {
  display: flex;
  gap: 10px;
}

.chat-input .el-input {
  flex: 1;
}
</style>
