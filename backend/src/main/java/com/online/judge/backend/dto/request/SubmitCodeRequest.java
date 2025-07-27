package com.online.judge.backend.dto.request;

import com.online.judge.backend.model.shared.SubmissionLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** A DTO for encapsulating user's code submission to a problem. */
public record SubmitCodeRequest(@NotNull Long problemId, @NotBlank String code, @NotNull SubmissionLanguage language) {}
