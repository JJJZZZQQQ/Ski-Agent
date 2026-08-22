package com.skiagent.llm.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * DeepSeek 聊天服务
 * 使用 Spring AI OpenAI 兼容协议调用 DeepSeek V4 Flash
 */
@Service
public class DeepSeekChatService {

    private final ChatClient chatClient;

    public DeepSeekChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 流式对话 - 返回 token 流
     * @param prompt 用户输入
     * @return Flux<String> 流式 token
     */
    public Flux<String> streamChat(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
