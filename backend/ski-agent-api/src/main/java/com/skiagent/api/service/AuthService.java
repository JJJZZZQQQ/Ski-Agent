package com.skiagent.api.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skiagent.common.dto.Result;
import com.skiagent.common.exception.BusinessException;
import com.skiagent.common.util.JwtUtil;
import com.skiagent.dao.entity.User;
import com.skiagent.dao.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 认证服务 - 注册、登录、用户信息查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    public Result<Map<String, Object>> register(String username, String password, String nickname) {
        // 检查用户名唯一性
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (count > 0) throw BusinessException.badRequest("用户名已存在");

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setStatus(0);

        userMapper.insert(user);
        log.info("用户注册成功: username={}, id={}", username, user.getId());

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return Result.ok(Map.of("token", token, "user", buildUserInfo(user)));
    }

    /**
     * 用户登录
     */
    public Result<Map<String, Object>> login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) throw BusinessException.unauthorized("用户名或密码错误");
        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw BusinessException.unauthorized("用户名或密码错误");

        // 更新登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功: userId={}", user.getId());
        return Result.ok(Map.of("token", token, "user", buildUserInfo(user)));
    }

    /**
     * 获取当前用户信息
     */
    public Result<Map<String, Object>> me(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw BusinessException.notFound("用户不存在");
        return Result.ok(buildUserInfo(user));
    }

    /** 转换为前端需要的用户信息（不含敏感字段） */
    private Map<String, Object> buildUserInfo(User user) {
        return Map.of("id", user.getId(), "username", user.getUsername(),
                "nickname", user.getNickname() != null ? user.getNickname() : user.getUsername(),
                "avatar", user.getAvatar() != null ? user.getAvatar() : "");
    }
}
