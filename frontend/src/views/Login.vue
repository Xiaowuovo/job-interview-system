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
              if (res.code === 200) {
                this.$setCurrentUser(res.data)
                this.$message.success('登录成功')
                this.$router.push('/home/dashboard')
              }
            })
            .catch(err => {
              console.error('登录失败:', err)
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    handleRegister() {
      if (!this.form.username || !this.form.password) {
        this.$message.warning('请先填写用户名和密码')
        return
      }

      if (this.form.password.length < 6) {
        this.$message.warning('密码长度不能少于6位')
        return
      }

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
            if (res.code === 200) {
              this.$message.success('注册成功，请登录')
              this.form.password = ''
            }
          })
          .catch(err => {
            console.error('注册失败:', err)
          })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
/* 现代化登录页 - 支持浅色/深色主题 */
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--lc-bg-primary);
  padding: 20px;
  transition: background-color var(--lc-transition);
}

.login-wrapper {
  width: 1000px;
  height: 620px;
  background: var(--lc-bg-card);
  border-radius: var(--lc-radius-2xl);
  box-shadow: var(--lc-shadow-xl);
  display: flex;
  overflow: hidden;
  border: 1px solid var(--lc-border);
  animation: scaleIn 0.4s ease;
}

/* Left Side - 品牌展示区 */
.login-left {
  flex: 1.1;
  background: var(--lc-gradient-primary);
  position: relative;
  padding: 50px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #fff;
  overflow: hidden;
}

.brand-title {
  font-size: 22px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #fff;
}

.brand-title i {
  font-size: 28px;
}

.brand-desc h2 {
  font-size: 36px;
  margin-bottom: 20px;
  line-height: 1.2;
  color: #fff;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.brand-desc p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.8;
  max-width: 360px;
}

.brand-footer {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

/* Decorative Elements */
.brand-bg-circle {
  position: absolute;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 70%);
  top: -150px;
  right: -150px;
  animation: pulse 4s ease-in-out infinite;
}

.brand-bg-circle-2 {
  position: absolute;
  width: 350px;
  height: 350px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  bottom: -100px;
  left: -100px;
  animation: pulse 4s ease-in-out infinite 1s;
}

/* Right Side - 表单区 */
.login-right {
  flex: 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px;
  background: var(--lc-bg-card);
}

.form-container {
  width: 100%;
  max-width: 340px;
}

.form-title {
  font-size: 28px;
  color: var(--lc-text-primary);
  margin-bottom: 8px;
  font-weight: 700;
  letter-spacing: -0.3px;
}

.form-subtitle {
  color: var(--lc-text-muted);
  margin-bottom: 32px;
  font-size: 14px;
}

/* Form Inputs */
.login-form /deep/ .el-input__inner {
  height: 50px;
  background: var(--lc-bg-input);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
  border-radius: var(--lc-radius-lg);
  font-size: 15px;
  transition: all var(--lc-transition);
}

.login-form /deep/ .el-input__inner:hover {
  border-color: var(--lc-border-dark);
}

.login-form /deep/ .el-input__inner:focus {
  border-color: var(--lc-primary);
  box-shadow: 0 0 0 3px var(--lc-primary-bg);
}

.login-form /deep/ .el-input__inner::placeholder {
  color: var(--lc-text-placeholder);
}

.login-form /deep/ .el-input__prefix {
  color: var(--lc-text-muted);
}

.login-form /deep/ .el-form-item {
  margin-bottom: 20px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.form-actions /deep/ .el-checkbox__label {
  color: var(--lc-text-secondary);
  font-size: 14px;
}

.form-actions /deep/ .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: var(--lc-primary);
  border-color: var(--lc-primary);
}

.forgot-btn {
  color: var(--lc-primary) !important;
  font-size: 14px;
}

.forgot-btn:hover {
  color: var(--lc-primary-light) !important;
}

.submit-btn {
  height: 50px;
  font-size: 16px;
  letter-spacing: 1px;
  background: var(--lc-gradient-primary) !important;
  border: none !important;
  color: var(--lc-text-inverse) !important;
  font-weight: 600;
  border-radius: var(--lc-radius-lg);
  transition: all var(--lc-transition);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--lc-shadow-primary);
}

.submit-btn:active {
  transform: translateY(0);
}

/* Register Button */
.login-form /deep/ .el-button--default {
  height: 50px;
  background: transparent;
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
  border-radius: var(--lc-radius-lg);
  font-size: 15px;
  transition: all var(--lc-transition);
}

.login-form /deep/ .el-button--default:hover {
  border-color: var(--lc-primary);
  color: var(--lc-primary);
  background: var(--lc-primary-bg);
}

.demo-info {
  margin-top: 28px;
}

.demo-info /deep/ .el-alert {
  background: var(--lc-primary-bg);
  border: 1px solid rgba(255, 107, 0, 0.2);
  border-radius: var(--lc-radius-lg);
  padding: 14px 16px;
}

.demo-info /deep/ .el-alert__title {
  color: var(--lc-primary);
  font-weight: 600;
}

.demo-info /deep/ .el-alert__icon {
  color: var(--lc-primary);
}

.demo-accounts {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 13px;
  color: var(--lc-text-secondary);
  margin-top: 8px;
}

.demo-accounts span {
  padding: 4px 0;
}

/* 响应式 */
@media (max-width: 900px) {
  .login-wrapper {
    flex-direction: column;
    height: auto;
    width: 100%;
    max-width: 440px;
  }
  
  .login-left {
    padding: 40px;
    min-height: 220px;
  }
  
  .login-right {
    padding: 40px;
  }
  
  .brand-desc h2 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }
  
  .login-left {
    padding: 30px;
  }
  
  .login-right {
    padding: 30px;
  }
  
  .form-title {
    font-size: 24px;
  }
}

/* 动画 */
@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.05); }
}
</style>
