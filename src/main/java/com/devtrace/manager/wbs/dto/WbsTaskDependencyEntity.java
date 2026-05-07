package com.devtrace.manager.wbs.dto;

public class WbsTaskDependencyEntity extends WbsTaskDependencyResponse {

    /**
     * WBS 작업 의존성 엔티티를 화면/API 응답 DTO로 변환합니다.
     *
     * @return WBS 작업 의존성 응답 DTO
     */
    public WbsTaskDependencyResponse toResponse() {
        return this;
    }
}
