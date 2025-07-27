package com.online.judge.backend.factory;

import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.Tag;
import com.online.judge.backend.model.shared.ProblemTag;

public class TagFactory {
	public static Tag createTag(Problem problem) {
		return createTag(problem, ProblemTag.ARRAY);
	}

	public static Tag createTag(Problem problem, ProblemTag problemTag) {
		Tag tag = new Tag();
		tag.setProblem(problem);
		tag.setTagName(problemTag);
		return tag;
	}

	private TagFactory() {
		// Prevent instantiation
	}
}
