<template>
  <div class="dashboard">
    <!-- Welcome Banner -->
    <el-card class="welcome-card" shadow="hover">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>{{ getGreeting() }}，{{ user.username }}！👋</h2>
          <p>准备好开始今天的学习了吗？这里是你目前的学习进度概览。</p>
          <div class="quick-info">
            <el-tag v-if="user.targetPosition" type="primary" effect="plain" style="margin-right: 10px;">
              <i class="el-icon-position"></i> 目标岗位: {{ user.targetPosition }}
            </el-tag>
            <el-tag type="warning" effect="plain">
              <i class="el-icon-coin"></i> 积分: {{ user.points || 0 }}
            </el-tag>
          </div>
        </div>
        <div class="welcome-actions">
          <el-button
            type="primary"
            :disabled="pointsStats.todaySigned"
            @click="dailySignIn"
            :loading="signingIn">
            <i class="el-icon-medal"></i>
            {{ pointsStats.todaySigned ? '已签到' : '每日签到' }}
          </el-button>
          <div v-if="pointsStats.continuousSignInDays > 0" class="signin-days">
            连续签到 {{ pointsStats.continuousSignInDays }} 天 🔥
          </div>
        </div>
      </div>
    </el-card>

    <!-- Statistics Row -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="6" v-for="(item, index) in statItems" :key="index">
        <el-card shadow="hover" class="stat-card" @click.native="handleStatClick(item.route)">
          <div class="stat-icon-wrapper" :style="{ background: item.color }">
            <i :class="item.icon"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
            <div class="stat-trend" v-if="item.trend">
              <i :class="item.trend > 0 ? 'el-icon-top' : 'el-icon-bottom'"
                 :style="{color: item.trend > 0 ? '#67C23A' : '#F56C6C'}"></i>
              <span :style="{color: item.trend > 0 ? '#67C23A' : '#F56C6C'}">
                {{ Math.abs(item.trend) }}%
              </span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- Study Progress Chart -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-data-line"></i> 学习进度统计</span>
          </div>
          <div id="studyChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>

      <!-- Category Performance -->
      <el-col :span="12">
        <el-card shadow="hover" class="chart-card">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-pie-chart"></i> 分类练习分析</span>
          </div>
          <div id="categoryChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <!-- Recent Tutorials -->
      <el-col :span="16">
        <el-card shadow="hover" class="content-card">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-reading"></i> 最新教程</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="$router.push('/home/tutorials')">全部教程</el-button>
          </div>
          <el-table :data="recentTutorials" style="width: 100%" :show-header="false" v-loading="loading.tutorials">
            <el-table-column width="60">
              <template>
                <el-tag size="mini" type="info">教程</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题">
              <template slot-scope="scope">
                <span class="tutorial-link" @click="viewTutorial(scope.row.id)">{{ scope.row.title }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" align="right">
              <template slot-scope="scope">
                <el-tag size="small" effect="plain">{{ scope.row.category }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column width="100" align="right">
              <template slot-scope="scope">
                <span class="date-text">{{ formatDate(scope.row.createdAt) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- Quick Actions -->
      <el-col :span="8">
        <el-card shadow="hover" class="content-card">
          <div slot="header" class="clearfix">
            <span class="card-title"><i class="el-icon-s-promotion"></i> 快速开始</span>
          </div>
          <div class="action-grid">
            <div class="action-item" @click="$router.push('/home/practice')">
              <div class="action-icon" style="background: #e1f3d8; color: #67C23A">
                <i class="el-icon-edit"></i>
              </div>
              <span>刷题练习</span>
            </div>
            <div class="action-item" @click="$router.push('/home/interview')">
              <div class="action-icon" style="background: #fdf6ec; color: #E6A23C">
                <i class="el-icon-microphone"></i>
              </div>
              <span>模拟面试</span>
            </div>
            <div class="action-item" @click="$router.push('/home/knowledge')">
              <div class="action-icon" style="background: #f0f9ff; color: #409EFF">
                <i class="el-icon-notebook-2"></i>
              </div>
              <span>知识学习</span>
            </div>
            <div class="action-item" @click="$router.push('/home/wrong-questions')">
              <div class="action-icon" style="background: #fef0f0; color: #F56C6C">
                <i class="el-icon-warning-outline"></i>
              </div>
              <span>我的错题</span>
            </div>
            <div class="action-item" @click="$router.push('/home/ability')">
              <div class="action-icon" style="background: #f4f4f5; color: #909399">
                <i class="el-icon-data-analysis"></i>
              </div>
              <span>能力评估</span>
            </div>
            <div class="action-item" @click="$router.push('/home/favorites')">
              <div class="action-icon" style="background: #fff7e6; color: #E6A23C">
                <i class="el-icon-star-on"></i>
              </div>
              <span>我的收藏</span>
            </div>
            <div class="action-item" @click="$router.push('/home/study-report')">
              <div class="action-icon" style="background: #f0f9ff; color: #409EFF">
                <i class="el-icon-s-marketing"></i>
              </div>
              <span>学习报告</span>
            </div>
            <div class="action-item" @click="$router.push('/home/records')">
              <div class="action-icon" style="background: #ecf5ff; color: #409EFF">
                <i class="el-icon-document"></i>
              </div>
              <span>成绩记录</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      stats: {
        tutorialCount: 0,
        questionCount: 0,
        testCount: 0,
        avgScore: 0,
        wrongQuestionCount: 0,
        knowledgeCount: 0
      },
      pointsStats: {
        totalPoints: 0,
        todayPoints: 0,
        continuousSignInDays: 0,
        todaySigned: false
      },
      signingIn: false,
      recentTutorials: [],
      loading: {
        tutorials: false
      },
      studyChart: null,
      categoryChart: null,
      categoryData: {}
    }
  },
  computed: {
    statItems() {
      return [
        {
          label: '已学知识点',
          value: this.stats.knowledgeCount,
          icon: 'el-icon-notebook-2',
          color: '#409EFF',
          trend: 12,
          route: '/home/knowledge'
        },
        {
          label: '练习题目数',
          value: this.stats.questionCount,
          icon: 'el-icon-edit',
          color: '#67C23A',
          trend: 8,
          route: '/home/practice'
        },
        {
          label: '错题数量',
          value: this.stats.wrongQuestionCount,
          icon: 'el-icon-warning-outline',
          color: '#E6A23C',
          trend: -15,
          route: '/home/wrong-questions'
        },
        {
          label: '平均分数',
          value: this.stats.avgScore + '%',
          icon: 'el-icon-trophy',
          color: '#F56C6C',
          trend: 5,
          route: '/home/ability'
        }
      ]
    }
  },
  mounted() {
    this.loadData()
    this.loadPointsStats()
    this.initCharts()
  },
  beforeDestroy() {
    if (this.studyChart) {
      this.studyChart.dispose()
    }
    if (this.categoryChart) {
      this.categoryChart.dispose()
    }
  },
  methods: {
    getGreeting() {
      const hour = new Date().getHours()
      if (hour < 12) return '早安'
      if (hour < 18) return '下午好'
      return '晚上好'
    },
    loadData() {
      const user = this.user

      // 加载教程
      this.loading.tutorials = true
      this.$http.get('/tutorials').then(res => {
        if (res.data.code === 200) {
          this.stats.tutorialCount = res.data.data.length
          this.recentTutorials = res.data.data.slice(0, 5)
        }
      }).finally(() => {
        this.loading.tutorials = false
      })

      // 加载题目数量
      this.$http.get('/questions').then(res => {
        if (res.data.code === 200) {
          this.stats.questionCount = res.data.data.length
        }
      })

      // 加载测试统计
      this.$http.get(`/test-records/statistics/${user.id}`).then(res => {
        if (res.data.code === 200) {
          this.stats.testCount = res.data.data.totalTests
          this.stats.avgScore = res.data.data.averageScore ? res.data.data.averageScore.toFixed(1) : 0
          this.categoryData = res.data.data.categoryAvgScore || {}
          this.updateCategoryChart()
        }
      })

      // 加载错题统计
      this.$http.get(`/wrong-questions/statistics/${user.id}`).then(res => {
        if (res.data.code === 200) {
          this.stats.wrongQuestionCount = res.data.data.totalWrongQuestions || 0
        }
      })

      // 加载知识点学习统计
      this.$http.get(`/knowledge/statistics/${user.id}`).then(res => {
        if (res.data.code === 200) {
          this.stats.knowledgeCount = res.data.data.totalKnowledgePoints || 0
          this.updateStudyChart(res.data.data)
        }
      })
    },
    initCharts() {
      this.studyChart = echarts.init(document.getElementById('studyChart'))
      this.categoryChart = echarts.init(document.getElementById('categoryChart'))

      window.addEventListener('resize', () => {
        this.studyChart && this.studyChart.resize()
        this.categoryChart && this.categoryChart.resize()
      })
    },
    updateStudyChart(data) {
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          top: '5%',
          left: 'center'
        },
        series: [
          {
            name: '学习进度',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: data.completedCount || 0, name: '已完成', itemStyle: { color: '#67C23A' } },
              { value: data.inProgressCount || 0, name: '学习中', itemStyle: { color: '#E6A23C' } },
              {
                value: Math.max(0, (data.totalKnowledgePoints || 0) - (data.completedCount || 0) - (data.inProgressCount || 0)),
                name: '未开始',
                itemStyle: { color: '#909399' }
              }
            ]
          }
        ]
      }
      this.studyChart.setOption(option)
    },
    updateCategoryChart() {
      const categories = Object.keys(this.categoryData)
      const values = Object.values(this.categoryData)

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisTick: {
            alignWithLabel: true
          }
        },
        yAxis: {
          type: 'value',
          max: 100
        },
        series: [
          {
            name: '平均分',
            type: 'bar',
            barWidth: '60%',
            data: values,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            }
          }
        ]
      }
      this.categoryChart.setOption(option)
    },
    handleStatClick(route) {
      if (route) {
        this.$router.push(route)
      }
    },
    viewTutorial(id) {
      this.$router.push(`/home/tutorial/${id}`)
    },
    loadPointsStats() {
      this.$http.get(`/points/stats/${this.user.id}`).then(res => {
        if (res.data.code === 200) {
          this.pointsStats = res.data.data
          // 更新用户积分显示
          this.user.points = res.data.data.totalPoints
        }
      }).catch(() => {
        // 初始化默认值
        this.pointsStats = {
          totalPoints: 0,
          todayPoints: 0,
          continuousSignInDays: 0,
          todaySigned: false
        }
      })
    },
    dailySignIn() {
      this.signingIn = true
      this.$http.post(`/points/signin/${this.user.id}`).then(res => {
        if (res.data.code === 200) {
          const result = res.data.data
          this.$message.success(result.message)
          this.loadPointsStats() // 重新加载积分统计

          // 更新本地用户信息
          this.user.points = (this.user.points || 0) + result.points
          localStorage.setItem('user', JSON.stringify(this.user))
        } else {
          this.$message.warning(res.data.message)
        }
      }).catch(err => {
        this.$message.error('签到失败')
      }).finally(() => {
        this.signingIn = false
      })
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return `${date.getMonth() + 1}-${date.getDate()}`
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.mt-20 {
  margin-top: 20px;
}

/* Welcome Card */
.welcome-card {
  background: #fff;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text {
  flex: 1;
}

.welcome-text h2 {
  margin-bottom: 10px;
  color: #303133;
}

.welcome-text p {
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
}

.quick-info {
  margin-top: 15px;
}

.welcome-actions {
  text-align: center;
  margin-left: 40px;
}

.signin-days {
  margin-top: 10px;
  color: #E6A23C;
  font-size: 12px;
  font-weight: 600;
}

/* Stat Cards */
.stat-card {
  border: none;
  transition: all 0.3s;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.stat-card /deep/ .el-card__body {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: #fff;
  font-size: 24px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 5px;
}

.stat-trend {
  font-size: 12px;
  margin-top: 5px;
}

.stat-trend i {
  margin-right: 2px;
}

.quick-info {
  margin-top: 15px;
}

/* Content Cards */
.content-card {
  height: 100%;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.tutorial-link {
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.tutorial-link:hover {
  color: #409EFF;
}

.date-text {
  color: #C0C4CC;
  font-size: 12px;
}

/* Charts */
.chart-card {
  height: 420px;
}

/* Action Grid */
.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f9fafc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #ebeef5;
}

.action-item:hover {
  background: #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transform: translateY(-2px);
  border-color: #dcdfe6;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 10px;
}

.action-item span {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}
</style>
