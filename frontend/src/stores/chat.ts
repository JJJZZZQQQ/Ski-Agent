import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatApi } from '@/api/agent'

export const useChatStore = defineStore('chat', () => {
  const threads = ref<any[]>([])
  const currentThreadId = ref<string | null>(null)
  const messages = ref<any[]>([])
  const isStreaming = ref(false)

  async function loadThreads() {
    const res = await chatApi.listThreads()
    threads.value = res.data || []
  }

  async function createThread(title?: string) {
    const res = await chatApi.createThread(title || '新对话')
    threads.value.unshift({ threadId: res.threadId, title: res.title })
    return res.threadId
  }

  async function loadMessages(threadId: string) {
    const res = await chatApi.listMessages(threadId)
    messages.value = res.data || []
  }

  /** 开始流式聊天，返回 SSE 事件流 reader */
  function startStream(threadId: string | null, content: string) {
    isStreaming.value = true
    return chatApi.streamChat(threadId, content)
  }

  function stopStream() {
    isStreaming.value = false
  }

  function addMessage(msg: any) { messages.value.push(msg) }
  function updateLastMessage(content: string) {
    if (messages.value.length > 0) messages.value[messages.value.length - 1].content = content
  }

  return { threads, currentThreadId, messages, isStreaming, loadThreads, createThread, loadMessages, startStream, stopStream, addMessage, updateLastMessage }
})
