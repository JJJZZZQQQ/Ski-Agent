-- ============================================================
-- 滑雪 Agent 数据库初始化脚本
-- 创建时间：2026-08-22
-- 使用方式：mysql -u ski_agent -p ski_agent < init.sql
-- ============================================================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `flow_work_ai_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名-登录用',
    `password_hash` VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码哈希',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`      VARCHAR(256) DEFAULT NULL COMMENT '头像 URL',
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '0=正常 1=禁用',
    `last_login_at` DATETIME   DEFAULT NULL COMMENT '最后登录时间',
    `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 会话线程表
CREATE TABLE IF NOT EXISTS `flow_work_ai_chat_thread` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `thread_id`       VARCHAR(64)  NOT NULL COMMENT 'AG-UI Thread UUID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户 ID',
    `title`           VARCHAR(128) DEFAULT NULL COMMENT '会话标题',
    `status`          TINYINT      NOT NULL DEFAULT 0 COMMENT '0=active 1=archived 2=deleted',
    `last_message_at` DATETIME     DEFAULT NULL COMMENT '最后消息时间',
    `metadata`        JSON         DEFAULT NULL COMMENT '扩展字段',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`      DATETIME     DEFAULT NULL COMMENT '软删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_thread_id` (`thread_id`),
    KEY               `idx_user_lastmsg` (`user_id`, `last_message_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话线程表';

-- 3. Agent 执行记录表
CREATE TABLE IF NOT EXISTS `flow_work_ai_chat_run` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `run_id`          VARCHAR(64)  NOT NULL COMMENT 'AG-UI Run UUID',
    `thread_id`       VARCHAR(64)  NOT NULL COMMENT '会话 Thread ID',
    `user_id`         BIGINT       NOT NULL COMMENT '用户 ID',
    `status`          VARCHAR(32)  NOT NULL DEFAULT 'running' COMMENT 'running/completed/failed/cancelled',
    `started_at`      DATETIME     DEFAULT NULL COMMENT '开始时间',
    `ended_at`        DATETIME     DEFAULT NULL COMMENT '结束时间',
    `duration_ms`     INT          DEFAULT NULL COMMENT '耗时（毫秒）',
    `input`           TEXT         DEFAULT NULL COMMENT '输入摘要',
    `output`          TEXT         DEFAULT NULL COMMENT '输出摘要',
    `error_message`   TEXT         DEFAULT NULL COMMENT '失败原因',
    `token_usage`     JSON         DEFAULT NULL COMMENT '{prompt_tokens,completion_tokens,total_tokens}',
    `cost`            DECIMAL(10,4) DEFAULT NULL COMMENT '费用估算',
    `model`           VARCHAR(64)  DEFAULT NULL COMMENT '模型名',
    `workflow_type`   VARCHAR(64)  DEFAULT NULL COMMENT '工作流类型（阶段3 用）',
    `metadata`        JSON         DEFAULT NULL COMMENT '扩展字段',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_run_id` (`run_id`),
    KEY               `idx_thread_started` (`thread_id`, `started_at` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 执行记录表';

-- 4. 消息表
CREATE TABLE IF NOT EXISTS `flow_work_ai_chat_message` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `message_id`        VARCHAR(64)  NOT NULL COMMENT 'AG-UI Message UUID',
    `thread_id`         VARCHAR(64)  NOT NULL COMMENT '会话 Thread ID',
    `run_id`            VARCHAR(64)  DEFAULT NULL COMMENT 'Run ID（user 消息可空）',
    `role`              VARCHAR(16)  NOT NULL COMMENT 'user/assistant/tool/system',
    `content`           MEDIUMTEXT   DEFAULT NULL COMMENT '消息内容',
    `tool_calls`        JSON         DEFAULT NULL COMMENT '工具调用（assistant 消息）',
    `tool_call_id`      VARCHAR(64)  DEFAULT NULL COMMENT '工具调用 ID（tool 消息）',
    `parent_message_id` VARCHAR(64)  DEFAULT NULL COMMENT '父消息 ID（支持消息分支）',
    `status`            TINYINT      NOT NULL DEFAULT 0 COMMENT '0=normal 1=deleted',
    `token_count`       INT          DEFAULT NULL COMMENT 'token 数',
    `metadata`          JSON         DEFAULT NULL COMMENT '扩展字段',
    `created_at`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_thread_created` (`thread_id`, `created_at`),
    KEY `idx_parent` (`parent_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';