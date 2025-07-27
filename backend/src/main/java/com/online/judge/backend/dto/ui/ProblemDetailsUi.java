package com.online.judge.backend.dto.ui;

import java.util.List;
import com.online.judge.backend.model.shared.ProblemDifficulty;
import com.online.judge.backend.model.shared.ProblemTag;

public record ProblemDetailsUi(
		Long id,
		String title,
		String statement,
		Double timeLimitInSecond,
		Integer memoryLimitInMb,
		ProblemDifficulty difficulty,
		List<ProblemTag> tags,
		List<TestCaseUi> sampleTestCases) {}
