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
            <div class="info-item" v-if="userInfo.membershipType && userInfo.membershipType !== 'FREE'">
              <i class="el-icon-medal"></i>
              <span class="label">会员：</span>
              <el-tag :type="getMembershipType(userInfo.membershipType)" size="small">
                {{ getMembershipText(userInfo.membershipType) }}
              </el-tag>
            </div>
          </div>

          <el-divider></el-divider>

          <el-button type="primary" style="width: 100%;" @click="editDialogVisible = true">
            <i class="el-icon-edit"></i> 编辑资料
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

        <!-- 最近活动 -->
        <el-card shadow="hover" class="activity-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-time"></i> 最近活动</span>
          </div>
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in recentActivities"
              :key="index"
              :timestamp="activity.time"
              :color="activity.color">
              <i :class="activity.icon" style="margin-right: 8px;"></i>
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>

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

        <!-- 成就徽章 -->
        <el-card shadow="hover" class="badge-card" style="margin-top: 20px;">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-trophy"></i> 成就徽章</span>
          </div>
          <div class="badges-grid">
            <div
              v-for="(badge, index) in badges"
              :key="index"
              class="badge-item"
              :class="{unlocked: badge.unlocked}">
              <div class="badge-icon">
                <i :class="badge.icon"></i>
              </div>
              <div class="badge-name">{{ badge.name }}</div>
              <div class="badge-desc">{{ badge.description }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
    return {
      userInfo: JSON.parse(localStorage.getItem('user') || '{}'),
      editDialogVisible: false,
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
      learningStats: [],
      recentActivities: [],
      pointsRecords: [],
      badges: [
        { name: '初学者', icon: 'el-icon-star-off', description: '注册账号', unlocked: true },
        { name: '勤奋者', icon: 'el-icon-trophy', description: '连续学习7天', unlocked: false },
        { name: '刷题达人', icon: 'el-icon-medal', description: '完成100道题', unlocked: false },
        { name: '知识探索者', icon: 'el-icon-discover', description: '学习20个知识点', unlocked: false },
        { name: '面试高手', icon: 'el-icon-user', description: '完成10次模拟面试', unlocked: false },
        { name: '精益求精', icon: 'el-icon-star-on', description: '清空错题本', unlocked: false }
      ]
    }
  },
  mounted() {
    this.loadUserInfo()
    this.loadStatistics()
    this.loadActivities()
    this.loadPointsRecords()
  },
  methods: {
    loadUserInfo() {
      this.$http.get(`/users/${this.userInfo.id}`).then(res => {
        if (res.data.code === 200) {
          this.userInfo = res.data.data
          localStorage.setItem('user', JSON.stringify(this.userInfo))

          // 填充编辑表单
          this.editForm = {
            nickname: this.userInfo.nickname || '',
            email: this.userInfo.email || '',
            phone: this.userInfo.phone || '',
            targetPosition: this.userInfo.targetPosition || ''
          }
        }
      })
    },
    loadPointsRecords() {
      this.$http.get(`/points/records/${this.userInfo.id}`).then(res => {
        if (res.data.code === 200) {
          // 只显示最近10条记录
          this.pointsRecords = res.data.data.slice(0, 10)
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
      // 加载学习统计
      Promise.all([
        this.$http.get(`/knowledge/statistics/${this.userInfo.id}`),
        this.$http.get(`/test-records/statistics/${this.userInfo.id}`),
        this.$http.get(`/wrong-questions/statistics/${this.userInfo.id}`)
      ]).then(([knowledgeRes, testRes, wrongRes]) => {
        this.learningStats = [
          {
            label: '已学知识点',
            value: knowledgeRes.data.data?.totalKnowledgePoints || 0,
            icon: 'el-icon-notebook-2',
            color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
          },
          {
            label: '练习题数',
            value: testRes.data.data?.totalQuestions || 0,
            icon: 'el-icon-edit',
            color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
          },
          {
            label: '错题数',
            value: wrongRes.data.data?.totalWrongQuestions || 0,
            icon: 'el-icon-warning',
            color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
          },
          {
            label: '学习时长',
            value: Math.floor((knowledgeRes.data.data?.totalStudyTime || 0) / 60) + '分钟',
            icon: 'el-icon-time',
            color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
          },
          {
            label: '平均分',
            value: (testRes.data.data?.averageScore || 0).toFixed(1) + '分',
            icon: 'el-icon-trophy',
            color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
          },
          {
            label: '完成率',
            value: (knowledgeRes.data.data?.completionRate || 0).toFixed(1) + '%',
            icon: 'el-icon-circle-check',
            color: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
          }
        ]
      })
    },
    loadActivities() {
      // 模拟最近活动数据
      this.recentActivities = [
        {
          time: '2小时前',
          content: '完成了Java集合框架的学习',
          icon: 'el-icon-success',
          color: '#67C23A'
        },
        {
          time: '昨天',
          content: '参加了技术面试模拟',
          icon: 'el-icon-microphone',
          color: '#E6A23C'
        },
        {
          time: '2天前',
          content: '完成了10道算法题',
          icon: 'el-icon-edit',
          color: '#409EFF'
        },
        {
          time: '3天前',
          content: '清空了5道错题',
          icon: 'el-icon-circle-check',
          color: '#67C23A'
        }
      ]
    },
    saveProfile() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          this.$http.put(`/users/${this.userInfo.id}`, this.editForm).then(res => {
            if (res.data.code === 200) {
              this.$message.success('保存成功')
              this.editDialogVisible = false
              this.loadUserInfo()
            }
          })
        }
      })
    },
    getRoleType(role) {
      const types = {
        'STUDENT': 'primary',
        'TEACHER': 'success',
        'ADMIN': 'danger'
      }
      return types[role] || 'info'
    },
    getRoleText(role) {
      const texts = {
        'STUDENT': '学生',
        'TEACHER': '教师',
        'ADMIN': '管理员'
      }
      return texts[role] || '用户'
    },
    getMembershipType(type) {
      const types = {
        'VIP': 'warning',
        'ENTERPRISE': 'danger'
      }
      return types[type] || 'info'
    },
    getMembershipText(type) {
      const texts = {
        'VIP': 'VIP会员',
        'ENTERPRISE': '企业会员'
      }
      return texts[type] || '普通用户'
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
}

/* 个人信息卡片 */
.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 20px 0;
}

.username {
  margin: 15px 0 10px 0;
  color: #303133;
}

.info-section {
  text-align: left;
  padding: 10px 20px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #606266;
}

.info-item i {
  font-size: 18px;
  color: #909399;
  margin-right: 10px;
  width: 20px;
}

.info-item .label {
  color: #909399;
  font-size: 14px;
  min-width: 80px;
}

.info-item .value {
  color: #303133;
  font-size: 14px;
  flex: 1;
}

.info-item .highlight {
  color: #E6A23C;
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
  color: #303133;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f9fafc;
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-item:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
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
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

/* 成就徽章 */
.badges-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.badge-item {
  text-align: center;
  padding: 20px;
  background: #f9fafc;
  border-radius: 8px;
  transition: all 0.3s;
  opacity: 0.5;
}

.badge-item.unlocked {
  opacity: 1;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.badge-item.unlocked:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.badge-icon {
  font-size: 48px;
  color: #E6A23C;
  margin-bottom: 10px;
}

.badge-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.badge-desc {
  font-size: 12px;
  color: #909399;
}

/* 积分记录 */
.points-card .empty-records {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.points-card .empty-records i {
  font-size: 48px;
  margin-bottom: 10px;
}
</style>
