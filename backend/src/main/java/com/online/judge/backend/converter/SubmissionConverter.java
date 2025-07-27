package com.online.judge.backend.converter;

import static com.online.judge.backend.converter.ProblemConverter.toProblemSummaryUi;
import static com.online.judge.backend.converter.UserConverter.toUserSummaryUi;

import com.online.judge.backend.dto.ui.SubmissionDetailsUi;
import com.online.judge.backend.dto.ui.SubmissionSummaryUi;
import com.online.judge.backend.model.Submission;
import java.util.List;

public class SubmissionConverter {
	public static SubmissionDetailsUi toSubmissionDetailsUi(Submission submission) {
		return new SubmissionDetailsUi(
				submission.getId(),
				toProblemSummaryUi(submission.getProblem(), List.of()),
				toUserSummaryUi(submission.getUser()),
				submission.getStatus(),
				submission.getLanguage(),
				submission.getSubmittedAt(),
				submission.getCode(),
				submission.getExecutionTimeSeconds(),
				submission.getMemoryUsedMb(),
				List.of());
	}

	public static SubmissionSummaryUi toSubmissionSummaryUi(Submission submission) {
		return new SubmissionSummaryUi(
				submission.getId(),
				toProblemSummaryUi(submission.getProblem(), List.of()),
				toUserSummaryUi(submission.getUser()),
				submission.getStatus(),
				submission.getLanguage(),
				submission.getSubmittedAt());
	}

	private SubmissionConverter() {
		// Prevent instantiation
	}
}
