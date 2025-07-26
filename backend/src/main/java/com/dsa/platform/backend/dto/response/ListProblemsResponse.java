package com.dsa.platform.backend.dto.response;

import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import java.util.List;

public record ListProblemsResponse(List<ProblemSummaryUi> problems) {}
