package com.dsa.platform.backend.dto.ui;

import java.util.List;

import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;

public record ProblemDetailsUi(
        Long id,
        String title,
        String statement,
        Double timeLimitInSecond,
        Integer memoryLimitInMb,
        ProblemDifficulty difficulty,
        List<ProblemTag> tags,
        List<TestCaseUi> sampleTestCases) {
}