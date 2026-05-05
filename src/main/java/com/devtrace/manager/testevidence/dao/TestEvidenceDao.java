package com.devtrace.manager.testevidence.dao;

import com.devtrace.manager.testevidence.dto.TestEvidenceEntity;
import com.devtrace.manager.testevidence.dto.TestEvidenceSearchCondition;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestEvidenceDao {

    void insertTestEvidence(TestEvidenceEntity testEvidence);

    void updateTestEvidence(TestEvidenceEntity testEvidence);

    void deleteTestEvidence(UUID testEvidenceId);

    Optional<TestEvidenceEntity> selectTestEvidenceDetails(UUID testEvidenceId);

    List<TestEvidenceEntity> selectTestEvidenceList(TestEvidenceSearchCondition condition);
}
