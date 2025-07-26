package com.dsa.platform.backend.dto.ui;

import java.util.List;
import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;

public record ProblemSummaryUi(Long id, String title, ProblemDifficulty difficulty,
        List<ProblemTag> tags) {
}
