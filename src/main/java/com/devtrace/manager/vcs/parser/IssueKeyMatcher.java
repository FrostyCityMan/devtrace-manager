package com.devtrace.manager.vcs.parser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class IssueKeyMatcher {

    private static final Pattern ISSUE_KEY_PATTERN = Pattern.compile("(?i)(?:#|\\[)?\\b([A-Z][A-Z0-9]+-\\d+)\\b\\]?");

    private IssueKeyMatcher() {
    }

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
