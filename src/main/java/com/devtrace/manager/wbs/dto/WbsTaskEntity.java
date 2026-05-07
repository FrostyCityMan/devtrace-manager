package com.devtrace.manager.wbs.dto;

public class WbsTaskEntity extends WbsTaskResponse {

    /**
     * WBS 작업 엔티티를 화면/API 응답 DTO로 변환합니다.
     *
     * @return WBS 작업 응답 DTO
     */
    public WbsTaskResponse toResponse() {
        return this;
    }
}
