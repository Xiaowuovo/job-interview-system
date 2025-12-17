<template>
  <div class="tutorial-detail">
    <el-card v-if="tutorial">
      <div slot="header" class="header">
        <el-button icon="el-icon-arrow-left" @click="$router.back()">返回</el-button>
        <el-tag>{{ tutorial.category }}</el-tag>
      </div>

      <h1>{{ tutorial.title }}</h1>

      <div class="meta">
        <span><i class="el-icon-view"></i> 浏览 {{ tutorial.viewCount }}</span>
        <span><i class="el-icon-time"></i> {{ formatDate(tutorial.createdAt) }}</span>
      </div>

      <el-divider></el-divider>

      <div class="content" v-html="formatContent(tutorial.content)"></div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'TutorialDetail',
  data() {
    return {
      tutorial: null
    }
  },
  created() {
    this.loadTutorial()
  },
  methods: {
    loadTutorial() {
      const id = this.$route.params.id
      this.$http.get(`/tutorials/${id}`).then(res => {
        if (res.data.code === 200) {
          this.tutorial = res.data.data
        }
      })
    },
    formatContent(content) {
      return content.replace(/\n/g, '<br>')
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style scoped>
.tutorial-detail {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h1 {
  margin: 20px 0;
  color: #303133;
}

.meta {
  color: #909399;
  font-size: 14px;
}

.meta span {
  margin-right: 20px;
}

.content {
  line-height: 2;
  color: #606266;
  font-size: 16px;
}
</style>
