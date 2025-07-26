package com.dsa.platform.backend.converter;

import static com.dsa.platform.backend.converter.TagConverter.toTagFromProblemTag;
import static com.dsa.platform.backend.converter.TestCaseConverter.toTestCaseFromCreateTestCaseRequest;
import static com.dsa.platform.backend.converter.TestCaseConverter.toTestCaseUi;

import com.dsa.platform.backend.dto.request.CreateProblemRequest;
import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import com.dsa.platform.backend.model.Problem;
import com.dsa.platform.backend.model.Tag;
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

	public static Problem toProblemFromCreateProblemRequest(CreateProblemRequest request) {
		Problem problem = new Problem();
		problem.setTitle(request.title());
		problem.setStatement(request.statement());
		problem.setDifficulty(request.difficulty());
		problem.setTimeLimitSecond(request.timeLimit());
		problem.setMemoryLimitMb(request.memoryLimit());

		List<Tag> tags = request.tags().stream()
				.map(problemTag -> toTagFromProblemTag(problem, problemTag))
				.toList();
		problem.setTags(tags);

		List<TestCase> testCases = request.testCases().stream()
				.map(tcRequest -> toTestCaseFromCreateTestCaseRequest(problem, tcRequest))
				.toList();
		problem.setTestCases(testCases);

		return problem;
	}

	private ProblemConverter() {
		// Prevent instantiation
	}
}
