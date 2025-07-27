package com.online.judge.backend.dto.ui;

import com.online.judge.backend.model.shared.SubmissionLanguage;
import com.online.judge.backend.model.shared.SubmissionStatus;
import java.time.Instant;

public record SubmissionSummaryUi(
		Long id,
		ProblemSummaryUi problemSummary,
		UserSummaryUi userSummary,
		SubmissionStatus status,
		SubmissionLanguage language,
		Instant submittedAt) {}
