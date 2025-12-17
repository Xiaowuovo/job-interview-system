import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'tutorials',
        name: 'Tutorials',
        component: () => import('@/views/Tutorials.vue')
      },
      {
        path: 'tutorial/:id',
        name: 'TutorialDetail',
        component: () => import('@/views/TutorialDetail.vue')
      },
      {
        path: 'practice',
        name: 'Practice',
        component: () => import('@/views/Practice.vue')
      },
      {
        path: 'test/:category',
        name: 'Test',
        component: () => import('@/views/Test.vue')
      },
      {
        path: 'interview',
        name: 'Interview',
        component: () => import('@/views/Interview.vue')
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/Records.vue')
      },
      {
        path: 'ability',
        name: 'Ability',
        component: () => import('@/views/Ability.vue')
      },
      {
        path: 'knowledge',
        name: 'Knowledge',
        component: () => import('@/views/Knowledge.vue')
      },
      {
        path: 'wrong-questions',
        name: 'WrongQuestions',
        component: () => import('@/views/WrongQuestions.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue')
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/Favorites.vue')
      },
      {
        path: 'interview-report/:id',
        name: 'InterviewReport',
        component: () => import('@/views/InterviewReport.vue')
      },
      {
        path: 'study-report',
        name: 'StudyReport',
        component: () => import('@/views/StudyReport.vue')
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

export default router
