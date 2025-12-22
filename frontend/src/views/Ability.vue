<template>
  <div class="ability">
    <el-card class="ability-card">
      <div slot="header" class="header">
        <span class="title">
          <i class="el-icon-data-analysis"></i>
          能力评估模型
        </span>
        <el-tag :type="getLevelType(abilityModel.level)">{{ getLevelText(abilityModel.level) }}</el-tag>
      </div>

      <!-- 综合评分 -->
      <div class="overall-score">
        <div class="score-circle">
          <el-progress
            type="circle"
            :percentage="Math.round(abilityModel.overallScore || 0)"
            :width="160"
            :color="getScoreColor(abilityModel.overallScore)">
            <template slot="default">
              <div class="score-text">
                <div class="score-value">{{ (abilityModel.overallScore || 0).toFixed(1) }}</div>
                <div class="score-label">综合评分</div>
              </div>
            </template>
          </el-progress>
        </div>
        <div class="score-info">
          <h3>你的当前水平</h3>
          <p class="level-desc">{{ getLevelDescription(abilityModel.level) }}</p>
          <div class="target-position" v-if="user.targetPosition">
            <i class="el-icon-position"></i>
            <span>目标岗位：{{ user.targetPosition }}</span>
          </div>
        </div>
      </div>

      <el-divider></el-divider>

      <!-- 能力雷达图 -->
      <div class="radar-container">
        <h3 class="section-title">技术能力雷达图</h3>
        <div id="radarChart" style="width: 100%; height: 400px;"></div>
      </div>

      <el-divider></el-divider>

      <!-- 详细能力评分 -->
      <div class="ability-details">
        <h3 class="section-title">详细能力评分</h3>
        <el-row :gutter="20">
          <el-col :span="12" v-for="item in abilityItems" :key="item.key">
            <div class="ability-item">
              <div class="ability-label">
                <i :class="item.icon"></i>
                {{ item.label }}
              </div>
              <el-progress
                :percentage="Math.round(abilityModel[item.key] || 0)"
                :color="getProgressColor(abilityModel[item.key])">
              </el-progress>
              <div class="ability-value">{{ (abilityModel[item.key] || 0).toFixed(1) }}</div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-divider></el-divider>

      <!-- 提升建议 -->
      <div class="suggestions">
        <h3 class="section-title">提升建议</h3>
        <el-alert
          v-for="(suggestion, index) in suggestions"
          :key="index"
          :title="suggestion.title"
          :type="suggestion.type"
          :closable="false"
          show-icon
          style="margin-bottom: 15px;">
          <div>{{ suggestion.description }}</div>
          <div style="margin-top: 10px;">
            <el-button size="small" type="primary" @click="goToPractice(suggestion.category)">
              开始练习
            </el-button>
          </div>
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Ability',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      abilityModel: {
        algorithmAbility: 0,
        codingAbility: 0,
        systemDesignAbility: 0,
        databaseAbility: 0,
        networkAbility: 0,
        osAbility: 0,
        communicationAbility: 0,
        problemSolvingAbility: 0,
        overallScore: 0,
        level: 'BEGINNER'
      },
      abilityItems: [
        { key: 'algorithmAbility', label: '算法能力', icon: 'el-icon-set-up' },
        { key: 'codingAbility', label: '编码能力', icon: 'el-icon-edit' },
        { key: 'systemDesignAbility', label: '系统设计', icon: 'el-icon-finished' },
        { key: 'databaseAbility', label: '数据库', icon: 'el-icon-coin' },
        { key: 'networkAbility', label: '计算机网络', icon: 'el-icon-connection' },
        { key: 'osAbility', label: '操作系统', icon: 'el-icon-cpu' },
        { key: 'communicationAbility', label: '沟通能力', icon: 'el-icon-chat-dot-round' },
        { key: 'problemSolvingAbility', label: '问题解决', icon: 'el-icon-trophy' }
      ],
      radarChart: null
    }
  },
  computed: {
    suggestions() {
      const suggestions = []
      const threshold = 60

      if (this.abilityModel.algorithmAbility < threshold) {
        suggestions.push({
          title: '算法能力需要加强',
          description: '建议多练习数据结构和算法题目，掌握常见算法思想',
          type: 'warning',
          category: '算法'
        })
      }

      if (this.abilityModel.databaseAbility < threshold) {
        suggestions.push({
          title: '数据库知识有待提升',
          description: '建议学习SQL优化、索引原理、事务处理等核心知识',
          type: 'warning',
          category: '数据库'
        })
      }

      if (this.abilityModel.systemDesignAbility < threshold) {
        suggestions.push({
          title: '系统设计能力较弱',
          description: '建议学习高并发系统设计、分布式系统等知识',
          type: 'warning',
          category: '系统设计'
        })
      }

      if (suggestions.length === 0) {
        suggestions.push({
          title: '表现优秀！',
          description: '继续保持，可以尝试更高难度的题目和面试挑战',
          type: 'success',
          category: 'Java'
        })
      }

      return suggestions
    }
  },
  mounted() {
    this.loadAbilityModel()
  },
  methods: {
    loadAbilityModel() {
      this.$http.get(`/users/${this.user.id}/ability`).then(res => {
        if (res.data) {
          this.abilityModel = res.data
          this.$nextTick(() => {
            this.initRadarChart()
          })
        }
      }).catch(() => {})
    },
    initRadarChart() {
      const chartDom = document.getElementById('radarChart')
      this.radarChart = echarts.init(chartDom)

      const option = {
        tooltip: {
          trigger: 'item'
        },
        radar: {
          indicator: [
            { name: '算法能力', max: 100 },
            { name: '编码能力', max: 100 },
            { name: '系统设计', max: 100 },
            { name: '数据库', max: 100 },
            { name: '计算机网络', max: 100 },
            { name: '操作系统', max: 100 },
            { name: '沟通能力', max: 100 },
            { name: '问题解决', max: 100 }
          ],
          shape: 'polygon',
          splitNumber: 5,
          name: {
            textStyle: {
              color: '#606266',
              fontSize: 13
            }
          },
          splitLine: {
            lineStyle: {
              color: '#E4E7ED'
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: ['#F2F6FC', '#FFFFFF']
            }
          },
          axisLine: {
            lineStyle: {
              color: '#DCDFE6'
            }
          }
        },
        series: [{
          name: '能力评估',
          type: 'radar',
          data: [{
            value: [
              this.abilityModel.algorithmAbility || 0,
              this.abilityModel.codingAbility || 0,
              this.abilityModel.systemDesignAbility || 0,
              this.abilityModel.databaseAbility || 0,
              this.abilityModel.networkAbility || 0,
              this.abilityModel.osAbility || 0,
              this.abilityModel.communicationAbility || 0,
              this.abilityModel.problemSolvingAbility || 0
            ],
            name: '当前能力',
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.3)'
            },
            lineStyle: {
              width: 2
            }
          }]
        }]
      }

      this.radarChart.setOption(option)

      // 响应式
      window.addEventListener('resize', () => {
        this.radarChart && this.radarChart.resize()
      })
    },
    getLevelType(level) {
      const types = {
        'BEGINNER': 'info',
        'INTERMEDIATE': 'warning',
        'ADVANCED': 'success',
        'EXPERT': 'danger'
      }
      return types[level] || 'info'
    },
    getLevelText(level) {
      const texts = {
        'BEGINNER': '初级',
        'INTERMEDIATE': '中级',
        'ADVANCED': '高级',
        'EXPERT': '专家'
      }
      return texts[level] || '初级'
    },
    getLevelDescription(level) {
      const descriptions = {
        'BEGINNER': '你正处于学习的起步阶段，建议系统地学习基础知识，多做练习题',
        'INTERMEDIATE': '你已经掌握了基础知识，可以开始挑战中等难度的题目和项目',
        'ADVANCED': '你的技术能力较强，可以尝试系统设计类题目和高级面试挑战',
        'EXPERT': '你已经是该领域的专家，继续保持并分享你的经验给其他人'
      }
      return descriptions[level] || '继续努力！'
    },
    getScoreColor(score) {
      if (score >= 85) return '#67C23A'
      if (score >= 70) return '#E6A23C'
      if (score >= 60) return '#409EFF'
      return '#F56C6C'
    },
    getProgressColor(value) {
      if (value >= 80) return '#67C23A'
      if (value >= 60) return '#409EFF'
      if (value >= 40) return '#E6A23C'
      return '#F56C6C'
    },
    goToPractice(category) {
      this.$router.push(`/home/test/${category}`)
    }
  },
  beforeDestroy() {
    if (this.radarChart) {
      this.radarChart.dispose()
    }
    window.removeEventListener('resize', this.radarChart && this.radarChart.resize)
  }
}
</script>

<style scoped>
.ability {
  padding: 20px;
  animation: fadeInUp 0.4s ease;
}

/* 现代化主题 - 支持浅色/深色 */
.ability-card {
  max-width: 1200px;
  margin: 0 auto;
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

/deep/ .el-card {
  background: var(--lc-bg-card) !important;
  border: 1px solid var(--lc-border) !important;
  border-radius: var(--lc-radius-xl);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: var(--lc-text-primary);
}

.title i {
  margin-right: 8px;
  color: var(--lc-primary);
}

/* 综合评分 */
.overall-score {
  display: flex;
  align-items: center;
  gap: 60px;
  padding: 30px 0;
}

.score-circle {
  flex-shrink: 0;
}

.score-text {
  text-align: center;
}

.score-value {
  font-size: 32px;
  font-weight: bold;
  color: var(--lc-primary);
  line-height: 1;
}

.score-label {
  font-size: 14px;
  color: var(--lc-text-muted);
  margin-top: 8px;
}

.score-info h3 {
  margin-bottom: 15px;
  color: var(--lc-text-primary);
}

.level-desc {
  color: var(--lc-text-secondary);
  line-height: 1.8;
  margin-bottom: 15px;
}

.target-position {
  display: inline-flex;
  align-items: center;
  padding: 8px 16px;
  background: var(--lc-primary-bg);
  border-radius: var(--lc-radius);
  color: var(--lc-primary);
  font-size: 14px;
}

.target-position i {
  margin-right: 8px;
}

/* 雷达图 */
.radar-container {
  padding: 20px 0;
}

.section-title {
  margin-bottom: 30px;
  color: var(--lc-text-primary);
  font-size: 18px;
  font-weight: 600;
  padding-left: 12px;
  border-left: 4px solid var(--lc-primary);
}

/* 能力详情 */
.ability-details {
  padding: 20px 0;
}

.ability-item {
  background: var(--lc-bg-tertiary);
  padding: 20px;
  border-radius: var(--lc-radius-xl);
  margin-bottom: 16px;
  position: relative;
  border: 1px solid var(--lc-border);
}

.ability-label {
  font-size: 14px;
  color: var(--lc-text-primary);
  margin-bottom: 12px;
  font-weight: 500;
}

.ability-label i {
  margin-right: 8px;
  color: var(--lc-primary);
}

.ability-value {
  position: absolute;
  right: 20px;
  top: 20px;
  font-size: 18px;
  font-weight: bold;
  color: var(--lc-primary);
}

/deep/ .el-progress-bar__outer {
  background: var(--lc-bg-tertiary);
}

/deep/ .el-progress-bar__inner {
  background: var(--lc-gradient-primary);
}

/* 建议 */
.suggestions {
  padding: 20px 0;
}

/deep/ .el-alert {
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
}

/deep/ .el-alert__title {
  color: var(--lc-text-primary);
}

/deep/ .el-alert__description {
  color: var(--lc-text-secondary);
}

/deep/ .el-button--primary {
  background: var(--lc-gradient-primary);
  border: none;
  color: var(--lc-text-inverse);
  font-weight: 600;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
