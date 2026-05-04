package com.devtrace.manager.vcs.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GitLogParserTest {

    private final GitLogParser parser = new GitLogParser();

    @Test
    void parsePrettyPipeGitLog() {
        UUID projectId = UUID.randomUUID();
        String log = "a1b2c3|kim|2026-05-04 09:00:00 +0900|[ISO-101] 사용자 목록 조회 오류 수정\n"
                + "d4e5f6|lee|2026-05-04 10:00:00 +0900|#ISO-102 개발서버 배포 스크립트 수정";

        List<VcsChangeLogEntity> logs = parser.parse(projectId, log);

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getRevisionNo()).isEqualTo("a1b2c3");
        assertThat(logs.get(0).getIssueKeys()).containsExactly("ISO-101");
        assertThat(logs.get(1).getIssueKeys()).containsExactly("ISO-102");
    }

    @Test
    void parseNameStatusGitLog() {
        UUID projectId = UUID.randomUUID();
        String log = """
                commit|fd9a123|kim|2026-05-04 09:00:00 +0900|ISO-103 파일 업로드 예외 처리
                M	src/main/java/App.java
                A	src/test/java/AppTest.java

                commit|ab8c777|lee|2026-05-04 11:10:00 +0900|문서 정리
                D	README.old.md
                """;

        List<VcsChangeLogEntity> logs = parser.parse(projectId, log);

        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getChangedFiles()).hasSize(2);
        assertThat(logs.get(0).getChangedFiles().get(0).getChangeType()).isEqualTo("M");
        assertThat(logs.get(0).getChangedFiles().get(0).getFilePath()).isEqualTo("src/main/java/App.java");
        assertThat(logs.get(0).getIssueKeys()).containsExactly("ISO-103");
        assertThat(logs.get(1).getChangedFiles().get(0).getChangeType()).isEqualTo("D");
    }
}
