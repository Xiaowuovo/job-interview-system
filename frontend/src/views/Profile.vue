<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 左侧：个人信息卡片 -->
      <el-col :span="8">
        <el-card shadow="hover" class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="100" icon="el-icon-user-solid" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"></el-avatar>
            <h2 class="username">{{ userInfo.username }}</h2>
            <el-tag :type="getRoleType(userInfo.role)" size="medium">{{ getRoleText(userInfo.role) }}</el-tag>
          </div>

          <el-divider></el-divider>

          <div class="info-section">
            <div class="info-item">
              <i class="el-icon-user"></i>
              <span class="label">昵称：</span>
              <span class="value">{{ userInfo.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-message"></i>
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-phone"></i>
              <span class="label">手机：</span>
              <span class="value">{{ userInfo.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-position"></i>
              <span class="label">目标岗位：</span>
              <span class="value">{{ userInfo.targetPosition || '未设置' }}</span>
            </div>
            <div class="info-item">
              <i class="el-icon-coin"></i>
              <span class="label">积分：</span>
              <span class="value highlight">{{ userInfo.points || 0 }}</span>
            </div>
          </div>

          <el-divider></el-divider>

          <el-button type="primary" style="width: 100%; margin-bottom: 10px;" @click="editDialogVisible = true">
            <i class="el-icon-edit"></i> 编辑资料
          </el-button>
          <el-button style="width: 100%; margin-bottom: 10px;" @click="passwordDialogVisible = true">
            <i class="el-icon-lock"></i> 修改密码
          </el-button>
        </el-card>
      </el-col>

      <!-- 右侧：统计信息 -->
      <el-col :span="16">
        <!-- 学习统计 -->
        <el-card shadow="hover" class="stats-card">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-data-line"></i> 学习统计</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="8" v-for="(item, index) in learningStats" :key="index">
              <div class="stat-item">
                <div class="stat-icon" :style="{background: item.color}">
                  <i :class="item.icon"></i>
                </div>
                <div class="stat-content">
                  <div class="stat-value">{{ item.value }}</div>
                  <div class="stat-label">{{ item.label }}</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 班级信息 -->
        <el-card shadow="hover" class="class-info-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-s-custom"></i> 我的班级</span>
            <el-button type="primary" size="small" style="float:right;" @click="joinDialogVisible = true">
              <i class="el-icon-plus"></i> 加入班级
            </el-button>
          </div>
          <div v-if="myClasses.length === 0" class="empty-records">
            <i class="el-icon-s-custom"></i>
            <p>暂未加入任何班级，可凭邀请码加入</p>
          </div>
          <div v-for="cls in myClasses" :key="cls.classId" class="class-item">
            <div class="class-item-left">
              <div class="class-item-name">{{ cls.className }}</div>
              <div class="class-item-desc">{{ cls.description || '暂无描述' }}</div>
              <div class="class-item-meta">
                <el-tag size="mini" :type="cls.status === 'ACTIVE' ? 'success' : 'info'">{{ cls.status === 'ACTIVE' ? '进行中' : '已结束' }}</el-tag>
                <span style="margin-left:8px; color:var(--lc-text-muted); font-size:12px;">{{ cls.studentCount }} 人</span>
                <span style="margin-left:8px; color:var(--lc-text-muted); font-size:12px;">加入于 {{ formatDate(cls.joinTime) }}</span>
              </div>
            </div>
            <el-button size="small" type="danger" plain @click="leaveClass(cls)">退出</el-button>
          </div>
        </el-card>

        <!-- 加入班级对话框 -->
        <el-dialog title="加入班级" :visible.sync="joinDialogVisible" width="380px" :close-on-click-modal="false">
          <el-input v-model="joinCode" placeholder="请输入8位邀请码" maxlength="8" style="text-transform:uppercase;"></el-input>
          <span slot="footer">
            <el-button @click="joinDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="doJoinClass" :loading="joining">加入</el-button>
          </span>
        </el-dialog>

        <!-- 积分记录 -->
        <el-card shadow="hover" class="points-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-coin"></i> 积分记录</span>
            <el-tag type="info" size="small" style="float: right;">
              总积分: {{ userInfo.points || 0 }}
            </el-tag>
          </div>
          <el-table :data="pointsRecords" style="width: 100%" max-height="300">
            <el-table-column label="类型" width="120">
              <template slot-scope="scope">
                <el-tag :type="getPointsTypeTag(scope.row.type)" size="small">
                  {{ getPointsTypeText(scope.row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="说明"></el-table-column>
            <el-table-column label="积分" width="100">
              <template slot-scope="scope">
                <span :style="{color: scope.row.points > 0 ? '#67C23A' : '#F56C6C'}">
                  {{ scope.row.points > 0 ? '+' : '' }}{{ scope.row.points }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="160">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
          <div v-if="pointsRecords.length === 0" class="empty-records">
            <i class="el-icon-info"></i>
            <p>暂无积分记录</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      :visible.sync="passwordDialogVisible"
      width="450px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入旧密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码（6位以上）"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确定</el-button>
      </span>
    </el-dialog>

    <!-- 编辑资料对话框 -->
    <el-dialog
      title="编辑个人资料"
      :visible.sync="editDialogVisible"
      width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="目标岗位" prop="targetPosition">
          <el-input v-model="editForm.targetPosition" placeholder="请输入目标岗位"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    return {
      userInfo: JSON.parse(localStorage.getItem('user') || '{}'),
      editDialogVisible: false,
      passwordDialogVisible: false,
      editForm: {
        nickname: '',
        email: '',
        phone: '',
        targetPosition: ''
      },
      editRules: {
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      learningStats: [],
      pointsRecords: [],
      myClasses: [],
      joinDialogVisible: false,
      joinCode: '',
      joining: false
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadStatistics()
    this.loadPointsRecords()
    this.loadMyClasses()
  },
  methods: {
    loadUserInfo() {
      this.$http.get(`/users/${this.userInfo.id}`).then(res => {
        if (res.data) {
          this.userInfo = res.data
          localStorage.setItem('user', JSON.stringify(this.userInfo))

          // 填充编辑表单
          this.editForm = {
            nickname: this.userInfo.nickname || '',
            email: this.userInfo.email || '',
            phone: this.userInfo.phone || '',
            targetPosition: this.userInfo.targetPosition || ''
          }
        }
      }).catch(() => {})
    },
    loadMyClasses() {
      this.$http.get(`/classes/student/${this.userInfo.id}`).then(res => {
        this.myClasses = Array.isArray(res.data) ? res.data : []
      }).catch(() => { this.myClasses = [] })
    },
    doJoinClass() {
      if (!this.joinCode.trim()) {
        this.$message.warning('请输入邀请码')
        return
      }
      this.joining = true
      this.$http.post('/classes/join', {
        classCode: this.joinCode.trim().toUpperCase(),
        studentId: this.userInfo.id
      }).then(() => {
        this.$message.success('加入成功')
        this.joinDialogVisible = false
        this.joinCode = ''
        this.loadMyClasses()
      }).catch(err => {
        this.$message.error(err.message || '加入失败，请检查邀请码')
      }).finally(() => { this.joining = false })
    },
    leaveClass(cls) {
      this.$confirm(`确定退出班级"${cls.className}"吗？`, '提示', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.$http.post(`/classes/${cls.classId}/leave`, { studentId: this.userInfo.id }).then(() => {
          this.$message.success('已退出班级')
          this.loadMyClasses()
        })
      }).catch(() => {})
    },
    loadPointsRecords() {
      this.$http.get(`/points/records/${this.userInfo.id}`).then(res => {
        if (res.data) {
          // 只显示最近10条记录
          this.pointsRecords = res.data.slice(0, 10)
        }
      }).catch(() => {
        this.pointsRecords = []
      })
    },
    getPointsTypeText(type) {
      const types = {
        'DAILY_SIGNIN': '每日签到',
        'STUDY': '学习知识点',
        'PRACTICE_CORRECT': '答题正确',
        'PRACTICE_WRONG': '答题参与',
        'TEST_PASS': '测试通过',
        'INTERVIEW_GOOD': '面试良好',
        'SHARE': '分享内容',
        'CONTINUOUS_SIGNIN_7': '连续签到奖励',
        'EXCHANGE': '积分兑换'
      }
      return types[type] || type
    },
    getPointsTypeTag(type) {
      const tags = {
        'DAILY_SIGNIN': 'success',
        'STUDY': 'primary',
        'PRACTICE_CORRECT': 'success',
        'PRACTICE_WRONG': 'info',
        'TEST_PASS': 'warning',
        'INTERVIEW_GOOD': 'danger',
        'SHARE': 'success',
        'CONTINUOUS_SIGNIN_7': 'warning',
        'EXCHANGE': 'danger'
      }
      return tags[type] || 'info'
    },
    loadStatistics() {
      Promise.all([
        this.$http.get(`/knowledge/statistics/${this.userInfo.id}`),
        this.$http.get(`/test-records/statistics/${this.userInfo.id}`),
        this.$http.get(`/wrong-questions/statistics/${this.userInfo.id}`)
      ]).then(([knowledgeRes, testRes, wrongRes]) => {
        const kd = knowledgeRes.data || {}
        const td = testRes.data || {}
        const wd = wrongRes.data || {}
        this.learningStats = [
          {
            label: '已学知识点',
            value: kd.totalKnowledgePoints || 0,
            icon: 'el-icon-notebook-2',
            color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
          },
          {
            label: '练习题数',
            value: td.totalQuestions || 0,
            icon: 'el-icon-edit',
            color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
          },
          {
            label: '错题数',
            value: wd.totalWrongQuestions || 0,
            icon: 'el-icon-warning',
            color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
          },
          {
            label: '学习时长',
            value: Math.floor((kd.totalStudyTime || 0) / 60) + '分钟',
            icon: 'el-icon-time',
            color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
          },
          {
            label: '平均分',
            value: (td.averageScore || 0).toFixed(1) + '分',
            icon: 'el-icon-trophy',
            color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
          },
          {
            label: '完成率',
            value: (kd.completionRate || 0).toFixed(1) + '%',
            icon: 'el-icon-circle-check',
            color: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
          }
        ]
      }).catch(() => {})
    },
    saveProfile() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          this.$http.put(`/users/${this.userInfo.id}`, this.editForm).then(() => {
            this.$message.success('保存成功')
            this.editDialogVisible = false
            this.loadUserInfo()
          }).catch(() => {})
        }
      })
    },
    getRoleType(role) {
      const types = {
        'STUDENT': 'primary',
        'TEACHER': 'success'
      }
      return types[role] || 'info'
    },
    getRoleText(role) {
      const texts = {
        'STUDENT': '学生',
        'TEACHER': '教师'
      }
      return texts[role] || '用户'
    },
    changePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.$http.post(`/users/${this.userInfo.id}/change-password`, {
            oldPassword: this.passwordForm.oldPassword,
            newPassword: this.passwordForm.newPassword
          }).then(res => {
            if (res.code === 200) {
              this.$message.success('密码修改成功，请重新登录')
              this.passwordDialogVisible = false
              this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
              }
              // 2秒后跳转到登录页
              setTimeout(() => {
                localStorage.removeItem('user')
                this.$router.push('/login')
              }, 2000)
            }
          }).catch(err => {
            console.error('修改密码失败:', err)
          })
        }
      })
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
/* 现代化 Profile 页面 - 支持浅色/深色主题 */
.profile-container {
  padding: 0;
  animation: fadeInUp 0.4s ease;
}

/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-card__header {
  border-bottom: 1px solid var(--lc-border);
}

/* 个人信息卡片 */
.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px 0;
}

/deep/ .el-avatar {
  background: var(--lc-gradient-primary);
}

.username {
  margin: 15px 0 10px 0;
  color: var(--lc-text-primary);
}

.info-section {
  text-align: left;
  padding: 10px 20px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: var(--lc-text-secondary);
}

.info-item i {
  font-size: 18px;
  color: var(--lc-text-muted);
  margin-right: 10px;
  width: 20px;
}

.info-item .label {
  color: var(--lc-text-muted);
  font-size: 14px;
  min-width: 80px;
}

.info-item .value {
  color: var(--lc-text-primary);
  font-size: 14px;
  flex: 1;
}

.info-item .highlight {
  color: var(--lc-primary);
  font-weight: bold;
  font-size: 16px;
}

/* 统计卡片 */
.stats-card,
.activity-card,
.badge-card {
  height: auto;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--lc-text-primary);
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: var(--lc-bg-tertiary);
  border-radius: var(--lc-radius-xl);
  transition: all var(--lc-transition);
  border: 1px solid var(--lc-border);
}

.stat-item:hover {
  border-color: var(--lc-primary);
  box-shadow: var(--lc-shadow);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--lc-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--lc-text-primary);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  color: var(--lc-text-muted);
}

/* 积分记录 */
.points-card .empty-records {
  text-align: center;
  padding: 40px 0;
  color: var(--lc-text-muted);
}

.points-card .empty-records i {
  font-size: 48px;
  margin-bottom: 10px;
}

/* 班级信息 */
.class-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid var(--lc-border);
}

.class-item:last-child { border-bottom: none; }

.class-item-left { flex: 1; margin-right: 12px; }

.class-item-name {
  font-weight: 600;
  font-size: 15px;
  color: var(--lc-text-primary);
  margin-bottom: 4px;
}

.class-item-desc {
  font-size: 13px;
  color: var(--lc-text-muted);
  margin-bottom: 6px;
}

.class-item-meta {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 4px;
}

.class-info-card .empty-records {
  text-align: center;
  padding: 30px 0;
  color: var(--lc-text-muted);
}

.class-info-card .empty-records i {
  font-size: 40px;
  margin-bottom: 8px;
  display: block;
}

/* 表格样式 */
/deep/ .el-table {
  background: transparent !important;
  color: var(--lc-text-primary);
}

/deep/ .el-table tr {
  background: transparent !important;
}

/deep/ .el-table th {
  background: var(--lc-bg-tertiary) !important;
  color: var(--lc-text-secondary);
  border-bottom: 1px solid var(--lc-border);
}

/deep/ .el-table td {
  border-bottom: 1px solid var(--lc-border);
}

/* 按钮样式 */
/deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: var(--lc-text-inverse);
  font-weight: 600;
}

/* 表单样式 */
/deep/ .el-form-item__label {
  color: var(--lc-text-secondary);
}

/deep/ .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
}

/deep/ .el-select .el-input__inner {
  background: var(--lc-bg-input);
  border-color: var(--lc-border);
  color: var(--lc-text-primary);
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

/deep/ .el-tag {
  background: var(--lc-primary-bg);
  border-color: transparent;
  color: var(--lc-primary);
}

/* 动画 */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
