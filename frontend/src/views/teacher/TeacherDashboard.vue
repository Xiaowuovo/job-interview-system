<template>
  <div class="teacher-dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: item.color }">
              <i :class="item.icon"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ item.value }}</div>
              <div class="stat-title">{{ item.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>快速操作</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" icon="el-icon-edit" @click="goToQuestionCreate">创建题目</el-button>
            <el-button type="success" icon="el-icon-document-add" @click="goToTutorialCreate">创建教程</el-button>
            <el-button type="warning" icon="el-icon-collection" @click="goToCourseCreate">创建课程</el-button>
            <el-button type="info" icon="el-icon-reading" @click="goToKnowledgeCreate">创建知识点</el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <div slot="header">
            <span>最近创建</span>
          </div>
          <el-table :data="recentItems" style="width: 100%" max-height="300">
            <el-table-column prop="type" label="类型" width="80">
              <template slot-scope="scope">
                <el-tag :type="getTypeTag(scope.row.type)" size="small">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" show-overflow-tooltip></el-table-column>
            <el-table-column prop="createdAt" label="创建时间" width="160">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <el-card>
          <div slot="header">
            <span>数据趋势</span>
          </div>
          <div id="trendChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'TeacherDashboard',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      stats: [
        {
          title: '我的题目',
          value: 0,
          icon: 'el-icon-edit-outline',
          color: '#409EFF'
        },
        {
          title: '我的教程',
          value: 0,
          icon: 'el-icon-document',
          color: '#67C23A'
        },
        {
          title: '我的课程',
          value: 0,
          icon: 'el-icon-collection',
          color: '#E6A23C'
        },
        {
          title: '我的学生',
          value: 0,
          icon: 'el-icon-user',
          color: '#F56C6C'
        }
      ],
      recentItems: [],
      chart: null
    }
  },
  mounted() {
    this.loadStats()
    this.loadRecentItems()
    this.initChart()
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
  },
  methods: {
    loadStats() {
      this.$http.get(`/questions/teacher/${this.user.id}/count`).then(res => {
        this.stats[0].value = res.data || 0
      }).catch(() => {})

      this.$http.get(`/tutorials/teacher/${this.user.id}/count`).then(res => {
        this.stats[1].value = res.data || 0
      }).catch(() => {})

      this.$http.get(`/teacher/course/my-courses?teacherId=${this.user.id}`).then(res => {
        this.stats[2].value = res.data?.length || 0
      }).catch(() => {})
    },
    loadRecentItems() {
      Promise.all([
        this.$http.get(`/questions?teacherId=${this.user.id}&limit=3`).catch(() => ({ data: [] })),
        this.$http.get(`/tutorials?teacherId=${this.user.id}&limit=3`).catch(() => ({ data: [] })),
        this.$http.get(`/teacher/course/my-courses?teacherId=${this.user.id}`).catch(() => ({ data: [] }))
      ]).then(([questions, tutorials, courses]) => {
        const items = []
        
        if (questions.data) {
          questions.data.forEach(q => {
            items.push({
              type: '题目',
              title: q.title,
              createdAt: q.createdAt
            })
          })
        }
        
        if (tutorials.data) {
          tutorials.data.forEach(t => {
            items.push({
              type: '教程',
              title: t.title,
              createdAt: t.createdAt
            })
          })
        }
        
        if (courses.data) {
          courses.data.slice(0, 3).forEach(c => {
            items.push({
              type: '课程',
              title: c.title,
              createdAt: c.createdAt
            })
          })
        }
        
        items.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        this.recentItems = items.slice(0, 5)
      })
    },
    initChart() {
      this.chart = echarts.init(document.getElementById('trendChart'))
      const option = {
        title: {
          text: '过去7天创建内容统计',
          left: 'center'
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['题目', '教程', '课程', '知识点'],
          bottom: 0
        },
        xAxis: {
          type: 'category',
          data: this.getLast7Days()
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '题目',
            type: 'line',
            data: [5, 3, 6, 4, 7, 5, 8],
            smooth: true
          },
          {
            name: '教程',
            type: 'line',
            data: [2, 1, 3, 2, 4, 3, 5],
            smooth: true
          },
          {
            name: '课程',
            type: 'line',
            data: [1, 2, 1, 3, 2, 4, 3],
            smooth: true
          },
          {
            name: '知识点',
            type: 'line',
            data: [3, 4, 5, 6, 5, 7, 6],
            smooth: true
          }
        ]
      }
      this.chart.setOption(option)
    },
    getLast7Days() {
      const days = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        days.push(`${date.getMonth() + 1}/${date.getDate()}`)
      }
      return days
    },
    goToQuestionCreate() {
      this.$router.push('/teacher/questions/create')
    },
    goToTutorialCreate() {
      this.$router.push('/teacher/tutorials/create')
    },
    goToCourseCreate() {
      this.$router.push('/teacher/courses/create')
    },
    goToKnowledgeCreate() {
      this.$router.push('/teacher/knowledge/create')
    },
    getTypeTag(type) {
      const tags = {
        '题目': 'primary',
        '教程': 'success',
        '课程': 'warning',
        '知识点': 'info'
      }
      return tags[type] || ''
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
.teacher-dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon i {
  font-size: 30px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 120px;
}
</style>
