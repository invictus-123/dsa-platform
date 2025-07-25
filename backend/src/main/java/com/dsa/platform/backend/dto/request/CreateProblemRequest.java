package com.dsa.platform.backend.dto.request;

import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO representing the request body for creating a new problem.
 */
public record CreateProblemRequest(
		@NotBlank(message = "Title cannot be blank") String title,
		@NotBlank(message = "Statement cannot be blank") String statement,
		@NotNull(message = "Difficulty cannot be null") ProblemDifficulty difficulty,
		@NotNull(message = "Time limit must be provided") Double timeLimit,
		@NotNull(message = "Memory limit must be provided") Integer memoryLimit,
		@NotEmpty(message = "Tags cannot be empty") List<ProblemTag> tags,
		@NotEmpty(message = "Test cases cannot be empty") @Valid List<CreateTestCaseRequest> testCases) {}
