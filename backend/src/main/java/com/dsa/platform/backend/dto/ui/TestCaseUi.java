package com.dsa.platform.backend.dto.ui;

public record TestCaseUi(
        String input,
        String expectedOutput,
        String explanation) {
}