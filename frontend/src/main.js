import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'

Vue.use(ElementUI)
Vue.config.productionTip = false

// 配置axios
axios.defaults.baseURL = '/api'
Vue.prototype.$http = axios

// 简单的用户信息存储
Vue.prototype.$user = JSON.parse(localStorage.getItem('user') || 'null')

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
