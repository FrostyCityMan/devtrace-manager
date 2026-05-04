package com.devtrace.manager.vcs.parser;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SvnLogParserTest {

    private final SvnLogParser parser = new SvnLogParser();

    @Test
    void parseSvnXmlLog() {
        UUID projectId = UUID.randomUUID();
        String xml = """
                <log>
                  <logentry revision="101">
                    <author>kim</author>
                    <date>2026-05-04T00:00:00.000000Z</date>
                    <paths>
                      <path action="M">/trunk/src/App.java</path>
                      <path action="A">/trunk/src/NewFile.java</path>
                    </paths>
                    <msg>[ISO-201] SVN 로그 파서 작성</msg>
                  </logentry>
                </log>
                """;

        List<VcsChangeLogEntity> logs = parser.parse(projectId, xml);

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getRevisionNo()).isEqualTo("101");
        assertThat(logs.get(0).getIssueKeys()).containsExactly("ISO-201");
        assertThat(logs.get(0).getChangedFiles()).hasSize(2);
        assertThat(logs.get(0).getChangedFiles().get(0).getFilePath()).isEqualTo("/trunk/src/App.java");
    }
}
