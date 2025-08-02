package com.online.judge.backend.dto.message;

import jakarta.validation.constraints.NotNull;

public record SubmissionMessage(@NotNull Long submissionId) {}
