package com.online.judge.backend.dto.message;

import jakarta.validation.constraints.NotNull;

public record ResultNotificationMessage(@NotNull Long submissionId) {}
