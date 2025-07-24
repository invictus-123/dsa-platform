package com.dsa.platform.backend.model.shared;

public enum SubmissionStatus {
    WAITING_FOR_EXECUTION,
    RUNNING,
    PASSED,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    COMPILATION_ERROR,
    RUNTIME_ERROR
}
