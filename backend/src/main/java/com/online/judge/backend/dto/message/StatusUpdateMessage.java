package com.online.judge.backend.dto.message;

import com.online.judge.backend.model.shared.SubmissionStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateMessage(@NotNull Long submissionId, @NotNull SubmissionStatus status) {}
