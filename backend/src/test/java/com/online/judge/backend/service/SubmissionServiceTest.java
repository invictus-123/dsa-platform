package com.online.judge.backend.service;

import static com.online.judge.backend.factory.SubmissionFactory.createSubmission;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.online.judge.backend.dto.ui.ProblemSummaryUi;
import com.online.judge.backend.dto.ui.SubmissionDetailsUi;
import com.online.judge.backend.dto.ui.SubmissionSummaryUi;
import com.online.judge.backend.dto.ui.UserSummaryUi;
import com.online.judge.backend.exception.SubmissionNotFoundException;
import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.Submission;
import com.online.judge.backend.model.User;
import com.online.judge.backend.repository.SubmissionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {
	private static final int PAGE_SIZE = 20;

	@Mock
	private SubmissionRepository submissionRepository;

	private SubmissionService submissionService;

	@BeforeEach
	void setUp() {
		submissionService = new SubmissionService(submissionRepository, PAGE_SIZE);
	}

	@Test
	void listSubmissions_shouldReturnPaginatedSubmissions() {
		int page = 1;
		List<Submission> submissions = List.of(createSubmissionWithId(1L), createSubmissionWithId(2L));
		Pageable pageable =
				PageRequest.of(page - 1, PAGE_SIZE, Sort.by("submittedAt").descending());
		Page<Submission> submissionPage = new PageImpl<>(submissions, pageable, submissions.size());
		when(submissionRepository.findAll(pageable)).thenReturn(submissionPage);

		List<SubmissionSummaryUi> result = submissionService.listSubmissions(page);

		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	void getSubmissionDetailsById_whenSubmissionExists_shouldReturnDetails() {
		Long submissionId = 10L;
		Submission submission = createSubmissionWithId(submissionId);
		when(submissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
		SubmissionDetailsUi expectedSubmmissionDetails = new SubmissionDetailsUi(
				submission.getId(),
				createProblemSummaryUi(submission.getProblem()),
				createUserSummaryUi(submission.getUser()),
				submission.getStatus(),
				submission.getLanguage(),
				submission.getSubmittedAt(),
				submission.getCode(),
				submission.getExecutionTimeSeconds(),
				submission.getMemoryUsedMb(),
				List.of());

		SubmissionDetailsUi result = submissionService.getSubmissionDetailsById(submissionId);

		assertNotNull(result);
		assertEquals(expectedSubmmissionDetails, result);
	}

	@Test
	void getSubmissionDetailsById_whenSubmissionDoesNotExist_shouldThrowException() {
		Long submissionId = 10L;
		when(submissionRepository.findById(submissionId)).thenReturn(Optional.empty());

		SubmissionNotFoundException exception = assertThrows(SubmissionNotFoundException.class, () -> {
			submissionService.getSubmissionDetailsById(submissionId);
		});

		assertEquals("Submission with ID " + submissionId + " not found", exception.getMessage());
	}

	private Submission createSubmissionWithId(Long submissionId) {
		Submission submission = createSubmission();
		submission.setId(submissionId);
		return submission;
	}

	private ProblemSummaryUi createProblemSummaryUi(Problem problem) {
		return new ProblemSummaryUi(problem.getId(), problem.getTitle(), problem.getDifficulty(), List.of());
	}

	private UserSummaryUi createUserSummaryUi(User user) {
		return new UserSummaryUi(user.getHandle());
	}
}
