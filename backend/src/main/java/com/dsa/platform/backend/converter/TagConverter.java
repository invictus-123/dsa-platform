package com.dsa.platform.backend.converter;

import com.dsa.platform.backend.model.Problem;
import com.dsa.platform.backend.model.Tag;
import com.dsa.platform.backend.model.shared.ProblemTag;

public class TagConverter {

	public static Tag toTagFromProblemTag(Problem problem, ProblemTag problemTag) {
		Tag newTag = new Tag();
		newTag.setTagName(problemTag);
		newTag.setProblem(problem);
		return newTag;
	}

	private TagConverter() {
		// Prevent instantiation
	}
}
