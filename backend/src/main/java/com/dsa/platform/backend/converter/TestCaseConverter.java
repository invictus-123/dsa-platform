package com.dsa.platform.backend.converter;

import java.util.List;

import com.dsa.platform.backend.dto.ui.TestCaseUi;
import com.dsa.platform.backend.model.TestCase;

public class TestCaseConverter {
    public static TestCaseUi toTestCaseUi(TestCase testCases) {
        return new TestCaseUi(
                testCases.getInput(),
                testCases.getOutput(),
                testCases.getExplanation());
    }

    public static List<TestCaseUi> toTestCaseUi(List<TestCase> testCases) {
        return testCases.stream()
                .map(TestCaseConverter::toTestCaseUi)
                .toList();
    }

    private TestCaseConverter() {
        // Prevent instantiation
    }
}
