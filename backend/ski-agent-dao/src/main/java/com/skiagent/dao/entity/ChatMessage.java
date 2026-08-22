package com.skiagent.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息表实体 - flow_work_ai_chat_message
 */
@Data
@TableName("flow_work_ai_chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("message_id") private String messageId;
    @TableField("thread_id") private String threadId;
    @TableField("run_id") private String runId;
    @TableField("role") private String role;
    @TableField("content") private String content;
    @TableField(value = "tool_calls", typeHandler = com.skiagent.dao.config.JsonTypeHandler.class) private Object toolCalls;
    @TableField("tool_call_id") private String toolCallId;
    @TableField("parent_message_id") private String parentMessageId;
    @TableField("status") private Integer status;
    @TableField("token_count") private Integer tokenCount;
    @TableField(value = "metadata", typeHandler = com.skiagent.dao.config.JsonTypeHandler.class) private Object metadata;
    @TableField(value = "created_at", fill = FieldFill.INSERT) private LocalDateTime createdAt;
}
