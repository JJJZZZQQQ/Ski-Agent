import request from './request'

export const chatApi = {
  createThread: (title: string) => request.post('/chat/threads', null, { params: { title } }),
  listThreads: () => request.get('/chat/threads'),
  listMessages: (threadId: string) => request.get(`/chat/threads/${threadId}/messages`),
  /** SSE 流式聊天：返回 ReadableStream */
  streamChat: (threadId: string | null, content: string) => {
    const token = localStorage.getItem('ski_token')
    const params = new URLSearchParams({ content })
    if (threadId) params.append('threadId', threadId)
    return fetch(`/api/chat/stream?${params}`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` }
    })
  }
}
