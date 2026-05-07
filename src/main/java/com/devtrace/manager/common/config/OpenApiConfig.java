package com.devtrace.manager.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * springdoc-openapi 문서의 기본 메타데이터를 설정합니다.
 */
@Configuration
public class OpenApiConfig {

    /**
     * DevTrace Manager API 문서 정보를 구성합니다.
     *
     * @return OpenAPI 설정 객체
     */
    @Bean
    public OpenAPI devTraceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevTrace Manager API")
                        .version("v1")
                        .description("DevTrace Manager standalone API documentation"));
    }
}
