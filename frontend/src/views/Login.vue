<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isRegister = ref(false)
const form = reactive({ username: '', password: '', nickname: '' })
const loading = ref(false)

async function submit() {
  if (!form.username || !form.password) { ElMessage.warning('请填写用户名和密码'); return }
  loading.value = true
  try {
    if (isRegister.value) {
      await userStore.register(form.username, form.password, form.nickname || undefined)
    } else {
      await userStore.login(form.username, form.password)
    }
    const redirect = (route.query.redirect as string) || '/chat'
    router.push(redirect)
  } catch { /* axios interceptor already handles error */ }
  finally { loading.value = false }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h1>🎿 Ski Agent</h1>
      <p class="subtitle">你的滑雪专属助手</p>
      <el-form @submit.prevent="submit" label-position="top">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password />
        </el-form-item>
        <el-form-item v-if="isRegister" label="昵称（可选）">
          <el-input v-model="form.nickname" placeholder="给自己起个名字吧" size="large" />
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" native-type="submit" style="width:100%">
          {{ isRegister ? '注册' : '登录' }}
        </el-button>
      </el-form>
      <p class="toggle">{{ isRegister ? '已有账号？' : '没有账号？' }}<a href="javascript:void(0)" @click="isRegister=!isRegister">{{ isRegister ? '去登录' : '去注册' }}</a></p>
    </div>
  </div>
</template>

<style scoped>
.login-page { height:100%; display:flex; align-items:center; justify-content:center; background:linear-gradient(135deg,#667eea 0%,#764ba2 100%); }
.login-card { background:#fff; padding:40px; border-radius:12px; width:400px; box-shadow:0 8px 32px rgba(0,0,0,.15); }
.login-card h1 { text-align:center; font-size:28px; margin-bottom:4px; }
.subtitle { text-align:center; color:#999; margin-bottom:24px; }
.toggle { text-align:center; margin-top:16px; color:#999; }
.toggle a { color:#667eea; cursor:pointer; }
</style>