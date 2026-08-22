import request from './request'

export const authApi = {
  login: (username: string, password: string) =>
    request.post('/auth/login', { username, password }),
  register: (username: string, password: string, nickname?: string) =>
    request.post('/auth/register', { username, password, nickname }),
  me: () => request.get('/auth/me')
}
