<template>
  <div class="lc-layout">
    <!-- 侧边栏 -->
    <aside class="lc-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="lc-logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="currentColor">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/>
          </svg>
        </div>
        <span class="logo-text" v-show="!sidebarCollapsed">InterviewPro</span>
      </div>
      
      <nav class="lc-nav">
        <div class="nav-section">
          <div class="nav-section-title" v-show="!sidebarCollapsed">学习</div>
          <router-link to="/home/dashboard" class="nav-item" :class="{ active: $route.path === '/home/dashboard' }">
            <i class="el-icon-s-home"></i>
            <span v-show="!sidebarCollapsed">仪表盘</span>
          </router-link>
          <router-link to="/home/tutorials" class="nav-item" :class="{ active: $route.path === '/home/tutorials' }">
            <i class="el-icon-reading"></i>
            <span v-show="!sidebarCollapsed">面试教程</span>
          </router-link>
          <router-link to="/home/knowledge" class="nav-item" :class="{ active: $route.path === '/home/knowledge' }">
            <i class="el-icon-notebook-2"></i>
            <span v-show="!sidebarCollapsed">知识库</span>
          </router-link>
        </div>
        
        <div class="nav-section">
          <div class="nav-section-title" v-show="!sidebarCollapsed">练习</div>
          <router-link to="/home/practice" class="nav-item" :class="{ active: $route.path.includes('/home/practice') || $route.path.includes('/home/test') }">
            <i class="el-icon-edit-outline"></i>
            <span v-show="!sidebarCollapsed">题目练习</span>
          </router-link>
          <router-link to="/home/interview" class="nav-item" :class="{ active: $route.path === '/home/interview' }">
            <i class="el-icon-microphone"></i>
            <span v-show="!sidebarCollapsed">AI面试</span>
            <span class="nav-badge" v-show="!sidebarCollapsed">AI</span>
          </router-link>
          <router-link to="/home/wrong-questions" class="nav-item" :class="{ active: $route.path === '/home/wrong-questions' }">
            <i class="el-icon-warning-outline"></i>
            <span v-show="!sidebarCollapsed">错题本</span>
          </router-link>
          <router-link to="/home/favorites" class="nav-item" :class="{ active: $route.path === '/home/favorites' }">
            <i class="el-icon-star-off"></i>
            <span v-show="!sidebarCollapsed">收藏夹</span>
          </router-link>
        </div>
        
        <div class="nav-section">
          <div class="nav-section-title" v-show="!sidebarCollapsed">分析</div>
          <router-link to="/home/ability" class="nav-item" :class="{ active: $route.path === '/home/ability' }">
            <i class="el-icon-data-analysis"></i>
            <span v-show="!sidebarCollapsed">能力评估</span>
          </router-link>
          <router-link to="/home/study-report" class="nav-item" :class="{ active: $route.path === '/home/study-report' }">
            <i class="el-icon-s-marketing"></i>
            <span v-show="!sidebarCollapsed">学习报告</span>
          </router-link>
          <router-link to="/home/records" class="nav-item" :class="{ active: $route.path === '/home/records' }">
            <i class="el-icon-document"></i>
            <span v-show="!sidebarCollapsed">成绩记录</span>
          </router-link>
        </div>
      </nav>
      
      <div class="sidebar-footer">
        <button class="collapse-btn" @click="toggleSidebar">
          <i :class="sidebarCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
        </button>
      </div>
    </aside>
    
    <!-- 主内容区 -->
    <div class="lc-main">
      <!-- 顶部导航 -->
      <header class="lc-header">
        <div class="header-left">
          <div class="page-title">{{ getPageTitle() }}</div>
        </div>
        <div class="header-right">
          <div class="header-search">
            <i class="el-icon-search"></i>
            <input type="text" placeholder="搜索题目、知识点..." />
          </div>
          <div class="header-actions">
            <button class="theme-toggle-btn" @click="handleToggleTheme" :title="currentTheme === 'dark' ? '切换到浅色模式' : '切换到深色模式'">
              <i :class="currentTheme === 'dark' ? 'el-icon-sunny' : 'el-icon-moon'"></i>
            </button>
            <button class="action-btn" title="通知">
              <i class="el-icon-bell"></i>
              <span class="badge">3</span>
            </button>
            <el-dropdown trigger="click" @command="handleCommand">
              <div class="user-avatar">
                <div class="avatar-circle">{{ user.username ? user.username.charAt(0).toUpperCase() : 'U' }}</div>
                <span class="user-name">{{ user.username }}</span>
                <i class="el-icon-arrow-down"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="lc-dropdown">
                <el-dropdown-item command="profile">
                  <i class="el-icon-user"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <i class="el-icon-setting"></i> 设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </header>
      
      <!-- 内容区 -->
      <main class="lc-content">
        <transition name="fade" mode="out-in">
          <router-view/>
        </transition>
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}'),
      sidebarCollapsed: false,
      currentTheme: 'light'
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
    // 获取当前主题
    this.currentTheme = this.$getTheme()
  },
  methods: {
    handleToggleTheme() {
      // 添加过渡类
      document.documentElement.classList.add('theme-transition')
      this.currentTheme = this.$toggleTheme()
      // 移除过渡类
      setTimeout(() => {
        document.documentElement.classList.remove('theme-transition')
      }, 300)
    },
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    getPageTitle() {
      const titles = {
        '/home/dashboard': '仪表盘',
        '/home/tutorials': '面试教程',
        '/home/knowledge': '知识库',
        '/home/practice': '题目练习',
        '/home/interview': 'AI模拟面试',
        '/home/wrong-questions': '错题本',
        '/home/favorites': '收藏夹',
        '/home/ability': '能力评估',
        '/home/study-report': '学习报告',
        '/home/records': '成绩记录',
        '/home/profile': '个人中心'
      }
      return titles[this.$route.path] || this.$route.name || '首页'
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout()
      } else if (command === 'profile') {
        this.$router.push('/home/profile')
      } else if (command === 'settings') {
        this.$message.info('设置功能开发中...')
      }
    },
    logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('user')
        this.$message.success('已退出登录')
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* 现代化布局 - 支持浅色/深色主题 */
.lc-layout {
  display: flex;
  min-height: 100vh;
  background: var(--lc-bg-primary);
  transition: background-color var(--lc-transition);
}

/* 侧边栏 */
.lc-sidebar {
  width: 260px;
  background: var(--lc-bg-sidebar);
  border-right: 1px solid var(--lc-border);
  display: flex;
  flex-direction: column;
  transition: all var(--lc-transition-slow);
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
  box-shadow: var(--lc-shadow-sm);
}

.lc-sidebar.collapsed {
  width: 72px;
}

.lc-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid var(--lc-border);
}

.logo-icon {
  width: 36px;
  height: 36px;
  color: var(--lc-primary);
  flex-shrink: 0;
  transition: transform var(--lc-transition);
}

.lc-logo:hover .logo-icon {
  transform: scale(1.1);
}

.logo-text {
  margin-left: 12px;
  font-size: 20px;
  font-weight: 700;
  color: var(--lc-text-primary);
  white-space: nowrap;
  background: var(--lc-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 导航 */
.lc-nav {
  flex: 1;
  overflow-y: auto;
  padding: 20px 12px;
}

.nav-section {
  margin-bottom: 28px;
}

.nav-section-title {
  font-size: 11px;
  font-weight: 700;
  color: var(--lc-text-muted);
  text-transform: uppercase;
  letter-spacing: 0.8px;
  padding: 0 14px;
  margin-bottom: 10px;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 14px;
  border-radius: var(--lc-radius-lg);
  color: var(--lc-text-secondary);
  text-decoration: none;
  transition: all var(--lc-transition);
  margin-bottom: 4px;
  position: relative;
  font-weight: 500;
}

.nav-item i {
  font-size: 20px;
  width: 24px;
  flex-shrink: 0;
  transition: transform var(--lc-transition);
}

.nav-item span {
  margin-left: 14px;
  font-size: 14px;
  white-space: nowrap;
}

.nav-item:hover {
  background: var(--lc-bg-hover);
  color: var(--lc-text-primary);
}

.nav-item:hover i {
  transform: scale(1.1);
}

.nav-item.active {
  background: var(--lc-primary-bg);
  color: var(--lc-primary);
  font-weight: 600;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: var(--lc-gradient-primary);
  border-radius: 0 4px 4px 0;
}

.nav-badge {
  margin-left: auto;
  padding: 3px 10px;
  background: var(--lc-gradient-primary);
  color: var(--lc-text-inverse);
  font-size: 10px;
  font-weight: 700;
  border-radius: var(--lc-radius-full);
  box-shadow: var(--lc-shadow-primary);
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid var(--lc-border);
}

.collapse-btn {
  width: 100%;
  padding: 10px;
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  border-radius: var(--lc-radius);
  color: var(--lc-text-secondary);
  cursor: pointer;
  transition: all var(--lc-transition);
  font-size: 16px;
}

.collapse-btn:hover {
  background: var(--lc-bg-hover);
  color: var(--lc-primary);
  border-color: var(--lc-primary);
}

/* 主内容区 */
.lc-main {
  flex: 1;
  margin-left: 260px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  transition: margin-left var(--lc-transition-slow);
}

.lc-sidebar.collapsed + .lc-main {
  margin-left: 72px;
}

/* 顶部导航 */
.lc-header {
  height: 64px;
  background: var(--lc-bg-header);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--lc-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--lc-text-primary);
  letter-spacing: -0.3px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-search {
  display: flex;
  align-items: center;
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  border-radius: var(--lc-radius-lg);
  padding: 10px 18px;
  width: 300px;
  transition: all var(--lc-transition);
}

.header-search:focus-within {
  border-color: var(--lc-primary);
  box-shadow: 0 0 0 3px var(--lc-primary-bg);
}

.header-search i {
  color: var(--lc-text-muted);
  margin-right: 10px;
  font-size: 16px;
}

.header-search input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: var(--lc-text-primary);
  font-size: 14px;
}

.header-search input::placeholder {
  color: var(--lc-text-placeholder);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 主题切换按钮 */
.theme-toggle-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--lc-radius);
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--lc-transition);
}

.theme-toggle-btn:hover {
  background: var(--lc-bg-hover);
  border-color: var(--lc-primary);
  color: var(--lc-primary);
}

.theme-toggle-btn i {
  font-size: 18px;
  transition: transform var(--lc-transition-bounce);
}

.theme-toggle-btn:hover i {
  transform: rotate(20deg) scale(1.1);
}

.action-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--lc-radius);
  background: var(--lc-bg-tertiary);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-secondary);
  cursor: pointer;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--lc-transition);
}

.action-btn:hover {
  background: var(--lc-bg-hover);
  color: var(--lc-text-primary);
  border-color: var(--lc-border-dark);
}

.action-btn i {
  font-size: 18px;
}

.action-btn .badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  background: var(--lc-danger);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  border-radius: var(--lc-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 2s infinite;
}

.user-avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 6px 14px;
  border-radius: var(--lc-radius-lg);
  transition: all var(--lc-transition);
  border: 1px solid transparent;
}

.user-avatar:hover {
  background: var(--lc-bg-hover);
  border-color: var(--lc-border);
}

.avatar-circle {
  width: 34px;
  height: 34px;
  border-radius: var(--lc-radius-full);
  background: var(--lc-gradient-primary);
  color: var(--lc-text-inverse);
  font-weight: 700;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--lc-shadow-primary);
}

.user-name {
  margin-left: 10px;
  font-size: 14px;
  color: var(--lc-text-primary);
  font-weight: 600;
}

.user-avatar i {
  margin-left: 6px;
  color: var(--lc-text-muted);
  font-size: 12px;
  transition: transform var(--lc-transition);
}

.user-avatar:hover i {
  transform: rotate(180deg);
}

/* 内容区 */
.lc-content {
  flex: 1;
  padding: 28px;
  background: var(--lc-bg-primary);
  overflow-y: auto;
}

/* 页面过渡动画 */
.fade-enter-active {
  animation: fadeInUp 0.3s ease;
}

.fade-leave-active {
  animation: fadeIn 0.15s ease reverse;
}

/* 响应式 */
@media (max-width: 1024px) {
  .header-search {
    width: 200px;
  }
  
  .user-name {
    display: none;
  }
}

@media (max-width: 768px) {
  .lc-sidebar {
    transform: translateX(-100%);
  }
  
  .lc-sidebar.collapsed {
    transform: translateX(0);
    width: 72px;
  }
  
  .lc-main {
    margin-left: 0;
  }
  
  .header-search {
    display: none;
  }
}

/* 下拉菜单样式 */
.lc-dropdown {
  background: #282828 !important;
  border: 1px solid #3c3c3c !important;
}
</style>
