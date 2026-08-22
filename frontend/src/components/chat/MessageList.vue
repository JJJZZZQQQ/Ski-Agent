<script setup lang="ts">
import { ref, watch, nextTick } from 'vue'
import { marked } from 'marked'

/** 配置 Markdown 渲染选项 */
marked.setOptions({ breaks: true, gfm: true })

interface Message { role: string; content: string }
const props = defineProps<{ messages: Message[]; streaming: boolean }>()
const listRef = ref<HTMLDivElement>()
const isAutoScroll = ref(true)

watch(() => props.messages.length, () => { if (isAutoScroll.value) scrollNow() }, { flush: 'post' })
watch(() => props.streaming, (val) => { if (val) scrollNow() })

function scrollNow() { nextTick(() => { if (listRef.value) listRef.value.scrollTop = listRef.value.scrollHeight }) }
function onScroll() {
  if (!listRef.value) return
  const { scrollTop, scrollHeight, clientHeight } = listRef.value
  isAutoScroll.value = (scrollHeight - scrollTop - clientHeight) < 50
}

/**
 * 渲染消息内容：流式中用纯文本 + 光标，完成后用 Markdown 渲染为 HTML
 */
function renderContent(msg: Message, idx: number): string {
  const isLastAssistant = idx === props.messages.length - 1 && msg.role === 'assistant'
  const cursor = (isLastAssistant && props.streaming) ? '▊' : ''
  const text = msg.content + cursor

  // 流式进行中：纯文本（保留换行），不做 Markdown 渲染避免闪烁
  if (isLastAssistant && props.streaming) {
    return escapeHtml(text).replace(/\n/g, '<br>')
  }
  // 已完成：Markdown 渲染
  return marked.parse(text) as string
}

function escapeHtml(str: string): string {
  return str.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;')
}

defineExpose({ scrollToBottom: scrollNow })
</script>

<template>
  <div class="message-list" ref="listRef" @scroll="onScroll">
    <div v-if="messages.length===0" class="empty">👋 开始对话吧！我是你的滑雪专属助手~</div>
    <div v-for="(msg, i) in messages" :key="i" :class="['message', msg.role]">
      <div class="msg-avatar">{{ msg.role==='user' ? '🧑' : '🎿' }}</div>
      <div class="msg-bubble" :class="msg.role" v-html="renderContent(msg, i)" />
    </div>
  </div>
</template>

<style scoped>
.message-list { flex:1; overflow-y:auto; padding:16px; background:#fafafa; }
.empty { text-align:center; padding:80px 0; color:#999; font-size:16px; }
.message { display:flex; gap:12px; margin-bottom:20px; }
.message.user { flex-direction:row-reverse; }
.msg-avatar { width:36px; height:36px; border-radius:50%; background:#e4e7ed; display:flex; align-items:center; justify-content:center; font-size:18px; flex-shrink:0; }

/* 气泡基础样式 */
.msg-bubble { max-width:80%; padding:12px 18px; border-radius:12px; line-height:1.8; word-break:break-word; }
.msg-bubble.user { background:#409eff; color:#fff; border-bottom-right-radius:4px; }
.msg-bubble.assistant { background:#fff; color:#333; border-bottom-left-radius:4px; box-shadow:0 1px 3px rgba(0,0,0,.08); }

/* ── Markdown 渲染样式（仅 assistant 消息生效） ── */
.msg-bubble.assistant :deep(h1), .msg-bubble.assistant :deep(h2), .msg-bubble.assistant :deep(h3) { margin:12px 0 8px; }
.msg-bubble.assistant :deep(h2) { font-size:18px; border-bottom:1px solid #e4e7ed; padding-bottom:4px; }
.msg-bubble.assistant :deep(h3) { font-size:16px; }
.msg-bubble.assistant :deep(ul), .msg-bubble.assistant :deep(ol) { padding-left:20px; margin:6px 0; }
.msg-bubble.assistant :deep(li) { margin:4px 0; }
.msg-bubble.assistant :deep(strong) { font-weight:600; color:#303133; }
.msg-bubble.assistant :deep(p) { margin:6px 0; }
.msg-bubble.assistant :deep(code) { background:#f0f2f5; padding:2px 6px; border-radius:4px; font-size:13px; }
.msg-bubble.assistant :deep(pre) { background:#f6f8fa; padding:12px; border-radius:8px; overflow-x:auto; margin:8px 0; }
.msg-bubble.assistant :deep(pre code) { background:none; padding:0; }
.msg-bubble.assistant :deep(blockquote) { border-left:3px solid #409eff; padding-left:12px; color:#666; margin:8px 0; }

/* user 消息里的 Markdown（极少用，简单处理） */
.msg-bubble.user :deep(strong) { font-weight:600; }
.msg-bubble.user :deep(p) { margin:4px 0; }
</style>