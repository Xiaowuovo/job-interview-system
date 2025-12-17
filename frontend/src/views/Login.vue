<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- Left Side: Branding/Image -->
      <div class="login-left">
        <div class="brand-content">
          <div class="brand-title">
            <i class="el-icon-s-platform"></i>
            <span>面试辅助系统</span>
          </div>
          <div class="brand-desc">
            <h2>助你轻松拿下 Offer</h2>
            <p>智能模拟面试，海量题库练习，全方位提升你的面试竞争力。</p>
          </div>
          <div class="brand-footer">
            <span>&copy; 2025 Interview Assistant</span>
          </div>
        </div>
        <div class="brand-bg-circle"></div>
        <div class="brand-bg-circle-2"></div>
      </div>

      <!-- Right Side: Form -->
      <div class="login-right">
        <div class="form-container">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">请登录您的账号以继续</p>

          <el-form :model="form" :rules="rules" ref="loginForm" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                prefix-icon="el-icon-user">
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                prefix-icon="el-icon-lock"
                show-password
                @keyup.enter.native="handleLogin">
              </el-input>
            </el-form-item>

            <div class="form-actions">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <el-button type="text" class="forgot-btn">忘记密码?</el-button>
            </div>

            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%" class="submit-btn">
                登录
              </el-button>
            </el-form-item>

            <el-form-item>
              <el-button plain @click="handleRegister" style="width: 100%">注册新账号</el-button>
            </el-form-item>
          </el-form>

          <div class="demo-info">
            <el-alert
              title="演示账号"
              type="info"
              :closable="false"
              show-icon>
              <div class="demo-accounts">
                <span>学生: student / 123456</span>
                <span>教师: teacher / 123456</span>
              </div>
            </el-alert>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      rememberMe: false,
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$http.post('/users/login', this.form)
            .then(res => {
              if (res.data.code === 200) {
                localStorage.setItem('user', JSON.stringify(res.data.data))
                this.$message.success('登录成功')
                this.$router.push('/home/dashboard')
              } else {
                this.$message.error(res.data.message)
              }
            })
            .catch(() => {
              this.$message.error('登录失败，请检查网络或服务器')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    handleRegister() {
      this.$prompt('请输入邮箱', '注册', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
        inputErrorMessage: '邮箱格式不正确'
      }).then(({ value }) => {
        const user = {
          username: this.form.username,
          password: this.form.password,
          email: value
        }
        this.$http.post('/users/register', user)
          .then(res => {
            if (res.data.code === 200) {
              this.$message.success('注册成功，请登录')
            } else {
              this.$message.error(res.data.message)
            }
          })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
  padding: 20px;
}

.login-wrapper {
  width: 1000px;
  height: 600px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.1);
  display: flex;
  overflow: hidden;
}

/* Left Side */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #3a7bd5 0%, #3a6073 100%);
  position: relative;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #fff;
  overflow: hidden;
}

.brand-title {
  font-size: 24px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-desc h2 {
  font-size: 36px;
  margin-bottom: 20px;
  line-height: 1.2;
}

.brand-desc p {
  font-size: 16px;
  opacity: 0.8;
  line-height: 1.6;
}

.brand-footer {
  font-size: 12px;
  opacity: 0.6;
}

/* Decorative Circles */
.brand-bg-circle {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255,255,255,0.1);
  top: -50px;
  right: -50px;
}

.brand-bg-circle-2 {
  position: absolute;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: rgba(255,255,255,0.05);
  bottom: -20px;
  left: -50px;
}

/* Right Side */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-container {
  width: 100%;
  max-width: 360px;
}

.form-title {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.form-subtitle {
  color: #909399;
  margin-bottom: 30px;
}

.login-form .el-input__inner {
  height: 45px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forgot-btn {
  color: #909399;
}

.submit-btn {
  height: 45px;
  font-size: 16px;
  letter-spacing: 2px;
}

.demo-info {
  margin-top: 30px;
}

.demo-accounts {
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 12px;
}
</style>
