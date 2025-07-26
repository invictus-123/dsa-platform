package com.dsa.platform.backend.dto.ui;

import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;
import java.util.List;

public record ProblemDetailsUi(
		Long id,
		String title,
		String statement,
		Double timeLimitInSecond,
		Integer memoryLimitInMb,
		ProblemDifficulty difficulty,
		List<ProblemTag> tags,
		List<TestCaseUi> sampleTestCases) {}
