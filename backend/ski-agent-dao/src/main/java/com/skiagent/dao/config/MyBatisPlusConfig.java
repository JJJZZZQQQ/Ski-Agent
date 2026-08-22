package com.skiagent.dao.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/** MyBatis-Plus 配置 - 扫描 Mapper 包 */
@Configuration
@MapperScan("com.skiagent.dao.mapper")
public class MyBatisPlusConfig {}
