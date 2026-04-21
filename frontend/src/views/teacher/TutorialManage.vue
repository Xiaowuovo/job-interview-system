<template>
  <div class="tutorial-manage">
    <el-card>
      <div slot="header" class="header">
        <span>教程管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showCreateDialog">
          创建教程
        </el-button>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="基础指导" value="基础指导"></el-option>
            <el-option label="技术面试" value="技术面试"></el-option>
            <el-option label="行为面试" value="行为面试"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索标题" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchTutorials">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tutorials" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="教程标题" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="category" label="分类" width="120"></el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template slot-scope="scope">
            <el-tag :type="getDifficultyType(scope.row.difficulty)" size="small">
              {{ scope.row.difficulty || '中等' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="editTutorial(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="viewTutorial(scope.row)">查看</el-button>
            <el-button size="mini" type="text" style="color: #F56C6C;" @click="deleteTutorial(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right;">
      </el-pagination>
    </el-card>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="70%"
      :close-on-click-modal="false">
      <el-form :model="tutorialForm" :rules="tutorialRules" ref="tutorialForm" label-width="100px">
        <el-form-item label="教程标题" prop="title">
          <el-input v-model="tutorialForm.title" placeholder="请输入教程标题"></el-input>
        </el-form-item>

        <el-form-item label="教程描述" prop="description">
          <el-input
            type="textarea"
            v-model="tutorialForm.description"
            :rows="3"
            placeholder="请输入教程描述">
          </el-input>
        </el-form-item>

        <el-form-item label="教程内容" prop="content">
          <el-input
            type="textarea"
            v-model="tutorialForm.content"
            :rows="10"
            placeholder="请输入教程内容，支持Markdown格式">
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="tutorialForm.category" placeholder="请选择分类" style="width: 100%;">
                <el-option label="基础指导" value="基础指导"></el-option>
                <el-option label="技术面试" value="技术面试"></el-option>
                <el-option label="行为面试" value="行为面试"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="tutorialForm.difficulty" placeholder="请选择难度" style="width: 100%;">
                <el-option label="简单" value="简单"></el-option>
                <el-option label="中等" value="中等"></el-option>
                <el-option label="困难" value="困难"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标签">
          <el-tag
            v-for="tag in tutorialForm.tags"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="handleTagClose(tag)"
            style="margin-right: 10px;">
            {{ tag }}
          </el-tag>
          <el-input
            class="input-new-tag"
            v-if="tagInputVisible"
            v-model="tagInputValue"
            ref="saveTagInput"
            size="small"
            @keyup.enter.native="handleTagInputConfirm"
            @blur="handleTagInputConfirm"
            style="width: 90px;">
          </el-input>
          <el-button v-else class="button-new-tag" size="small" @click="showTagInput">+ 添加标签</el-button>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTutorial" :loading="submitting">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'TutorialManage',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      searchForm: {
        category: '',
        keyword: ''
      },
      tutorials: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '创建教程',
      isEdit: false,
      submitting: false,
      tutorialForm: {
        title: '',
        description: '',
        content: '',
        category: '',
        difficulty: '中等',
        tags: []
      },
      tutorialRules: {
        title: [{ required: true, message: '请输入教程标题', trigger: 'blur' }],
        description: [{ required: true, message: '请输入教程描述', trigger: 'blur' }],
        content: [{ required: true, message: '请输入教程内容', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }]
      },
      tagInputVisible: false,
      tagInputValue: ''
    }
  },
  mounted() {
    this.loadTutorials()
  },
  methods: {
    loadTutorials() {
      this.loading = true
      const params = {
        teacherId: this.user.id,
        ...this.searchForm
      }
      
      this.$http.get('/tutorials', { params }).then(res => {
        if (Array.isArray(res.data)) {
          this.tutorials = res.data
          this.total = res.data.length
        }
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    searchTutorials() {
      this.loadTutorials()
    },
    resetSearch() {
      this.searchForm = {
        category: '',
        keyword: ''
      }
      this.loadTutorials()
    },
    showCreateDialog() {
      this.dialogTitle = '创建教程'
      this.isEdit = false
      this.tutorialForm = {
        title: '',
        description: '',
        content: '',
        category: '',
        difficulty: '中等',
        tags: []
      }
      this.dialogVisible = true
    },
    editTutorial(row) {
      this.dialogTitle = '编辑教程'
      this.isEdit = true
      this.tutorialForm = {
        id: row.id,
        title: row.title,
        description: row.description,
        content: row.content,
        category: row.category,
        difficulty: row.difficulty || '中等',
        tags: row.tags || []
      }
      this.dialogVisible = true
    },
    viewTutorial(row) {
      this.$router.push(`/home/tutorial/${row.id}`)
    },
    deleteTutorial(row) {
      this.$confirm('确定要删除这个教程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/tutorials/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadTutorials()
        })
      }).catch(() => {})
    },
    submitTutorial() {
      this.$refs.tutorialForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const data = {
            ...this.tutorialForm,
            authorId: this.user.id
          }
          
          const request = this.isEdit
            ? this.$http.put(`/tutorials/${data.id}`, data)
            : this.$http.post('/tutorials', data)
          
          request.then(() => {
            this.$message.success(this.isEdit ? '保存成功' : '创建成功')
            this.dialogVisible = false
            this.loadTutorials()
          }).catch(err => {
            this.$message.error(err.message || '操作失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    handleTagClose(tag) {
      this.tutorialForm.tags.splice(this.tutorialForm.tags.indexOf(tag), 1)
    },
    showTagInput() {
      this.tagInputVisible = true
      this.$nextTick(() => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    handleTagInputConfirm() {
      let inputValue = this.tagInputValue
      if (inputValue) {
        this.tutorialForm.tags.push(inputValue)
      }
      this.tagInputVisible = false
      this.tagInputValue = ''
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.loadTutorials()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.loadTutorials()
    },
    getDifficultyType(difficulty) {
      const types = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return types[difficulty] || ''
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
.tutorial-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
</style>
