<template>
  <el-container class="home-container">
    <el-aside width="220px" class="app-aside">
      <div class="logo-container">
        <i class="el-icon-s-platform logo-icon"></i>
        <span class="logo-text">面试辅助系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        class="el-menu-vertical">
        <el-menu-item index="/home/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/home/tutorials">
          <i class="el-icon-reading"></i>
          <span slot="title">面试教程</span>
        </el-menu-item>
        <el-menu-item index="/home/knowledge">
          <i class="el-icon-notebook-2"></i>
          <span slot="title">知识点学习</span>
        </el-menu-item>
        <el-menu-item index="/home/practice">
          <i class="el-icon-edit"></i>
          <span slot="title">题目练习</span>
        </el-menu-item>
        <el-menu-item index="/home/favorites">
          <i class="el-icon-star-on"></i>
          <span slot="title">我的收藏</span>
        </el-menu-item>
        <el-menu-item index="/home/interview">
          <i class="el-icon-microphone"></i>
          <span slot="title">AI模拟面试</span>
        </el-menu-item>
        <el-menu-item index="/home/wrong-questions">
          <i class="el-icon-warning-outline"></i>
          <span slot="title">我的错题本</span>
        </el-menu-item>
        <el-menu-item index="/home/ability">
          <i class="el-icon-data-analysis"></i>
          <span slot="title">能力评估</span>
        </el-menu-item>
        <el-menu-item index="/home/study-report">
          <i class="el-icon-s-marketing"></i>
          <span slot="title">学习报告</span>
        </el-menu-item>
        <el-menu-item index="/home/records">
          <i class="el-icon-document"></i>
          <span slot="title">成绩记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <i class="el-icon-s-fold toggle-icon"></i>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/home/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.name }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar size="small" icon="el-icon-user-solid" style="background-color: #409EFF"></el-avatar>
              <span class="username">{{ user.username }}</span>
              <i class="el-icon-caret-bottom"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <transition name="fade-transform" mode="out-in">
          <router-view/>
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('user') || '{}')
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.logout()
      } else if (command === 'profile') {
        this.$router.push('/home/profile')
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
.home-container {
  height: 100vh;
}

/* Sidebar Styles */
.app-aside {
  background-color: #304156;
  color: #fff;
  transition: width 0.3s;
  overflow-x: hidden;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  z-index: 10;
}

.logo-container {
  height: 60px;
  line-height: 60px;
  text-align: center;
  background: #2b2f3a;
  overflow: hidden;
}

.logo-icon {
  color: #409EFF;
  font-size: 24px;
  vertical-align: middle;
  margin-right: 10px;
}

.logo-text {
  color: #fff;
  font-weight: 600;
  font-size: 18px;
  vertical-align: middle;
  font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
}

.el-menu-vertical {
  border-right: none;
}

/* Header Styles */
.app-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  color: #606266;
}

.breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 60px;
  margin-left: 8px;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.username {
  margin: 0 8px;
  font-size: 14px;
}

/* Main Content Styles */
.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-x: hidden;
}

/* Transitions */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all .5s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
