package com.dsa.platform.backend.converter;

import static com.dsa.platform.backend.converter.TestCaseConverter.toTestCaseUi;

import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import com.dsa.platform.backend.model.Problem;
import com.dsa.platform.backend.model.TestCase;
import com.dsa.platform.backend.model.shared.ProblemTag;
import java.util.List;

public class ProblemConverter {
	public static ProblemSummaryUi toProblemSummaryUi(Problem problem, List<ProblemTag> tags) {
		return new ProblemSummaryUi(problem.getId(), problem.getTitle(), problem.getDifficulty(), tags);
	}

	public static ProblemDetailsUi toProblemDetailsUi(
			Problem problem, List<ProblemTag> tags, List<TestCase> sampleTestCases) {
		return new ProblemDetailsUi(
				problem.getId(),
				problem.getTitle(),
				problem.getStatement(),
				problem.getTimeLimitSecond(),
				problem.getMemoryLimitMb(),
				problem.getDifficulty(),
				tags,
				toTestCaseUi(sampleTestCases));
	}

	private ProblemConverter() {
		// Prevent instantiation
	}
}
