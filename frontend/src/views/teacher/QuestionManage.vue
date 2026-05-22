<template>
  <div class="question-manage">
    <el-card>
      <div slot="header" class="header">
        <span>题目管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showCreateDialog">
          创建题目
        </el-button>
      </div>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="题目类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="单选题" value="CHOICE"></el-option>
            <el-option label="多选题" value="MULTIPLE_CHOICE"></el-option>
            <el-option label="编程题" value="CODING"></el-option>
            <el-option label="简答题" value="SHORT_ANSWER"></el-option>
            <el-option label="系统设计题" value="SYSTEM_DESIGN"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="Java" value="Java"></el-option>
            <el-option label="前端" value="前端"></el-option>
            <el-option label="数据库" value="数据库"></el-option>
            <el-option label="算法" value="算法"></el-option>
            <el-option label="系统设计" value="系统设计"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="全部" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="简单" value="EASY"></el-option>
            <el-option label="中等" value="MEDIUM"></el-option>
            <el-option label="困难" value="HARD"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchQuestions">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="questions" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="题目标题" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="120">
          <template slot-scope="scope">
            <el-tag size="small">{{ formatType(scope.row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100"></el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80">
          <template slot-scope="scope">
            <el-tag :type="getDifficultyType(scope.row.difficulty)" size="small">
              {{ formatDifficulty(scope.row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80"></el-table-column>
        <el-table-column prop="answerCount" label="答题" width="80"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="editQuestion(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="viewQuestion(scope.row)">查看</el-button>
            <el-button size="mini" type="text" style="color: #F56C6C;" @click="deleteQuestion(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        style="margin-top: 20px; text-align: right;">
      </el-pagination>
    </el-card>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="80%"
      :close-on-click-modal="false">
      <el-form :model="questionForm" :rules="questionRules" ref="questionForm" label-width="100px">
        <el-form-item label="题目类型" prop="type">
          <el-radio-group v-model="questionForm.type">
            <el-radio label="CHOICE">单选题</el-radio>
            <el-radio label="MULTIPLE_CHOICE">多选题</el-radio>
            <el-radio label="CODING">编程题</el-radio>
            <el-radio label="SHORT_ANSWER">简答题</el-radio>
            <el-radio label="SYSTEM_DESIGN">系统设计题</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="题目标题" prop="title">
          <el-input v-model="questionForm.title" placeholder="请输入题目标题"></el-input>
        </el-form-item>

        <el-form-item label="题目内容" prop="content">
          <el-input
            type="textarea"
            v-model="questionForm.content"
            :rows="6"
            placeholder="请输入题目内容，支持Markdown格式">
          </el-input>
        </el-form-item>

        <el-form-item label="选项" v-if="questionForm.type === 'CHOICE' || questionForm.type === 'MULTIPLE_CHOICE'">
          <div v-for="(option, index) in questionForm.options" :key="index" style="margin-bottom: 10px;">
            <el-input v-model="questionForm.options[index]" :placeholder="'选项 ' + String.fromCharCode(65 + index)">
              <template slot="prepend">{{ String.fromCharCode(65 + index) }}</template>
              <el-button slot="append" icon="el-icon-delete" @click="removeOption(index)" v-if="questionForm.options.length > 2"></el-button>
            </el-input>
          </div>
          <el-button size="small" @click="addOption" v-if="questionForm.options.length < 6">添加选项</el-button>
        </el-form-item>

        <el-form-item label="正确答案" prop="answer">
          <el-input
            v-if="questionForm.type === 'CODING' || questionForm.type === 'SHORT_ANSWER' || questionForm.type === 'SYSTEM_DESIGN'"
            type="textarea"
            v-model="questionForm.answer"
            :rows="4"
            placeholder="请输入参考答案">
          </el-input>
          <el-select
            v-else-if="questionForm.type === 'CHOICE'"
            v-model="questionForm.answer"
            placeholder="请选择正确答案">
            <el-option
              v-for="(option, index) in questionForm.options"
              :key="index"
              :label="String.fromCharCode(65 + index)"
              :value="String.fromCharCode(65 + index)">
            </el-option>
          </el-select>
          <el-select
            v-else-if="questionForm.type === 'MULTIPLE_CHOICE'"
            v-model="questionForm.answer"
            multiple
            placeholder="请选择正确答案">
            <el-option
              v-for="(option, index) in questionForm.options"
              :key="index"
              :label="String.fromCharCode(65 + index)"
              :value="String.fromCharCode(65 + index)">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="题目解析" prop="explanation">
          <el-input
            type="textarea"
            v-model="questionForm.explanation"
            :rows="4"
            placeholder="请输入题目解析">
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="分类" prop="category">
              <el-select v-model="questionForm.category" placeholder="请选择分类">
                <el-option label="Java" value="Java"></el-option>
                <el-option label="前端" value="前端"></el-option>
                <el-option label="数据库" value="数据库"></el-option>
                <el-option label="算法" value="算法"></el-option>
                <el-option label="系统设计" value="系统设计"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="questionForm.difficulty" placeholder="请选择难度">
                <el-option label="简单" value="EASY"></el-option>
                <el-option label="中等" value="MEDIUM"></el-option>
                <el-option label="困难" value="HARD"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分值" prop="score">
              <el-input-number v-model="questionForm.score" :min="1" :max="100"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标签">
          <el-tag
            v-for="tag in questionForm.tags"
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
        <el-button type="primary" @click="submitQuestion" :loading="submitting">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'QuestionManage',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      searchForm: {
        type: '',
        category: '',
        difficulty: ''
      },
      allQuestions: [],
      questions: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '创建题目',
      isEdit: false,
      submitting: false,
      questionForm: {
        type: 'CHOICE',
        title: '',
        content: '',
        options: ['', '', '', ''],
        answer: '',
        explanation: '',
        category: '',
        difficulty: 'MEDIUM',
        score: 10,
        tags: []
      },
      questionRules: {
        type: [{ required: true, message: '请选择题目类型', trigger: 'change' }],
        title: [{ required: true, message: '请输入题目标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
        answer: [{ required: true, message: '请输入正确答案', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }],
        difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }]
      },
      tagInputVisible: false,
      tagInputValue: ''
    }
  },
  mounted() {
    this.loadQuestions()
  },
  methods: {
    loadQuestions() {
      this.loading = true
      this.$http.get('/questions').then(res => {
        if (Array.isArray(res.data)) {
          this.allQuestions = res.data
        }
        this.applyFilter()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    applyFilter() {
      let filtered = this.allQuestions
      if (this.searchForm.type) {
        filtered = filtered.filter(q => q.type === this.searchForm.type)
      }
      if (this.searchForm.category) {
        filtered = filtered.filter(q => q.category === this.searchForm.category)
      }
      if (this.searchForm.difficulty) {
        filtered = filtered.filter(q => q.difficulty === this.searchForm.difficulty)
      }
      this.total = filtered.length
      const start = (this.currentPage - 1) * this.pageSize
      this.questions = filtered.slice(start, start + this.pageSize)
    },
    searchQuestions() {
      this.currentPage = 1
      this.applyFilter()
    },
    resetSearch() {
      this.searchForm = { type: '', category: '', difficulty: '' }
      this.currentPage = 1
      this.applyFilter()
    },
    showCreateDialog() {
      this.dialogTitle = '创建题目'
      this.isEdit = false
      this.questionForm = {
        type: 'CHOICE',
        title: '',
        content: '',
        options: ['', '', '', ''],
        answer: '',
        explanation: '',
        category: '',
        difficulty: 'MEDIUM',
        score: 10,
        tags: []
      }
      this.dialogVisible = true
    },
    editQuestion(row) {
      this.dialogTitle = '编辑题目'
      this.isEdit = true
      const options = [
        row.optionA || '',
        row.optionB || '',
        row.optionC || '',
        row.optionD || ''
      ]
      const rawTags = row.tags
      const tagsArr = typeof rawTags === 'string' && rawTags
        ? rawTags.split(',').map(t => t.trim()).filter(Boolean)
        : (Array.isArray(rawTags) ? rawTags : [])
      this.questionForm = {
        id: row.id,
        type: row.type,
        title: row.title,
        content: row.content,
        options,
        answer: row.correctAnswer || row.answer || '',
        explanation: row.explanation,
        category: row.category,
        difficulty: row.difficulty,
        score: row.score || 10,
        tags: tagsArr
      }
      this.dialogVisible = true
    },
    viewQuestion(row) {
      this.$router.push(`/home/practice?questionId=${row.id}`)
    },
    deleteQuestion(row) {
      this.$confirm('确定要删除这个题目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/questions/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadQuestions()
        })
      }).catch(() => {})
    },
    submitQuestion() {
      this.$refs.questionForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const f = this.questionForm
          const data = {
            type: f.type,
            title: f.title,
            content: f.content,
            correctAnswer: Array.isArray(f.answer) ? f.answer.join(',') : f.answer,
            explanation: f.explanation,
            category: f.category,
            difficulty: f.difficulty,
            tags: Array.isArray(f.tags) ? f.tags.join(',') : (f.tags || ''),
            createdBy: this.user.id
          }
          if (f.id) data.id = f.id
          if (f.type === 'CHOICE' || f.type === 'MULTIPLE_CHOICE') {
            data.optionA = f.options[0] || ''
            data.optionB = f.options[1] || ''
            data.optionC = f.options[2] || ''
            data.optionD = f.options[3] || ''
            if (f.options[4] !== undefined) data.optionE = f.options[4]
            if (f.options[5] !== undefined) data.optionF = f.options[5]
          }
          
          const request = this.isEdit
            ? this.$http.put(`/questions/${data.id}`, data)
            : this.$http.post('/questions', data)
          
          request.then(() => {
            this.$message.success(this.isEdit ? '保存成功' : '创建成功')
            this.dialogVisible = false
            this.loadQuestions()
          }).catch(err => {
            this.$message.error(err.message || '操作失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    addOption() {
      this.questionForm.options.push('')
    },
    removeOption(index) {
      this.questionForm.options.splice(index, 1)
    },
    handleTagClose(tag) {
      this.questionForm.tags.splice(this.questionForm.tags.indexOf(tag), 1)
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
        this.questionForm.tags.push(inputValue)
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
    formatType(type) {
      const types = {
        'CHOICE': '单选题',
        'MULTIPLE_CHOICE': '多选题',
        'CODING': '编程题',
        'SHORT_ANSWER': '简答题',
        'SYSTEM_DESIGN': '系统设计题'
      }
      return types[type] || type
    },
    formatDifficulty(difficulty) {
      const difficulties = {
        'EASY': '简单',
        'MEDIUM': '中等',
        'HARD': '困难'
      }
      return difficulties[difficulty] || difficulty
    },
    getDifficultyType(difficulty) {
      const types = {
        'EASY': 'success',
        'MEDIUM': 'warning',
        'HARD': 'danger'
      }
      return types[difficulty] || ''
    }
  }
}
</script>

<style scoped>
.question-manage {
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
