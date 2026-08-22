package com.skiagent.api.controller;

import com.alibaba.fastjson2.JSON;
import com.skiagent.api.service.ChatService;
import com.skiagent.common.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;

/**
 * 聊天接口 - AG-UI SSE 流式聊天 + 会话管理
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /** 创建会话 */
    @PostMapping("/threads")
    public Result<Map<String, Object>> createThread(@RequestAttribute Long userId, @RequestParam(defaultValue = "新对话") String title) {
        var thread = chatService.createThread(userId, title);
        return Result.ok(Map.of("threadId", thread.getThreadId(), "title", thread.getTitle()));
    }

    /** 会话列表 */
    @GetMapping("/threads")
    public Result<Object> listThreads(@RequestAttribute Long userId) {
        return Result.ok(chatService.listThreads(userId));
    }

    /** 会话消息历史 */
    @GetMapping("/threads/{threadId}/messages")
    public Result<Object> listMessages(@PathVariable String threadId) {
        return Result.ok(chatService.listMessages(threadId));
    }

    /**
     * AG-UI SSE 流式聊天
     * 前端 subscribe 此接口，实时接收 AG-UI 事件流
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(
            @RequestAttribute Long userId,
            @RequestParam(required = false) String threadId,
            @RequestParam String content) {
        return chatService.streamChat(userId, threadId, content);
    }
}
