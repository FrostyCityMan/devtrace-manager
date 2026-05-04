package com.devtrace.manager.vcs.parser;

import com.devtrace.manager.vcs.dto.VcsChangeLogEntity;
import java.util.List;
import java.util.UUID;

public interface VcsLogParser {

    List<VcsChangeLogEntity> parse(UUID projectId, String logText);
}
