<template>
  <div class="knowledge-manage">
    <el-card>
      <div slot="header" class="header">
        <span>知识点管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showCreateDialog">
          创建知识点
        </el-button>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="Java基础" value="Java基础"></el-option>
            <el-option label="框架技术" value="框架技术"></el-option>
            <el-option label="数据库" value="数据库"></el-option>
            <el-option label="算法" value="算法"></el-option>
            <el-option label="网络" value="网络"></el-option>
            <el-option label="操作系统" value="操作系统"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="简单" value="简单"></el-option>
            <el-option label="中等" value="中等"></el-option>
            <el-option label="困难" value="困难"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索标题" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchKnowledge">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="knowledgeList" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="category" label="分类" width="120"></el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template slot-scope="scope">
            <el-tag :type="getDifficultyType(scope.row.difficulty)" size="small">
              {{ scope.row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="importance" label="重要度" width="100">
          <template slot-scope="scope">
            <el-rate v-model="scope.row.importance" disabled :max="5"></el-rate>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="editKnowledge(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="viewKnowledge(scope.row)">查看</el-button>
            <el-button size="mini" type="text" style="color: #F56C6C;" @click="deleteKnowledge(scope.row)">删除</el-button>
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
      <el-form :model="knowledgeForm" :rules="knowledgeRules" ref="knowledgeForm" label-width="100px">
        <el-form-item label="知识点标题" prop="title">
          <el-input v-model="knowledgeForm.title" placeholder="请输入知识点标题"></el-input>
        </el-form-item>

        <el-form-item label="知识点内容" prop="content">
          <el-input
            type="textarea"
            v-model="knowledgeForm.content"
            :rows="12"
            placeholder="请输入知识点内容，支持Markdown格式">
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="分类" prop="category">
              <el-select v-model="knowledgeForm.category" placeholder="请选择分类" style="width: 100%;">
                <el-option label="Java基础" value="Java基础"></el-option>
                <el-option label="框架技术" value="框架技术"></el-option>
                <el-option label="数据库" value="数据库"></el-option>
                <el-option label="算法" value="算法"></el-option>
                <el-option label="网络" value="网络"></el-option>
                <el-option label="操作系统" value="操作系统"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="knowledgeForm.difficulty" placeholder="请选择难度" style="width: 100%;">
                <el-option label="简单" value="简单"></el-option>
                <el-option label="中等" value="中等"></el-option>
                <el-option label="困难" value="困难"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="重要度" prop="importance">
              <el-rate v-model="knowledgeForm.importance" :max="5"></el-rate>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标签">
          <el-tag
            v-for="tag in knowledgeForm.tags"
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
        <el-button type="primary" @click="submitKnowledge" :loading="submitting">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'KnowledgeManage',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      searchForm: {
        category: '',
        difficulty: '',
        keyword: ''
      },
      allKnowledge: [],
      knowledgeList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '创建知识点',
      isEdit: false,
      submitting: false,
      knowledgeForm: {
        title: '',
        content: '',
        category: '',
        difficulty: '中等',
        importance: 3,
        tags: []
      },
      knowledgeRules: {
        title: [{ required: true, message: '请输入知识点标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入知识点内容', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }]
      },
      tagInputVisible: false,
      tagInputValue: ''
    }
  },
  mounted() {
    this.loadKnowledge()
  },
  methods: {
    loadKnowledge() {
      this.loading = true
      this.$http.get('/knowledge').then(res => {
        if (Array.isArray(res.data)) {
          this.allKnowledge = res.data
        }
        this.applyFilter()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    applyFilter() {
      let filtered = this.allKnowledge
      if (this.searchForm.category) {
        filtered = filtered.filter(k => k.category === this.searchForm.category)
      }
      if (this.searchForm.difficulty) {
        filtered = filtered.filter(k => k.difficulty === this.searchForm.difficulty)
      }
      if (this.searchForm.keyword) {
        const kw = this.searchForm.keyword.toLowerCase()
        filtered = filtered.filter(k => k.title && k.title.toLowerCase().includes(kw))
      }
      this.total = filtered.length
      const start = (this.currentPage - 1) * this.pageSize
      this.knowledgeList = filtered.slice(start, start + this.pageSize)
    },
    searchKnowledge() {
      this.currentPage = 1
      this.applyFilter()
    },
    resetSearch() {
      this.searchForm = { category: '', difficulty: '', keyword: '' }
      this.currentPage = 1
      this.applyFilter()
    },
    showCreateDialog() {
      this.dialogTitle = '创建知识点'
      this.isEdit = false
      this.knowledgeForm = {
        title: '',
        content: '',
        category: '',
        difficulty: '中等',
        importance: 3,
        tags: []
      }
      this.dialogVisible = true
    },
    editKnowledge(row) {
      this.dialogTitle = '编辑知识点'
      this.isEdit = true
      this.knowledgeForm = {
        id: row.id,
        title: row.title,
        content: row.content,
        category: row.category,
        difficulty: row.difficulty,
        importance: row.importance || 3,
        tags: typeof row.tags === 'string' && row.tags
          ? row.tags.split(',').map(t => t.trim()).filter(Boolean)
          : (Array.isArray(row.tags) ? row.tags : [])
      }
      this.dialogVisible = true
    },
    viewKnowledge(row) {
      this.$router.push(`/home/knowledge?id=${row.id}`)
    },
    deleteKnowledge(row) {
      this.$confirm('确定要删除这个知识点吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/knowledge/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadKnowledge()
        })
      }).catch(() => {})
    },
    submitKnowledge() {
      this.$refs.knowledgeForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const f = this.knowledgeForm
          const data = {
            ...f,
            tags: Array.isArray(f.tags) ? f.tags.join(',') : (f.tags || ''),
            authorId: this.user.id
          }
          
          const request = this.isEdit
            ? this.$http.put(`/knowledge/${data.id}`, data)
            : this.$http.post('/knowledge', data)
          
          request.then(() => {
            this.$message.success(this.isEdit ? '保存成功' : '创建成功')
            this.dialogVisible = false
            this.loadKnowledge()
          }).catch(err => {
            this.$message.error(err.message || '操作失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    handleTagClose(tag) {
      this.knowledgeForm.tags.splice(this.knowledgeForm.tags.indexOf(tag), 1)
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
        this.knowledgeForm.tags.push(inputValue)
      }
      this.tagInputVisible = false
      this.tagInputValue = ''
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.applyFilter()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.applyFilter()
    },
    getDifficultyType(difficulty) {
      const types = {
        '简单': 'success',
        '中等': 'warning',
        '困难': 'danger'
      }
      return types[difficulty] || ''
    }
  }
}
</script>

<style scoped>
.knowledge-manage {
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
