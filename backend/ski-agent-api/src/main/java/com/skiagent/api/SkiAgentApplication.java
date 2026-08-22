package com.skiagent.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 滑雪 Agent 应用启动类
 * 扫描 com.skiagent 包下所有 Bean
 */
@SpringBootApplication(scanBasePackages = "com.skiagent")
public class SkiAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkiAgentApplication.class, args);
    }
}
