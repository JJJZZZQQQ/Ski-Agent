import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    { path: '/', redirect: '/chat' },
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    { path: '/chat', name: 'Chat', component: () => import('@/views/Chat.vue'), meta: { requiresAuth: true } },
    { path: '/chat/:threadId', name: 'ChatThread', component: () => import('@/views/Chat.vue'), meta: { requiresAuth: true } },
    { path: '/profile', name: 'Profile', component: () => import('@/views/Profile.vue'), meta: { requiresAuth: true } },
    { path: '/knowledge', name: 'Knowledge', component: () => import('@/views/Knowledge.vue'), meta: { requiresAuth: true } }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('ski_token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.name === 'Login' && token) {
    next({ path: '/chat' })
  } else {
    next()
  }
})

export default router
