package com.devtrace.manager.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {
        "com.devtrace.manager.project.dao",
        "com.devtrace.manager.issue.dao",
        "com.devtrace.manager.worklog.dao",
        "com.devtrace.manager.board.dao",
        "com.devtrace.manager.vcs.dao",
        "com.devtrace.manager.columnspec.dao",
        "com.devtrace.manager.artifact.dao",
        "com.devtrace.manager.testevidence.dao",
        "com.devtrace.manager.wbs.dao",
        "com.devtrace.manager.dashboard.dao",
        "com.devtrace.manager.sprint.dao"
})
public class MyBatisConfig {
}
