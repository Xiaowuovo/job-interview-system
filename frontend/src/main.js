import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/styles/global-theme.css'
import request from './utils/request'
import EventBus, { EVENTS } from './utils/eventBus'
import ThemeManager, { initTheme, getTheme, setTheme, toggleTheme, THEMES } from './utils/theme'

Vue.use(ElementUI)

// 初始化主题（默认浅色）
initTheme()

// 全局主题管理
Vue.prototype.$theme = ThemeManager
Vue.prototype.$getTheme = getTheme
Vue.prototype.$setTheme = setTheme
Vue.prototype.$toggleTheme = toggleTheme
Vue.prototype.$THEMES = THEMES

// 全局事件总线
Vue.prototype.$bus = EventBus
Vue.prototype.$events = EVENTS
Vue.config.productionTip = false

// 使用封装的request实例
Vue.prototype.$http = request

// 全局获取当前用户方法
Vue.prototype.$getCurrentUser = function() {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      return JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
      localStorage.removeItem('user')
      return null
    }
  }
  return null
}

// 全局保存用户方法
Vue.prototype.$setCurrentUser = function(user) {
  if (user) {
    localStorage.setItem('user', JSON.stringify(user))
  } else {
    localStorage.removeItem('user')
  }
}

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
