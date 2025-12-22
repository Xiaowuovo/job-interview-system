import Vue from 'vue'

// 事件总线，用于跨组件通信
const EventBus = new Vue()

// 事件类型常量
export const EVENTS = {
  // 测试相关
  TEST_COMPLETED: 'test:completed',
  
  // 面试相关
  INTERVIEW_COMPLETED: 'interview:completed',
  
  // 学习相关
  KNOWLEDGE_STUDIED: 'knowledge:studied',
  
  // 收藏相关
  FAVORITE_CHANGED: 'favorite:changed',
  
  // 错题相关
  WRONG_QUESTION_CHANGED: 'wrong-question:changed',
  
  // 用户数据刷新
  USER_DATA_REFRESH: 'user:data-refresh',
  
  // 积分变化
  POINTS_CHANGED: 'points:changed'
}

export default EventBus
