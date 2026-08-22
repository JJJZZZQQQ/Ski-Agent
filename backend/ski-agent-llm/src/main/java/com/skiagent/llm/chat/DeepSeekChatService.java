package com.skiagent.llm.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * DeepSeek 聊天服务
 * 使用 Spring AI OpenAI 兼容协议调用 DeepSeek V4 Flash
 * 内置滑雪 Agent 专属 System Prompt
 */
@Service
public class DeepSeekChatService {

    /**
     * 滑雪 Agent 系统提示词
     * 定义角色定位、回复风格、能力边界
     */
    private static final String SYSTEM_PROMPT = """
            你是滑雪Agent（Ski Agent），一个专业的滑雪领域智能助手。

            你的职责：
            - 滑雪场推荐与出行规划
            - 滑雪装备选购与二手评估
            - 滑雪动作技巧与安全指导
            - 滑雪相关问题的诊断与建议

            回复要求：
            - 使用清晰的结构化Markdown格式（标题、列表等），让信息一目了然
            - **不要用代码块（```）包裹正常内容**，代码块仅用于展示代码片段或JSON参数
            - 语言友好专业，像一个经验丰富的滑雪教练在给学员讲解
            - 优先根据用户滑雪水平和偏好给出个性化建议

            你绝对不能做的事：
            - 开放式闲聊（如"滑雪起源是什么"、"滑雪好玩吗"等科普问答）
            - 通用旅游百科（与滑雪无关的旅游、美食、天气信息）
            - 长篇教学课本文本（只做针对性解答，不做完整课程输出）
            - 通用生活问答

            如果用户问的是非滑雪相关问题，礼貌地引导回滑雪主题。
            """;

    private final ChatClient chatClient;

    public DeepSeekChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 流式对话 - 返回 token 流
     * 每次请求均注入 System Prompt 以确保角色一致性
     * @param prompt 用户输入
     * @return Flux<String> 流式 token
     */
    public Flux<String> streamChat(String prompt) {
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(prompt)
                .stream()
                .content();
    }
}