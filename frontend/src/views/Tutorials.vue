<template>
  <div class="tutorials">
    <el-card>
      <div slot="header" class="header">
        <span>面试教程</span>
        <el-button type="primary" size="small" @click="showAddDialog" v-if="isTeacher">
          添加教程
        </el-button>
      </div>

      <!-- 搜索框 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索教程标题或内容..."
          prefix-icon="el-icon-search"
          clearable
          @input="handleSearch"
          style="max-width: 400px;">
        </el-input>
        <el-tag v-if="searchKeyword" type="info" size="small" style="margin-left: 10px;">
          找到 {{ filteredTutorials.length }} 个结果
        </el-tag>
      </div>

      <el-tabs v-model="activeCategory" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="基础指导" name="基础指导"></el-tab-pane>
        <el-tab-pane label="技术面试" name="技术面试"></el-tab-pane>
        <el-tab-pane label="行为面试" name="行为面试"></el-tab-pane>
      </el-tabs>

      <el-row :gutter="20">
        <el-col :span="8" v-for="tutorial in filteredTutorials" :key="tutorial.id">
          <el-card class="tutorial-card" @click.native="viewDetail(tutorial.id)">
            <h3>{{ tutorial.title }}</h3>
            <p class="tutorial-preview">{{ tutorial.content.substring(0, 100) }}...</p>
            <div class="tutorial-meta">
              <el-tag size="small">{{ tutorial.category }}</el-tag>
              <span><i class="el-icon-view"></i> {{ tutorial.viewCount }}</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog title="添加教程" :visible.sync="dialogVisible" width="60%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="基础指导" value="基础指导"></el-option>
            <el-option label="技术面试" value="技术面试"></el-option>
            <el-option label="行为面试" value="行为面试"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="form.content" :rows="10"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTutorial">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Tutorials',
  data() {
    return {
      tutorials: [],
      allTutorials: [], // 保存所有教程
      activeCategory: 'all',
      searchKeyword: '',
      dialogVisible: false,
      form: {
        title: '',
        category: '',
        content: '',
        createdBy: JSON.parse(localStorage.getItem('user')).id
      }
    }
  },
  computed: {
    isTeacher() {
      const user = JSON.parse(localStorage.getItem('user'))
      return user.role === 'TEACHER'
    },
    filteredTutorials() {
      let result = this.tutorials

      // 先按分类筛选
      if (this.activeCategory !== 'all') {
        result = result.filter(t => t.category === this.activeCategory)
      }

      return result
    }
  },
  created() {
    this.loadTutorials()
  },
  methods: {
    loadTutorials() {
      this.$http.get('/tutorials').then(res => {
        if (res.data) {
          this.tutorials = res.data
          this.allTutorials = res.data
        }
      }).catch(() => {})
    },
    handleSearch() {
      if (!this.searchKeyword || this.searchKeyword.trim() === '') {
        // 如果搜索框为空，恢复所有教程
        this.tutorials = this.allTutorials
        return
      }

      // 调用后端搜索接口
      this.$http.get(`/tutorials/search?keyword=${encodeURIComponent(this.searchKeyword)}`)
        .then(res => {
          if (res.data) {
            this.tutorials = res.data
          }
        })
        .catch(() => {
          this.$message.error('搜索失败')
        })
    },
    handleTabClick() {
      // Tab切换逻辑已在computed中处理
    },
    viewDetail(id) {
      this.$router.push(`/home/tutorial/${id}`)
    },
    showAddDialog() {
      this.dialogVisible = true
    },
    submitTutorial() {
      this.$http.post('/tutorials', this.form).then(() => {
        this.$message.success('添加成功')
        this.dialogVisible = false
        this.loadTutorials()
        this.form = { title: '', category: '', content: '', createdBy: this.form.createdBy }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* 现代化 Tutorials 页面 - 支持浅色/深色主题 */
.tutorials {
  padding: 0;
  animation: fadeInUp 0.4s ease;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-card__header {
  border-bottom: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-tabs__item {
  color: var(--lc-text-secondary);
}

/deep/ .el-tabs__item.is-active {
  color: var(--lc-primary);
}

/deep/ .el-tabs__active-bar {
  background: var(--lc-primary);
}

.tutorial-card {
  margin-bottom: 16px;
  cursor: pointer;
  transition: all var(--lc-transition);
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
}

.tutorial-card:hover {
  transform: translateY(-4px);
  border-color: var(--lc-primary) !important;
  box-shadow: var(--lc-shadow-lg);
}

.tutorial-card h3 {
  margin: 0 0 10px 0;
  color: var(--lc-text-primary);
}

.tutorial-preview {
  color: var(--lc-text-secondary);
  line-height: 1.6;
  margin: 10px 0;
}

.tutorial-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.tutorial-meta span {
  color: var(--lc-text-muted);
  font-size: 14px;
}

/deep/ .el-tag {
  background: var(--lc-primary-bg);
  border-color: transparent;
  color: var(--lc-primary);
}

/deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: var(--lc-text-inverse);
  font-weight: 600;
}

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

/deep/ .el-form-item__label {
  color: var(--lc-text-secondary);
}

/deep/ .el-textarea__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-select .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
