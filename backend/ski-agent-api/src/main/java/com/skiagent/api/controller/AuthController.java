package com.skiagent.api.controller;

import com.skiagent.api.service.AuthService;
import com.skiagent.common.dto.Result;
import jakarta.validation.constraints.NotBlank;
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

    /** 注册 */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(
            @RequestParam @NotBlank String username,
            @RequestParam @NotBlank String password,
            @RequestParam(required = false) String nickname) {
        return authService.register(username, password, nickname);
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @RequestParam @NotBlank String username,
            @RequestParam @NotBlank String password) {
        return authService.login(username, password);
    }

    /** 获取当前用户信息（需 JWT） */
    @GetMapping("/me")
    public Result<Map<String, Object>> me(@RequestAttribute Long userId) {
        return authService.me(userId);
    }
}
