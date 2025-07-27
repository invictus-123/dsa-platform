package com.online.judge.backend.converter;

import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.Tag;
import com.online.judge.backend.model.shared.ProblemTag;

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
