package com.online.judge.backend.dto.ui;

import com.online.judge.backend.model.shared.SubmissionLanguage;
import com.online.judge.backend.model.shared.SubmissionStatus;
import java.time.Instant;
import java.util.List;

public record SubmissionDetailsUi(
		Long id,
		ProblemSummaryUi problemSummary,
		UserSummaryUi userSummary,
		SubmissionStatus status,
		SubmissionLanguage language,
		Instant submittedAt,
		String code,
		Double executionTime,
		Integer memoryUsed,
		List<TestResultSummaryUi> testResults) {}
