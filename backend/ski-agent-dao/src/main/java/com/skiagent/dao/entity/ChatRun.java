package com.skiagent.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agent 执行记录表实体 - flow_work_ai_chat_run
 */
@Data
@TableName("flow_work_ai_chat_run")
public class ChatRun {
    @TableId(type = IdType.AUTO) private Long id;
    @TableField("run_id") private String runId;
    @TableField("thread_id") private String threadId;
    @TableField("user_id") private Long userId;
    @TableField("status") private String status;
    @TableField("started_at") private LocalDateTime startedAt;
    @TableField("ended_at") private LocalDateTime endedAt;
    @TableField("duration_ms") private Integer durationMs;
    @TableField("input") private String input;
    @TableField("output") private String output;
    @TableField("error_message") private String errorMessage;
    @TableField(value = "token_usage", typeHandler = com.skiagent.dao.config.JsonTypeHandler.class) private Object tokenUsage;
    @TableField("cost") private BigDecimal cost;
    @TableField("model") private String model;
    @TableField("workflow_type") private String workflowType;
    @TableField(value = "metadata", typeHandler = com.skiagent.dao.config.JsonTypeHandler.class) private Object metadata;
    @TableField(value = "created_at", fill = FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
