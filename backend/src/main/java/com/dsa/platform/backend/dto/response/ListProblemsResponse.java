package com.dsa.platform.backend.dto.response;

import java.util.List;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;

public record ListProblemsResponse(List<ProblemSummaryUi> problems) {
}
