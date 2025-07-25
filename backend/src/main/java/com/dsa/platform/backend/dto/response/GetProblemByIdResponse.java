package com.dsa.platform.backend.dto.response;

import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;

public record GetProblemByIdResponse(
        ProblemDetailsUi problemDetails) {
}