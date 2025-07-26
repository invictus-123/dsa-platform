package com.dsa.platform.backend.repository;

import com.dsa.platform.backend.model.TestCaseResult;
import com.dsa.platform.backend.model.shared.TestCaseResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseResultRepository extends JpaRepository<TestCaseResult, TestCaseResultId> {}
