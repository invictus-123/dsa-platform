package com.online.judge.backend.converter;

import com.online.judge.backend.dto.request.CreateTestCaseRequest;
import com.online.judge.backend.dto.ui.TestCaseUi;
import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.TestCase;
import java.util.List;

public class TestCaseConverter {
	public static TestCaseUi toTestCaseUi(TestCase testCases) {
		return new TestCaseUi(testCases.getInput(), testCases.getOutput(), testCases.getExplanation());
	}

	public static List<TestCaseUi> toTestCaseUi(List<TestCase> testCases) {
		return testCases.stream().map(TestCaseConverter::toTestCaseUi).toList();
	}

	public static TestCase toTestCaseFromCreateTestCaseRequest(Problem problem, CreateTestCaseRequest request) {
		TestCase testCase = new TestCase();
		testCase.setInput(request.input());
		testCase.setOutput(request.expectedOutput());
		testCase.setExplanation(request.explanation());
		testCase.setIsSample(request.isSample());
		testCase.setProblem(problem);
		return testCase;
	}

	private TestCaseConverter() {
		// Prevent instantiation
	}
}
