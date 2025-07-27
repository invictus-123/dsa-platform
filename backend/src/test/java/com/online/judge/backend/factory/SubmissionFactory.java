package com.online.judge.backend.factory;

import static com.online.judge.backend.factory.ProblemFactory.createProblem;
import static com.online.judge.backend.factory.UserFactory.createUser;

import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.Submission;
import com.online.judge.backend.model.User;
import com.online.judge.backend.model.shared.SubmissionLanguage;
import com.online.judge.backend.model.shared.SubmissionStatus;

public class SubmissionFactory {
	private static final Problem PROBLEM = createProblem();
	private static final User SUBMITTED_BY_USER = createUser();
	private static final SubmissionStatus STATUS = SubmissionStatus.PASSED;
	private static final SubmissionLanguage LANGUAGE = SubmissionLanguage.JAVA;
	private static final String CODE = "public class Solution {}";
	private static final Double EXECUTION_TIME_SECONDS = 1.0;
	private static final Integer MEMORY_USED_MB = 1024;

	public static Submission createSubmission() {
		return createSubmission(
				SUBMITTED_BY_USER, PROBLEM, STATUS, LANGUAGE, CODE, EXECUTION_TIME_SECONDS, MEMORY_USED_MB);
	}

	public static Submission createSubmission(
			User user,
			Problem problem,
			SubmissionStatus status,
			SubmissionLanguage language,
			String code,
			Double executionTimeSeconds,
			Integer memoryUsedMb) {
		Submission submission = new Submission();
		submission.setUser(user);
		submission.setProblem(problem);
		submission.setStatus(status);
		submission.setLanguage(language);
		submission.setCode(code);
		submission.setExecutionTimeSeconds(executionTimeSeconds);
		submission.setMemoryUsedMb(memoryUsedMb);
		return submission;
	}

	private SubmissionFactory() {
		// Prevent instantiation
	}
}
