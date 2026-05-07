package com.devtrace.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DevTrace Manager Spring Boot 애플리케이션의 진입점입니다.
 */
@SpringBootApplication
public class DevTraceManagerApplication {

    /**
     * 애플리케이션을 시작합니다.
     *
     * @param args 실행 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(DevTraceManagerApplication.class, args);
    }
}
