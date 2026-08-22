<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useChatStore } from '@/stores/chat'
import { ElMessage } from 'element-plus'
import MessageList from '@/components/chat/MessageList.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const inputText = ref('')
const msgListRef = ref<InstanceType<typeof MessageList>>()
const showSidebar = ref(true)

onMounted(async () => {
  await userStore.fetchMe()
  await chatStore.loadThreads()
  const tid = route.params.threadId as string
  if (tid) { chatStore.currentThreadId = tid; await chatStore.loadMessages(tid) }
})

/** 发送消息（SSE 流式） */
async function send() {
  if (!inputText.value.trim() || chatStore.isStreaming) return
  const content = inputText.value.trim()
  inputText.value = ''

  // 确保有 thread
  if (!chatStore.currentThreadId) {
    chatStore.currentThreadId = await chatStore.createThread()
    router.replace(`/chat/${chatStore.currentThreadId}`)
  }

  // 添加 user 消息到界面
  chatStore.addMessage({ role: 'user', content })
  scrollToBottom()

  try {
    const response = await chatStore.startStream(chatStore.currentThreadId, content)
    const reader = response.body?.getReader()
    if (!reader) { ElMessage.error('SSE 连接失败'); return }

    // 添加 assistant 占位消息
    chatStore.addMessage({ role: 'assistant', content: '' })
    const decoder = new TextDecoder()
    let buffer = ''

    // 读取 SSE 流
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = JSON.parse(line.substring(5).trim())
          if (data.delta) chatStore.updateLastMessage((chatStore.messages[chatStore.messages.length - 1].content || '') + data.delta)
          scrollToBottom()
        }
      }
    }
  } catch (err) {
    ElMessage.error('消息发送失败')
  } finally {
    chatStore.stopStream()
    // 刷新会话列表
    await chatStore.loadThreads()
  }
}

/** 切换会话 */
async function selectThread(threadId: string) {
  chatStore.currentThreadId = threadId
  router.push(`/chat/${threadId}`)
  await chatStore.loadMessages(threadId)
}

/** 创建新会话 */
async function newThread() {
  chatStore.currentThreadId = null
  chatStore.messages = []
  router.push('/chat')
}

async function scrollToBottom() { await nextTick(); msgListRef.value?.scrollToBottom() }
</script>

<template>
  <div class="chat-page">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: !showSidebar }">
      <div class="sidebar-header">
        <el-button type="primary" @click="newThread" size="small">+ 新对话</el-button>
      </div>
      <div class="thread-list">
        <div v-for="t in chatStore.threads" :key="t.threadId"
          :class="['thread-item', { active: chatStore.currentThreadId === t.threadId }]"
          @click="selectThread(t.threadId)">
          {{ t.title }}
        </div>
      </div>
      <div class="sidebar-footer">
        <span>{{ userStore.userInfo?.nickname || '未登录' }}</span>
        <el-button text size="small" @click="userStore.logout();router.push('/login')">退出</el-button>
      </div>
    </aside>

    <!-- 主聊天区 -->
    <main class="chat-main">
      <header class="chat-header">
        <el-button text @click="showSidebar = !showSidebar">☰</el-button>
        <span>{{ chatStore.currentThreadId ? (chatStore.threads.find(t=>t.threadId===chatStore.currentThreadId)?.title || '对话') : '新对话' }}</span>
      </header>
      <MessageList ref="msgListRef" :messages="chatStore.messages" :streaming="chatStore.isStreaming" />
      <footer class="chat-footer">
        <el-input v-model="inputText" placeholder="输入消息，Enter 发送..." size="large" @keyup.enter="send" :disabled="chatStore.isStreaming">
          <template #append>
            <el-button :icon="'Promotion'" @click="send" :loading="chatStore.isStreaming" />
          </template>
        </el-input>
      </footer>
    </main>
  </div>
</template>

<style scoped>
.chat-page { display:flex; height:100%; }
.sidebar { width:260px; background:#f5f7fa; display:flex; flex-direction:column; border-right:1px solid #e4e7ed; }
.sidebar.collapsed { display:none; }
.sidebar-header { padding:12px; border-bottom:1px solid #e4e7ed; }
.thread-list { flex:1; overflow-y:auto; padding:8px 0; }
.thread-item { padding:10px 16px; cursor:pointer; font-size:14px; color:#333; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.thread-item:hover { background:#e8eaed; }
.thread-item.active { background:#d9ecff; color:#409eff; }
.sidebar-footer { padding:12px; border-top:1px solid #e4e7ed; display:flex; justify-content:space-between; align-items:center; font-size:13px; }
.chat-main { flex:1; display:flex; flex-direction:column; }
.chat-header { padding:12px 16px; border-bottom:1px solid #e4e7ed; display:flex; align-items:center; gap:8px; background:#fff; }
.chat-footer { padding:12px 16px; border-top:1px solid #e4e7ed; background:#fff; }
</style>