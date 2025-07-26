package com.dsa.platform.backend.dto.ui;

import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;
import java.util.List;

public record ProblemSummaryUi(Long id, String title, ProblemDifficulty difficulty, List<ProblemTag> tags) {}
