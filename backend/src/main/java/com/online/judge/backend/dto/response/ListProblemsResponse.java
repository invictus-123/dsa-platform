package com.online.judge.backend.dto.response;

import java.util.List;
import com.online.judge.backend.dto.ui.ProblemSummaryUi;

public record ListProblemsResponse(List<ProblemSummaryUi> problems) {}
