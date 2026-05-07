package com.devtrace.manager.dashboard.service;

import com.devtrace.manager.dashboard.dto.DashboardResponse;

/**
 * 통합 운영 대시보드 조회를 담당하는 서비스 계약입니다.
 *
 * <p>대시보드는 프로젝트, 이슈, 공수, WBS, 테스트 증적, 변경이력, 산출물 데이터를
 * 첫 화면에서 판단 가능한 운영 지표로 조합합니다.</p>
 */
public interface DashboardService {

    /**
     * 대시보드 화면에 표시할 전체 운영 요약을 조회합니다.
     *
     * @return 대시보드 통합 응답
     */
    DashboardResponse selectDashboardDetails();
}
