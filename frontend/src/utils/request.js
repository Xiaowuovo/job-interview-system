import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取用户信息
    const userStr = localStorage.getItem('user')
    if (userStr) {
      try {
        const user = JSON.parse(userStr)
        // 添加用户ID到请求头
        if (user && user.id) {
          config.headers['userId'] = user.id
        }
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码不是200，则认为是错误
    if (res.code !== 200) {
      Message({
        message: res.message || '请求失败',
        type: 'error',
        duration: 3000
      })

      // 401: 未登录或登录过期
      if (res.code === 401) {
        Message({
          message: '登录已过期，请重新登录',
          type: 'warning',
          duration: 2000
        })
        localStorage.removeItem('user')
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    let message = '网络错误，请稍后重试'
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = error.response.data.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          localStorage.removeItem('user')
          router.push('/login')
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = error.response.data.message || '服务器错误'
          break
        default:
          message = error.response.data.message || '请求失败'
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    }

    Message({
      message: message,
      type: 'error',
      duration: 3000
    })

    return Promise.reject(error)
  }
)

export default service
