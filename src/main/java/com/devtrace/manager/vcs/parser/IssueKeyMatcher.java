package com.devtrace.manager.vcs.parser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 변경이력 메시지에서 이슈 키를 추출하는 유틸리티입니다.
 *
 * <p>{@code ISO-101}, {@code [ISO-101]}, {@code #ISO-101} 형태를 인식하며,
 * 추출된 키는 대문자로 정규화하고 입력 순서의 중복을 제거합니다.</p>
 */
public final class IssueKeyMatcher {

    private static final Pattern ISSUE_KEY_PATTERN = Pattern.compile("(?i)(?:#|\\[)?\\b([A-Z][A-Z0-9]+-\\d+)\\b\\]?");

    private IssueKeyMatcher() {
    }

    /**
     * 문자열에서 DevTrace 이슈 키 패턴을 추출합니다.
     *
     * @param text 검사 대상 문자열
     * @return 중복 제거된 이슈 키 목록
     */
    public static List<String> extractIssueKeys(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        Set<String> issueKeys = new LinkedHashSet<>();
        Matcher matcher = ISSUE_KEY_PATTERN.matcher(text);
        while (matcher.find()) {
            issueKeys.add(matcher.group(1).toUpperCase());
        }
        return new ArrayList<>(issueKeys);
    }
}
