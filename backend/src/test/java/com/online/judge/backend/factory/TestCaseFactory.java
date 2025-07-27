package com.online.judge.backend.factory;

import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.TestCase;

public class TestCaseFactory {
	private static final Boolean IS_SAMPLE = true;
	private static final String INPUT = "Test1 Input";
	private static final String OUTPUT = "Test1 Output";
	private static final String EXPLANATION = "Test1 Explanation";

	public static TestCase createTestCase(Problem problem) {
		return createTestCase(problem, IS_SAMPLE);
	}

	public static TestCase createTestCase(Problem problem, boolean isSample) {
		if (isSample) {
			return createTestCase(problem, isSample, INPUT, OUTPUT, EXPLANATION);
		}
		return createTestCase(problem, isSample, INPUT, OUTPUT);
	}

	public static TestCase createTestCase(Problem problem, boolean isSample, String input, String output) {
		TestCase testCase = new TestCase();
		testCase.setProblem(problem);
		testCase.setIsSample(isSample);
		testCase.setInput(input);
		testCase.setOutput(output);
		return testCase;
	}

	public static TestCase createTestCase(
			Problem problem, boolean isSample, String input, String output, String explanation) {
		TestCase testCase = createTestCase(problem, isSample, input, output);
		testCase.setExplanation(explanation);
		return testCase;
	}

	private TestCaseFactory() {
		// Prevent instantiation
	}
}
