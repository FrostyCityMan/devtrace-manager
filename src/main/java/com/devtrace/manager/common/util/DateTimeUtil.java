package com.devtrace.manager.common.util;

import java.time.LocalDateTime;

/**
 * 날짜/시간 값을 생성하는 공통 유틸리티입니다.
 *
 * <p>현재는 시스템 현재 시각만 제공하지만, 테스트 고정 시각 전략을 도입할 때
 * 중앙 변경 지점으로 활용할 수 있습니다.</p>
 */
public final class DateTimeUtil {

    /**
     * 인스턴스 생성을 막습니다.
     */
    private DateTimeUtil() {
    }

    /**
     * 현재 로컬 일시를 반환합니다.
     *
     * @return 현재 일시
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
