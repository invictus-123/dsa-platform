package com.online.judge.backend.dto.response;

import com.online.judge.backend.dto.ui.ProblemSummaryUi;
import java.util.List;

public record ListProblemsResponse(List<ProblemSummaryUi> problems) {}
