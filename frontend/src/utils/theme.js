/**
 * 主题管理工具
 * 支持浅色/深色模式切换，默认浅色模式
 */

const THEME_KEY = 'interview-pro-theme'

// 主题类型
export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark'
}

// 获取系统偏好主题
function getSystemTheme() {
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return THEMES.DARK
  }
  return THEMES.LIGHT
}

// 获取当前主题
export function getTheme() {
  const savedTheme = localStorage.getItem(THEME_KEY)
  if (savedTheme && Object.values(THEMES).includes(savedTheme)) {
    return savedTheme
  }
  // 默认使用浅色模式
  return THEMES.LIGHT
}

// 设置主题
export function setTheme(theme) {
  if (!Object.values(THEMES).includes(theme)) {
    console.warn('Invalid theme:', theme)
    return
  }
  
  localStorage.setItem(THEME_KEY, theme)
  applyTheme(theme)
}

// 应用主题到DOM
export function applyTheme(theme) {
  const root = document.documentElement
  
  // 移除所有主题类
  root.classList.remove('theme-light', 'theme-dark')
  
  // 添加当前主题类
  root.classList.add(`theme-${theme}`)
  
  // 设置data属性用于CSS变量
  root.setAttribute('data-theme', theme)
  
  // 更新meta标签颜色
  const metaThemeColor = document.querySelector('meta[name="theme-color"]')
  if (metaThemeColor) {
    metaThemeColor.setAttribute('content', theme === THEMES.DARK ? '#1a1a1a' : '#ffffff')
  }
}

// 切换主题
export function toggleTheme() {
  const currentTheme = getTheme()
  const newTheme = currentTheme === THEMES.LIGHT ? THEMES.DARK : THEMES.LIGHT
  setTheme(newTheme)
  return newTheme
}

// 初始化主题
export function initTheme() {
  const theme = getTheme()
  applyTheme(theme)
  
  // 监听系统主题变化
  if (window.matchMedia) {
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      // 只有在用户没有手动设置主题时才跟随系统
      if (!localStorage.getItem(THEME_KEY)) {
        applyTheme(e.matches ? THEMES.DARK : THEMES.LIGHT)
      }
    })
  }
  
  return theme
}

export default {
  THEMES,
  getTheme,
  setTheme,
  applyTheme,
  toggleTheme,
  initTheme
}
