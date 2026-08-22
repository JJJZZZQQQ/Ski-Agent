import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({ baseURL: '/api', timeout: 30000 })

request.interceptors.request.use(config => {
  const token = localStorage.getItem('ski_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  res => res.data,
  err => {
    const msg = err.response?.data?.message || err.message || '请求失败'
    ElMessage.error(msg)
    if (err.response?.status === 401) localStorage.removeItem('ski_token')
    return Promise.reject(err)
  }
)
export default request
