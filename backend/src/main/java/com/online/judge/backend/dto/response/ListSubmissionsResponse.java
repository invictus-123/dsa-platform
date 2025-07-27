package com.online.judge.backend.dto.response;

import com.online.judge.backend.dto.ui.SubmissionSummaryUi;
import java.util.List;

public record ListSubmissionsResponse(List<SubmissionSummaryUi> submissions) {}
