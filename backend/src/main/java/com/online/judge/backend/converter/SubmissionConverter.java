package com.online.judge.backend.converter;

import static com.online.judge.backend.converter.ProblemConverter.toProblemSummaryUi;
import static com.online.judge.backend.converter.UserConverter.toUserSummaryUi;

import com.online.judge.backend.dto.request.SubmitCodeRequest;
import com.online.judge.backend.dto.ui.SubmissionDetailsUi;
import com.online.judge.backend.dto.ui.SubmissionSummaryUi;
import com.online.judge.backend.model.Submission;
import com.online.judge.backend.model.shared.SubmissionStatus;
import java.util.List;

public class SubmissionConverter {
	public static SubmissionDetailsUi toSubmissionDetailsUi(Submission submission) {
		return new SubmissionDetailsUi(
				submission.getId(),
				toProblemSummaryUi(submission.getProblem()),
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
				toProblemSummaryUi(submission.getProblem()),
				toUserSummaryUi(submission.getUser()),
				submission.getStatus(),
				submission.getLanguage(),
				submission.getSubmittedAt());
	}

	public static Submission toSubmissionFromRequest(SubmitCodeRequest request) {
		Submission submission = new Submission();
		submission.setCode(request.code());
		submission.setLanguage(request.language());
		submission.setStatus(SubmissionStatus.WAITING_FOR_EXECUTION);
		return submission;
	}

	private SubmissionConverter() {
		// Prevent instantiation
	}
}
