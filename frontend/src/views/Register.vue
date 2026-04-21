<template>
  <div class="register-container">
    <div class="register-wrapper">
      <!-- Left Side: Branding/Image -->
      <div class="register-left">
        <div class="brand-content">
          <div class="brand-title">
            <i class="el-icon-s-platform"></i>
            <span>面试辅助系统</span>
          </div>
          <div class="brand-desc">
            <h2>开启学习之旅</h2>
            <p>注册账号，解锁智能模拟面试、海量题库、知识库等全部功能。</p>
          </div>
          <div class="brand-footer">
            <span>&copy; 2025 Interview Assistant</span>
          </div>
        </div>
        <div class="brand-bg-circle"></div>
        <div class="brand-bg-circle-2"></div>
      </div>

      <!-- Right Side: Form -->
      <div class="register-right">
        <div class="form-container">
          <h2 class="form-title">创建新账号</h2>
          <p class="form-subtitle">填写以下信息完成注册</p>

          <el-form :model="form" :rules="rules" ref="registerForm" class="register-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名 (字母、数字、下划线，3-20位)"
                prefix-icon="el-icon-user"
                maxlength="20">
              </el-input>
            </el-form-item>

            <el-form-item prop="email">
              <el-input
                v-model="form.email"
                placeholder="邮箱"
                prefix-icon="el-icon-message"
                type="email">
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码 (至少6位)"
                prefix-icon="el-icon-lock"
                show-password>
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="确认密码"
                prefix-icon="el-icon-lock"
                show-password
                @keyup.enter.native="handleRegister">
              </el-input>
            </el-form-item>

            <el-form-item prop="agree">
              <el-checkbox v-model="form.agree">
                我已阅读并同意 <el-button type="text" class="link-btn">《用户协议》</el-button> 和 <el-button type="text" class="link-btn">《隐私政策》</el-button>
              </el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" :loading="loading" @click="handleRegister" style="width: 100%" class="submit-btn">
                注册
              </el-button>
            </el-form-item>

            <div class="login-link">
              已有账号？<el-button type="text" class="link-btn" @click="goToLogin">立即登录</el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    const validateUsername = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入用户名'))
      } else if (value.length < 3 || value.length > 20) {
        callback(new Error('用户名长度为3-20位'))
      } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
        callback(new Error('用户名只能包含字母、数字和下划线'))
      } else {
        callback()
      }
    }

    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能少于6位'))
      } else {
        if (this.form.confirmPassword) {
          this.$refs.registerForm.validateField('confirmPassword')
        }
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.form.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    const validateAgree = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请阅读并同意用户协议和隐私政策'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      form: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        agree: false
      },
      rules: {
        username: [{ validator: validateUsername, trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
        ],
        password: [{ validator: validatePassword, trigger: 'blur' }],
        confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
        agree: [{ validator: validateAgree, trigger: 'change' }]
      }
    }
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          const user = {
            username: this.form.username,
            password: this.form.password,
            email: this.form.email
          }
          this.$http.post('/users/register', user)
            .then(res => {
              if (res.code === 200) {
                this.$message.success('注册成功，即将跳转到登录页...')
                setTimeout(() => {
                  this.$router.push('/login')
                }, 1500)
              }
            })
            .catch(err => {
              console.error('注册失败:', err)
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
/* 现代化注册页 - 支持浅色/深色主题 */
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--lc-bg-primary);
  padding: 20px;
  transition: background-color var(--lc-transition);
}

.register-wrapper {
  width: 1000px;
  height: 680px;
  background: var(--lc-bg-card);
  border-radius: var(--lc-radius-2xl);
  box-shadow: var(--lc-shadow-xl);
  display: flex;
  overflow: hidden;
  border: 1px solid var(--lc-border);
  animation: scaleIn 0.4s ease;
}

/* Left Side - 品牌展示区 */
.register-left {
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
.register-right {
  flex: 0.9;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px;
  background: var(--lc-bg-card);
}

.form-container {
  width: 100%;
  max-width: 360px;
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
  margin-bottom: 28px;
  font-size: 14px;
}

/* Form Inputs */
.register-form /deep/ .el-input__inner {
  height: 48px;
  background: var(--lc-bg-input);
  border: 1px solid var(--lc-border);
  color: var(--lc-text-primary);
  border-radius: var(--lc-radius-lg);
  font-size: 15px;
  transition: all var(--lc-transition);
}

.register-form /deep/ .el-input__inner:hover {
  border-color: var(--lc-border-dark);
}

.register-form /deep/ .el-input__inner:focus {
  border-color: var(--lc-primary);
  box-shadow: 0 0 0 3px var(--lc-primary-bg);
}

.register-form /deep/ .el-input__inner::placeholder {
  color: var(--lc-text-placeholder);
}

.register-form /deep/ .el-input__prefix {
  color: var(--lc-text-muted);
}

.register-form /deep/ .el-form-item {
  margin-bottom: 18px;
}

.register-form /deep/ .el-checkbox__label {
  color: var(--lc-text-secondary);
  font-size: 13px;
  line-height: 1.6;
}

.register-form /deep/ .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: var(--lc-primary);
  border-color: var(--lc-primary);
}

.link-btn {
  color: var(--lc-primary) !important;
  font-size: 13px;
  padding: 0 !important;
}

.link-btn:hover {
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

.login-link {
  text-align: center;
  margin-top: 20px;
  color: var(--lc-text-secondary);
  font-size: 14px;
}

/* 响应式 */
@media (max-width: 900px) {
  .register-wrapper {
    flex-direction: column;
    height: auto;
    width: 100%;
    max-width: 440px;
  }
  
  .register-left {
    padding: 40px;
    min-height: 220px;
  }
  
  .register-right {
    padding: 40px;
  }
  
  .brand-desc h2 {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: 16px;
  }
  
  .register-left {
    padding: 30px;
  }
  
  .register-right {
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
