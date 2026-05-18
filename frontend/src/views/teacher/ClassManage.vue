<template>
  <div class="class-manage">
    <!-- 班级列表 -->
    <el-card v-if="!selectedClass && !selectedStudent">
      <div slot="header" class="header">
        <span>班级管理</span>
        <el-button type="primary" size="small" icon="el-icon-plus" @click="showCreateDialog">
          创建班级
        </el-button>
      </div>

      <el-empty v-if="!loading && classes.length === 0" description="暂无班级，点击右上角创建"></el-empty>

      <el-row :gutter="20" v-loading="loading">
        <el-col :span="8" v-for="cls in classes" :key="cls.id" style="margin-bottom: 20px;">
          <el-card shadow="hover" class="class-card">
            <div class="class-header">
              <div class="class-name">{{ cls.name }}</div>
              <el-tag :type="getStatusType(cls.status)" size="small">{{ getStatusText(cls.status) }}</el-tag>
            </div>
            <div class="class-desc">{{ cls.description || '暂无描述' }}</div>
            <div class="class-meta">
              <div class="meta-item">
                <i class="el-icon-user"></i>
                <span>{{ cls.studentCount || 0 }} / {{ cls.maxStudents || 100 }} 人</span>
              </div>
              <div class="meta-item invite-code" @click="copyCode(cls.classCode)">
                <i class="el-icon-key"></i>
                <span>邀请码: <b>{{ cls.classCode }}</b></span>
                <i class="el-icon-copy-document" style="margin-left:4px; cursor:pointer;"></i>
              </div>
            </div>
            <div class="class-actions">
              <el-button size="small" type="primary" plain @click="viewStudents(cls)">
                <i class="el-icon-s-custom"></i> 查看学生
              </el-button>
              <el-button size="small" plain @click="editClass(cls)">
                <i class="el-icon-edit"></i> 编辑
              </el-button>
              <el-button size="small" type="danger" plain @click="deleteClass(cls)">
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 班级学生列表 -->
    <template v-if="selectedClass && !selectedStudent">
      <div class="back-bar">
        <el-button size="small" icon="el-icon-arrow-left" @click="selectedClass = null">返回班级列表</el-button>
        <span class="back-title">{{ selectedClass.name }} — 学生列表</span>
        <el-tag style="margin-left:12px;">{{ students.length }} 人</el-tag>
      </div>

      <el-card>
        <el-table :data="students" v-loading="studentsLoading" style="width:100%">
          <el-table-column label="用户名" prop="username" width="120"></el-table-column>
          <el-table-column label="昵称" prop="nickname" width="120">
            <template slot-scope="scope">{{ scope.row.nickname || '—' }}</template>
          </el-table-column>
          <el-table-column label="邮箱" prop="email" show-overflow-tooltip></el-table-column>
          <el-table-column label="目标岗位" prop="targetPosition" width="130">
            <template slot-scope="scope">{{ scope.row.targetPosition || '未设置' }}</template>
          </el-table-column>
          <el-table-column label="积分" prop="points" width="80"></el-table-column>
          <el-table-column label="加入时间" width="160">
            <template slot-scope="scope">{{ formatDate(scope.row.joinTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template slot-scope="scope">
              <el-button size="mini" type="text" @click="viewStudentStats(scope.row)">查看数据</el-button>
              <el-button size="mini" type="text" style="color:#F56C6C;" @click="removeStudent(scope.row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-if="!studentsLoading && students.length === 0" description="班级暂无学生"></el-empty>
      </el-card>
    </template>

    <!-- 学生详情数据 -->
    <template v-if="selectedStudent">
      <div class="back-bar">
        <el-button size="small" icon="el-icon-arrow-left" @click="selectedStudent = null">返回学生列表</el-button>
        <span class="back-title">{{ selectedStudent.nickname || selectedStudent.username }} — 学习数据</span>
      </div>

      <div v-loading="statsLoading">
        <el-row :gutter="20" style="margin-bottom:20px;">
          <el-col :span="8">
            <el-card class="info-card">
              <div slot="header"><i class="el-icon-user"></i> 基本信息</div>
              <div class="info-row"><span class="info-label">用户名</span><span>{{ studentStats.username }}</span></div>
              <div class="info-row"><span class="info-label">昵称</span><span>{{ studentStats.nickname || '未设置' }}</span></div>
              <div class="info-row"><span class="info-label">邮箱</span><span>{{ studentStats.email || '未设置' }}</span></div>
              <div class="info-row"><span class="info-label">目标岗位</span><span>{{ studentStats.targetPosition || '未设置' }}</span></div>
              <div class="info-row"><span class="info-label">积分</span><span class="highlight">{{ studentStats.points || 0 }}</span></div>
            </el-card>
          </el-col>
          <el-col :span="16">
            <el-row :gutter="16">
              <el-col :span="8" v-for="stat in computedStats" :key="stat.label" style="margin-bottom:16px;">
                <el-card class="stat-mini-card">
                  <div class="stat-icon-wrap" :style="{background: stat.color}">
                    <i :class="stat.icon"></i>
                  </div>
                  <div class="stat-mini-value">{{ stat.value }}</div>
                  <div class="stat-mini-label">{{ stat.label }}</div>
                </el-card>
              </el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>
    </template>

    <!-- 创建/编辑班级对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="classForm" :rules="classRules" ref="classForm" label-width="90px">
        <el-form-item label="班级名称" prop="name">
          <el-input v-model="classForm.name" placeholder="请输入班级名称"></el-input>
        </el-form-item>
        <el-form-item label="班级描述">
          <el-input type="textarea" v-model="classForm.description" :rows="3" placeholder="请输入班级描述（选填）"></el-input>
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input-number v-model="classForm.maxStudents" :min="1" :max="500"></el-input-number>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClass" :loading="submitting">{{ isEdit ? '保存' : '创建' }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ClassManage',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      classes: [],
      loading: false,
      selectedClass: null,
      students: [],
      studentsLoading: false,
      selectedStudent: null,
      studentStats: {},
      statsLoading: false,
      dialogVisible: false,
      dialogTitle: '创建班级',
      isEdit: false,
      submitting: false,
      classForm: {
        name: '',
        description: '',
        maxStudents: 100
      },
      classRules: {
        name: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    computedStats() {
      const s = this.studentStats
      return [
        { label: '已学知识点', value: s.studiedKnowledgePoints || 0, icon: 'el-icon-notebook-2', color: 'linear-gradient(135deg,#667eea,#764ba2)' },
        { label: '已掌握', value: s.masteredKnowledgePoints || 0, icon: 'el-icon-circle-check', color: 'linear-gradient(135deg,#43e97b,#38f9d7)' },
        { label: '学习时长', value: (s.totalStudyTimeMinutes || 0) + '分钟', icon: 'el-icon-time', color: 'linear-gradient(135deg,#4facfe,#00f2fe)' },
        { label: '测试次数', value: s.testCount || 0, icon: 'el-icon-edit', color: 'linear-gradient(135deg,#f093fb,#f5576c)' },
        { label: '平均分', value: (s.averageScore || 0) + '分', icon: 'el-icon-trophy', color: 'linear-gradient(135deg,#fa709a,#fee140)' },
        { label: '未解决错题', value: s.unsolvedWrongCount || 0, icon: 'el-icon-warning', color: 'linear-gradient(135deg,#f7971e,#ffd200)' }
      ]
    }
  },
  mounted() {
    this.loadClasses()
  },
  methods: {
    loadClasses() {
      this.loading = true
      this.$http.get(`/classes/teacher/${this.user.id}`).then(res => {
        this.classes = Array.isArray(res.data) ? res.data : []
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    showCreateDialog() {
      this.dialogTitle = '创建班级'
      this.isEdit = false
      this.classForm = { name: '', description: '', maxStudents: 100 }
      this.dialogVisible = true
    },
    editClass(cls) {
      this.dialogTitle = '编辑班级'
      this.isEdit = true
      this.classForm = { id: cls.id, name: cls.name, description: cls.description, maxStudents: cls.maxStudents }
      this.dialogVisible = true
    },
    submitClass() {
      this.$refs.classForm.validate(valid => {
        if (!valid) return
        this.submitting = true
        const data = { ...this.classForm, teacherId: this.user.id }
        const req = this.isEdit
          ? this.$http.put(`/classes/${data.id}`, data)
          : this.$http.post('/classes', data)
        req.then(() => {
          this.$message.success(this.isEdit ? '保存成功' : '创建成功')
          this.dialogVisible = false
          this.loadClasses()
        }).catch(err => {
          this.$message.error(err.message || '操作失败')
        }).finally(() => { this.submitting = false })
      })
    },
    deleteClass(cls) {
      this.$confirm(`确定删除班级"${cls.name}"吗？`, '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.$http.delete(`/classes/${cls.id}`).then(() => {
          this.$message.success('删除成功')
          this.loadClasses()
        })
      }).catch(() => {})
    },
    viewStudents(cls) {
      this.selectedClass = cls
      this.selectedStudent = null
      this.studentsLoading = true
      this.$http.get(`/classes/${cls.id}/students`).then(res => {
        this.students = Array.isArray(res.data) ? res.data : []
        this.studentsLoading = false
      }).catch(() => { this.studentsLoading = false })
    },
    removeStudent(student) {
      this.$confirm(`确定移除学生 "${student.nickname || student.username}" 吗？`, '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.$http.delete(`/classes/${this.selectedClass.id}/students/${student.studentId}`).then(() => {
          this.$message.success('已移除')
          this.viewStudents(this.selectedClass)
        })
      }).catch(() => {})
    },
    viewStudentStats(student) {
      this.selectedStudent = student
      this.statsLoading = true
      this.$http.get(`/classes/student/${student.studentId}/stats`).then(res => {
        this.studentStats = res.data || {}
        this.statsLoading = false
      }).catch(() => { this.statsLoading = false })
    },
    copyCode(code) {
      navigator.clipboard.writeText(code).then(() => {
        this.$message.success('邀请码已复制: ' + code)
      }).catch(() => {
        this.$message.info('邀请码: ' + code)
      })
    },
    getStatusType(status) {
      return { ACTIVE: 'success', ENDED: 'info', ARCHIVED: 'warning' }[status] || 'info'
    },
    getStatusText(status) {
      return { ACTIVE: '进行中', ENDED: '已结束', ARCHIVED: '已归档' }[status] || status
    },
    formatDate(val) {
      if (!val) return '—'
      if (Array.isArray(val)) {
        const [y, mo, d, h = 0, m = 0] = val
        return `${y}-${String(mo).padStart(2,'0')}-${String(d).padStart(2,'0')} ${String(h).padStart(2,'0')}:${String(m).padStart(2,'0')}`
      }
      return new Date(val).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.class-manage { padding: 20px; }

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.class-card { height: 100%; }

.class-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.class-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--lc-text-primary);
}

.class-desc {
  font-size: 13px;
  color: var(--lc-text-muted);
  margin-bottom: 12px;
  min-height: 36px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.class-meta {
  margin-bottom: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--lc-text-secondary);
  margin-bottom: 4px;
}

.invite-code {
  cursor: pointer;
  color: var(--lc-primary);
}

.invite-code b { letter-spacing: 2px; }

.class-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.back-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  gap: 12px;
}

.back-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--lc-text-primary);
}

.info-card .info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--lc-border);
  font-size: 14px;
}

.info-card .info-row:last-child { border-bottom: none; }

.info-label {
  color: var(--lc-text-muted);
  flex-shrink: 0;
  margin-right: 12px;
}

.highlight { color: var(--lc-primary); font-weight: 600; }

.stat-mini-card {
  text-align: center;
  padding: 4px;
}

.stat-icon-wrap {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 8px;
}

.stat-icon-wrap i { font-size: 22px; color: #fff; }

.stat-mini-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--lc-text-primary);
}

.stat-mini-label {
  font-size: 12px;
  color: var(--lc-text-muted);
  margin-top: 2px;
}
</style>
