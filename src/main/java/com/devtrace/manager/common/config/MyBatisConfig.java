package com.devtrace.manager.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * DevTrace Manager의 MyBatis Mapper 스캔 범위를 설정합니다.
 *
 * <p>도메인별 DAO 패키지를 명시적으로 등록하여 Controller, Service, DAO 계층 분리를
 * 유지하면서 MyBatis XML Mapper와 연결합니다.</p>
 */
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
