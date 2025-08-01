package com.online.judge.backend.repository;

import com.online.judge.backend.model.TestCaseResult;
import com.online.judge.backend.model.shared.TestCaseResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseResultRepository extends JpaRepository<TestCaseResult, TestCaseResultId> {}
