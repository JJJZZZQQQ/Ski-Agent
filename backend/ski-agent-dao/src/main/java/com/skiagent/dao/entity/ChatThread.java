package com.skiagent.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话线程表实体 - flow_work_ai_chat_thread
 */
@Data
@TableName("flow_work_ai_chat_thread")
public class ChatThread {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("thread_id") private String threadId;
    @TableField("user_id") private Long userId;
    @TableField("title") private String title;
    @TableField("status") private Integer status;
    @TableField("last_message_at") private LocalDateTime lastMessageAt;
    @TableField(value = "metadata", typeHandler = com.skiagent.dao.config.JsonTypeHandler.class) private Object metadata;
    @TableField(value = "created_at", fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    @TableLogic @TableField("deleted_at") private LocalDateTime deletedAt;
}
