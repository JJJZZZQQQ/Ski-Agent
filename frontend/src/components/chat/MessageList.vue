<script setup lang="ts">
import { ref, watch, nextTick, computed } from 'vue'

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

defineExpose({ scrollToBottom: scrollNow })
</script>

<template>
  <div class="message-list" ref="listRef" @scroll="onScroll">
    <div v-if="messages.length===0" class="empty">👋 开始对话吧！我是你的滑雪专属助手~</div>
    <div v-for="(msg, i) in messages" :key="i" :class="['message', msg.role]">
      <div class="msg-avatar">{{ msg.role==='user' ? '🧑' : '🎿' }}</div>
      <div class="msg-content">{{ msg.content }}{{ (i===messages.length-1 && streaming && msg.role==='assistant') ? '▊' : '' }}</div>
    </div>
  </div>
</template>

<style scoped>
.message-list { flex:1; overflow-y:auto; padding:16px; background:#fafafa; }
.empty { text-align:center; padding:80px 0; color:#999; font-size:16px; }
.message { display:flex; gap:12px; margin-bottom:20px; }
.message.user { flex-direction:row-reverse; }
.msg-avatar { width:36px; height:36px; border-radius:50%; background:#e4e7ed; display:flex; align-items:center; justify-content:center; font-size:18px; flex-shrink:0; }
.msg-content { max-width:70%; padding:10px 16px; border-radius:12px; line-height:1.6; white-space:pre-wrap; word-break:break-word; }
.message.user .msg-content { background:#409eff; color:#fff; border-bottom-right-radius:4px; }
.message.assistant .msg-content { background:#fff; color:#333; border-bottom-left-radius:4px; box-shadow:0 1px 3px rgba(0,0,0,.08); }
</style>