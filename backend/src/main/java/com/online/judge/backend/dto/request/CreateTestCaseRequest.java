package com.online.judge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing the request body for creating a single test case.
 */
public record CreateTestCaseRequest(
		@NotBlank(message = "Input cannot be blank") String input,
		@NotBlank(message = "Expected output cannot be blank") String expectedOutput,
		@NotNull(message = "isSample flag must be provided") Boolean isSample,
		String explanation) {}
