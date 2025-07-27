package com.online.judge.backend.dto.ui;

import com.online.judge.backend.model.shared.SubmissionStatus;

public record TestResultSummaryUi(
		Integer testCaseNumber, SubmissionStatus status, Double executionTime, Integer memoryUsed) {}
