package com.skiagent.api.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.skiagent.dao.entity.*;
import com.skiagent.dao.mapper.*;
import com.skiagent.llm.chat.DeepSeekChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 聊天服务 - 管理会话线程、执行记录和流式消息
 * 实现 AG-UI 协议的 Thread/Run/Message 三层模型
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatThreadMapper threadMapper;
    private final ChatRunMapper runMapper;
    private final ChatMessageMapper messageMapper;
    private final DeepSeekChatService deepSeekChatService;

    /**
     * 创建新会话线程
     * @param userId 用户 ID
     * @param title  会话标题（可选）
     * @return ChatThread
     */
    public ChatThread createThread(Long userId, String title) {
        ChatThread thread = new ChatThread();
        thread.setThreadId("thd_" + UUID.randomUUID().toString().replace("-", ""));
        thread.setUserId(userId);
        thread.setTitle(title != null ? title : "新对话");
        thread.setStatus(0);
        thread.setLastMessageAt(LocalDateTime.now());
        threadMapper.insert(thread);
        log.info("创建会话线程: threadId={}, userId={}", thread.getThreadId(), userId);
        return thread;
    }

    /**
     * 获取用户的所有会话线程（按最后消息时间倒序）
     * @param userId 用户 ID
     * @return 线程列表
     */
    public List<ChatThread> listThreads(Long userId) {
        return threadMapper.selectList(
                new LambdaQueryWrapper<ChatThread>()
                        .eq(ChatThread::getUserId, userId)
                        .eq(ChatThread::getStatus, 0)
                        .orderByDesc(ChatThread::getLastMessageAt)
        );
    }

    /**
     * 获取会话的所有消息（按时间正序）
     * @param threadId 会话 ID
     * @return 消息列表
     */
    public List<ChatMessage> listMessages(String threadId) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getThreadId, threadId)
                        .eq(ChatMessage::getStatus, 0)
                        .orderByAsc(ChatMessage::getCreatedAt)
        );
    }

    /**
     * 流式聊天（SSE）- 核心方法
     * 实现 AG-UI 协议的 run 生命周期：run_started → text_message_start/end → run_finished
     *
     * @param userId   用户 ID
     * @param threadId 会话 ID（可选，不传则自动创建）
     * @param content  用户输入的内容
     * @return SseEmitter 流式事件发射器
     */
    public SseEmitter streamChat(Long userId, String threadId, String content) {
        SseEmitter emitter = new SseEmitter(300000L); // 5 分钟超时

        CompletableFuture.runAsync(() -> {
            try {
                // 1. 获取或创建 thread
                ChatThread thread = getOrCreateThread(userId, threadId);

                // 2. 创建 run
                ChatRun run = createRun(userId, thread.getThreadId());

                // 3. 保存 user message
                String userMessageId = saveMessage(thread.getThreadId(), run.getRunId(), "user", content);

                // 4. 发送 run_started 事件
                sendEvent(emitter, "run_started", JSON.toJSONString(Map.of("runId", run.getRunId(), "threadId", thread.getThreadId())));

                // 5. 流式调用 DeepSeek 并推送 AG-UI 事件
                String assistantMessageId = "msg_" + UUID.randomUUID().toString().replace("-", "");
                sendEvent(emitter, "text_message_start", JSON.toJSONString(Map.of("messageId", assistantMessageId)));

                StringBuilder fullResponse = new StringBuilder();
                deepSeekChatService.streamChat(content)
                        .doOnNext(token -> {
                            try {
                                fullResponse.append(token);
                                sendEvent(emitter, "text_message_content",
                                        JSON.toJSONString(Map.of("messageId", assistantMessageId, "delta", token)));
                            } catch (Exception e) {
                                log.error("SSE 推送 token 失败", e);
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                sendEvent(emitter, "text_message_end", JSON.toJSONString(Map.of("messageId", assistantMessageId)));
                                sendEvent(emitter, "run_finished", JSON.toJSONString(Map.of("runId", run.getRunId())));

                                // 6. 保存 assistant message
                                String responseText = fullResponse.toString();
                                saveMessage(thread.getThreadId(), run.getRunId(), "assistant", responseText);

                                // 7. 更新 run 状态
                                updateRunCompleted(run, responseText);

                                // 8. 更新 thread 的最后消息时间
                                thread.setLastMessageAt(LocalDateTime.now());
                                if ("新对话".equals(thread.getTitle())) {
                                    thread.setTitle(content.length() > 20 ? content.substring(0, 20) + "..." : content);
                                }
                                threadMapper.updateById(thread);

                                // 9. 关闭 SSE
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("SSE 完成处理失败", e);
                                emitter.completeWithError(e);
                            }
                        })
                        .doOnError(e -> {
                            log.error("DeepSeek 调用失败", e);
                            sendEventQuietly(emitter, "run_error",
                                    JSON.toJSONString(Map.of("runId", run.getRunId(), "error", e.getMessage())));
                            updateRunFailed(run, e.getMessage());
                            emitter.completeWithError(e);
                        })
                        .subscribe();

            } catch (Exception e) {
                log.error("流式聊天失败", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /** 获取或创建 thread */
    private ChatThread getOrCreateThread(Long userId, String threadId) {
        if (threadId != null && !threadId.isEmpty()) {
            ChatThread thread = threadMapper.selectOne(
                    new LambdaQueryWrapper<ChatThread>().eq(ChatThread::getThreadId, threadId));
            if (thread != null) return thread;
        }
        return createThread(userId, null);
    }

    /** 创建 run 记录 */
    private ChatRun createRun(Long userId, String threadId) {
        ChatRun run = new ChatRun();
        run.setRunId("run_" + UUID.randomUUID().toString().replace("-", ""));
        run.setThreadId(threadId);
        run.setUserId(userId);
        run.setStatus("running");
        run.setStartedAt(LocalDateTime.now());
        run.setModel("deepseek-v4-flash");
        runMapper.insert(run);
        return run;
    }

    /** 保存消息到数据库 */
    private String saveMessage(String threadId, String runId, String role, String content) {
        String messageId = "msg_" + UUID.randomUUID().toString().replace("-", "");
        ChatMessage msg = new ChatMessage();
        msg.setMessageId(messageId);
        msg.setThreadId(threadId);
        msg.setRunId(role.equals("user") ? null : runId); // user 消息不挂 run
        msg.setRole(role);
        msg.setContent(content);
        msg.setStatus(0);
        messageMapper.insert(msg);
        return messageId;
    }

    /** 更新 run 为完成状态 */
    private void updateRunCompleted(ChatRun run, String output) {
        run.setStatus("completed");
        run.setEndedAt(LocalDateTime.now());
        run.setDurationMs((int) ChronoUnit.MILLIS.between(run.getStartedAt(), run.getEndedAt()));
        run.setOutput(output.length() > 500 ? output.substring(0, 500) : output);
        runMapper.updateById(run);
    }

    /** 更新 run 为失败状态 */
    private void updateRunFailed(ChatRun run, String error) {
        run.setStatus("failed");
        run.setEndedAt(LocalDateTime.now());
        run.setDurationMs((int) ChronoUnit.MILLIS.between(run.getStartedAt(), run.getEndedAt()));
        run.setErrorMessage(error);
        runMapper.updateById(run);
    }

    /** 发送 SSE 事件 */
    private void sendEvent(SseEmitter emitter, String eventName, String data) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(data));
    }

    /** 静默发送 SSE 事件（忽略错误） */
    private void sendEventQuietly(SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException ignored) {}
    }
}