import request from './request'

export const authApi = {
  login: (username: string, password: string) =>
    request.post('/auth/login', null, { params: { username, password } }),
  register: (username: string, password: string, nickname?: string) =>
    request.post('/auth/register', null, { params: { username, password, nickname } }),
  me: () => request.get('/auth/me')
}
