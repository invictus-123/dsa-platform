package com.online.judge.backend.dto.ui;

import java.util.List;
import com.online.judge.backend.model.shared.ProblemDifficulty;
import com.online.judge.backend.model.shared.ProblemTag;

public record ProblemSummaryUi(Long id, String title, ProblemDifficulty difficulty, List<ProblemTag> tags) {}
