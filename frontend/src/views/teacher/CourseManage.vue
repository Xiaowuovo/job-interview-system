<template>
  <div class="course-manage">
    <el-card>
      <div slot="header" class="header">
        <span>课程管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showCreateDialog">
          创建课程
        </el-button>
      </div>

      <el-table :data="courses" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60"></el-table-column>
        <el-table-column prop="title" label="课程名称" min-width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="category" label="分类" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="studentCount" label="学生数" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="editCourse(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" @click="manageChapters(scope.row)">章节管理</el-button>
            <el-button size="mini" type="text" @click="publishCourse(scope.row)" v-if="scope.row.status === 'DRAFT'">发布</el-button>
            <el-button size="mini" type="text" style="color: #F56C6C;" @click="deleteCourse(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建/编辑课程对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="60%"
      :close-on-click-modal="false">
      <el-form :model="courseForm" :rules="courseRules" ref="courseForm" label-width="100px">
        <el-form-item label="课程名称" prop="title">
          <el-input v-model="courseForm.title" placeholder="请输入课程名称"></el-input>
        </el-form-item>

        <el-form-item label="课程描述" prop="description">
          <el-input
            type="textarea"
            v-model="courseForm.description"
            :rows="4"
            placeholder="请输入课程描述">
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="courseForm.category" placeholder="请选择分类" style="width: 100%;">
                <el-option label="Java开发" value="Java开发"></el-option>
                <el-option label="前端开发" value="前端开发"></el-option>
                <el-option label="数据库" value="数据库"></el-option>
                <el-option label="算法" value="算法"></el-option>
                <el-option label="系统设计" value="系统设计"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度" prop="difficulty">
              <el-select v-model="courseForm.difficulty" placeholder="请选择难度" style="width: 100%;">
                <el-option label="初级" value="初级"></el-option>
                <el-option label="中级" value="中级"></el-option>
                <el-option label="高级" value="高级"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课时数" prop="duration">
              <el-input-number v-model="courseForm.duration" :min="1" :max="1000" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="价格" prop="price">
              <el-input-number v-model="courseForm.price" :min="0" :precision="2" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCourse" :loading="submitting">
          {{ isEdit ? '保存' : '创建' }}
        </el-button>
      </span>
    </el-dialog>

    <!-- 章节管理对话框 -->
    <el-dialog
      title="章节管理"
      :visible.sync="chapterDialogVisible"
      width="70%"
      :close-on-click-modal="false">
      <el-button type="primary" size="small" icon="el-icon-plus" @click="showAddChapterDialog" style="margin-bottom: 15px;">
        添加章节
      </el-button>

      <el-table :data="chapters" v-loading="chapterLoading">
        <el-table-column prop="orderNum" label="序号" width="80"></el-table-column>
        <el-table-column prop="title" label="章节标题" min-width="200"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="120"></el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="editChapter(scope.row)">编辑</el-button>
            <el-button size="mini" type="text" style="color: #F56C6C;" @click="deleteChapter(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 添加/编辑章节对话框 -->
    <el-dialog
      :title="chapterEditTitle"
      :visible.sync="chapterEditDialogVisible"
      width="50%"
      append-to-body
      :close-on-click-modal="false">
      <el-form :model="chapterForm" :rules="chapterRules" ref="chapterForm" label-width="100px">
        <el-form-item label="章节标题" prop="title">
          <el-input v-model="chapterForm.title" placeholder="请输入章节标题"></el-input>
        </el-form-item>

        <el-form-item label="章节内容" prop="content">
          <el-input
            type="textarea"
            v-model="chapterForm.content"
            :rows="6"
            placeholder="请输入章节内容">
          </el-input>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="序号" prop="orderNum">
              <el-input-number v-model="chapterForm.orderNum" :min="1" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时长(分钟)" prop="duration">
              <el-input-number v-model="chapterForm.duration" :min="1" style="width: 100%;"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="chapterEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChapter">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CourseManage',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      courses: [],
      loading: false,
      dialogVisible: false,
      dialogTitle: '创建课程',
      isEdit: false,
      submitting: false,
      courseForm: {
        title: '',
        description: '',
        category: '',
        difficulty: '中级',
        duration: 10,
        price: 0
      },
      courseRules: {
        title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入课程描述', trigger: 'blur' }],
        category: [{ required: true, message: '请选择分类', trigger: 'change' }]
      },
      chapterDialogVisible: false,
      chapterEditDialogVisible: false,
      chapterEditTitle: '添加章节',
      currentCourseId: null,
      chapters: [],
      chapterLoading: false,
      chapterForm: {
        title: '',
        content: '',
        orderNum: 1,
        duration: 30
      },
      chapterRules: {
        title: [{ required: true, message: '请输入章节标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入章节内容', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadCourses()
  },
  methods: {
    loadCourses() {
      this.loading = true
      this.$http.get(`/teacher/course/my-courses?teacherId=${this.user.id}`).then(res => {
        this.courses = res.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    showCreateDialog() {
      this.dialogTitle = '创建课程'
      this.isEdit = false
      this.courseForm = {
        title: '',
        description: '',
        category: '',
        difficulty: '中级',
        duration: 10,
        price: 0
      }
      this.dialogVisible = true
    },
    editCourse(row) {
      this.dialogTitle = '编辑课程'
      this.isEdit = true
      this.courseForm = {
        id: row.id,
        title: row.title,
        description: row.description,
        category: row.category,
        difficulty: row.difficulty,
        duration: row.duration,
        price: row.price
      }
      this.dialogVisible = true
    },
    submitCourse() {
      this.$refs.courseForm.validate(valid => {
        if (valid) {
          this.submitting = true
          const data = {
            ...this.courseForm,
            teacherId: this.user.id,
            status: 'DRAFT'
          }
          
          const request = this.isEdit
            ? this.$http.put(`/teacher/course/${data.id}`, data)
            : this.$http.post('/teacher/course', data)
          
          request.then(() => {
            this.$message.success(this.isEdit ? '保存成功' : '创建成功')
            this.dialogVisible = false
            this.loadCourses()
          }).catch(err => {
            this.$message.error(err.message || '操作失败')
          }).finally(() => {
            this.submitting = false
          })
        }
      })
    },
    publishCourse(row) {
      this.$confirm('确定要发布这门课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        this.$http.post(`/teacher/course/${row.id}/publish`).then(() => {
          this.$message.success('发布成功')
          this.loadCourses()
        })
      }).catch(() => {})
    },
    deleteCourse(row) {
      this.$confirm('确定要删除这门课程吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/teacher/course/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadCourses()
        })
      }).catch(() => {})
    },
    manageChapters(row) {
      this.currentCourseId = row.id
      this.chapterDialogVisible = true
      this.loadChapters()
    },
    loadChapters() {
      this.chapterLoading = true
      this.$http.get(`/teacher/course/${this.currentCourseId}/chapters`).then(res => {
        this.chapters = res.data || []
        this.chapterLoading = false
      }).catch(() => {
        this.chapterLoading = false
      })
    },
    showAddChapterDialog() {
      this.chapterEditTitle = '添加章节'
      this.chapterForm = {
        title: '',
        content: '',
        orderNum: this.chapters.length + 1,
        duration: 30
      }
      this.chapterEditDialogVisible = true
    },
    editChapter(row) {
      this.chapterEditTitle = '编辑章节'
      this.chapterForm = {
        id: row.id,
        title: row.title,
        content: row.content,
        orderNum: row.orderNum,
        duration: row.duration
      }
      this.chapterEditDialogVisible = true
    },
    submitChapter() {
      this.$refs.chapterForm.validate(valid => {
        if (valid) {
          const data = {
            ...this.chapterForm,
            courseId: this.currentCourseId
          }
          
          const request = data.id
            ? this.$http.put(`/teacher/course/chapter/${data.id}`, data)
            : this.$http.post(`/teacher/course/${this.currentCourseId}/chapter`, data)
          
          request.then(() => {
            this.$message.success('保存成功')
            this.chapterEditDialogVisible = false
            this.loadChapters()
          }).catch(err => {
            this.$message.error(err.message || '操作失败')
          })
        }
      })
    },
    deleteChapter(row) {
      this.$confirm('确定要删除这个章节吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/teacher/course/chapter/${row.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadChapters()
        })
      }).catch(() => {})
    },
    getStatusType(status) {
      const types = {
        'DRAFT': 'info',
        'PUBLISHED': 'success',
        'ARCHIVED': 'warning'
      }
      return types[status] || 'info'
    },
    formatStatus(status) {
      const statuses = {
        'DRAFT': '草稿',
        'PUBLISHED': '已发布',
        'ARCHIVED': '已归档'
      }
      return statuses[status] || status
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
.course-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
