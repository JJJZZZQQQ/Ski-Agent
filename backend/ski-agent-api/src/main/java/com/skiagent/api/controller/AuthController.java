package com.skiagent.api.controller;

import com.skiagent.api.service.AuthService;
import com.skiagent.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 认证接口 - 注册 / 登录 / 获取用户信息
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /** 注册（JSON body: {username, password, nickname?}） */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        return authService.register(body.get("username"), body.get("password"), body.get("nickname"));
    }

    /** 登录（JSON body: {username, password}） */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        return authService.login(body.get("username"), body.get("password"));
    }

    /** 获取当前用户信息（需 JWT） */
    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestAttribute Long userId) {
        return authService.me(userId);
    }
}
