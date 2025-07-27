package com.online.judge.backend.service;

import static com.online.judge.backend.factory.ProblemFactory.createProblem;
import static com.online.judge.backend.factory.TagFactory.createTag;
import static com.online.judge.backend.factory.TestCaseFactory.createTestCase;
import static com.online.judge.backend.factory.UiFactory.createProblemDetailsUi;
import static com.online.judge.backend.factory.UiFactory.createTestCaseUi;
import static com.online.judge.backend.factory.UserFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.online.judge.backend.dto.request.CreateProblemRequest;
import com.online.judge.backend.dto.request.CreateTestCaseRequest;
import com.online.judge.backend.dto.ui.ProblemDetailsUi;
import com.online.judge.backend.dto.ui.ProblemSummaryUi;
import com.online.judge.backend.exception.ProblemNotFoundException;
import com.online.judge.backend.exception.UserNotAuthorizedException;
import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.TestCase;
import com.online.judge.backend.model.User;
import com.online.judge.backend.model.shared.ProblemDifficulty;
import com.online.judge.backend.model.shared.ProblemTag;
import com.online.judge.backend.model.shared.UserRole;
import com.online.judge.backend.repository.ProblemRepository;
import com.online.judge.backend.util.UserUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {
	private static final int PAGE_SIZE = 20;
	private static final String PROBLEM_TITLE = "New Problem";
	private static final String PROBLEM_STATEMENT = "Statement";
	private static final double TIME_LIMIT = 1.0;
	private static final int MEMORY_LIMIT = 256;
	private static final ProblemDifficulty PROBLEM_DIFFICULTY = ProblemDifficulty.EASY;
	private static final List<ProblemTag> PROBLEM_TAGS = List.of(ProblemTag.ARRAY, ProblemTag.GREEDY);

	@Mock
	private ProblemRepository problemRepository;

	@Mock
	private UserUtil userUtil;

	private ProblemService problemService;

	@BeforeEach
	void setUp() {
		problemService = new ProblemService(problemRepository, userUtil, PAGE_SIZE);
	}

	@Test
	void listProblems_whenProblemsExist_returnsProblemSummaryList() {
		int page = 5;
		List<Problem> problems = List.of(createProblem(), createProblem());
		Pageable pageable =
				PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdAt").descending());
		Page<Problem> problemPage = new PageImpl<>(problems, pageable, problems.size());
		when(problemRepository.findAll(any(Pageable.class))).thenReturn(problemPage);

		List<ProblemSummaryUi> result = problemService.listProblems(page);

		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	void listProblems_whenNoProblemsExist_returnsEmptyList() {
		int page = 1;
		Pageable pageable =
				PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdAt").descending());
		when(problemRepository.findAll(pageable)).thenReturn(Page.empty());

		List<ProblemSummaryUi> result = problemService.listProblems(page);

		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(problemRepository).findAll(any(Pageable.class));
	}

	@Test
	void getProblemDetailsById_whenProblemExists_returnsProblemDetails() {
		Long problemId = 1L;
		Problem mockProblem = createProblem();
		mockProblem.setId(problemId);
		TestCase sampleTestCase = createTestCase(mockProblem, true);
		TestCase hiddenTestCase = createTestCase(mockProblem, false);
		mockProblem.setTestCases(List.of(sampleTestCase, hiddenTestCase));
		when(problemRepository.findById(problemId)).thenReturn(Optional.of(mockProblem));
		ProblemDetailsUi expectedProblemDetails = new ProblemDetailsUi(
				mockProblem.getId(),
				mockProblem.getTitle(),
				mockProblem.getStatement(),
				mockProblem.getTimeLimitSecond(),
				mockProblem.getMemoryLimitMb(),
				mockProblem.getDifficulty(),
				List.of(ProblemTag.ARRAY, ProblemTag.GREEDY),
				List.of(createTestCaseUi(sampleTestCase)));

		ProblemDetailsUi result = problemService.getProblemDetailsById(problemId);

		assertNotNull(result);
		assertEquals(expectedProblemDetails, result);
	}

	@Test
	void getProblemDetailsById_whenProblemDoesNotExist_throwsProblemNotFoundException() {
		Long problemId = 99L;
		when(problemRepository.findById(problemId)).thenReturn(Optional.empty());

		ProblemNotFoundException exception =
				assertThrows(ProblemNotFoundException.class, () -> problemService.getProblemDetailsById(problemId));

		assertEquals("Problem with ID " + problemId + " not found", exception.getMessage());
	}

	@Test
	void createProblem_whenUserIsAdmin_createsAndReturnsProblem() {
		User adminUser = createUser("admin", UserRole.ADMIN);
		when(userUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);
		CreateProblemRequest request = new CreateProblemRequest(
				PROBLEM_TITLE,
				PROBLEM_STATEMENT,
				PROBLEM_DIFFICULTY,
				TIME_LIMIT,
				MEMORY_LIMIT,
				PROBLEM_TAGS,
				List.of(new CreateTestCaseRequest(
						"Test1 Input", "Test1 Output", /*isSample=*/ true, "Test1 Explanation")));
		Problem problem = createProblemFromRequest(request);
		problem.setCreatedBy(adminUser);
		when(problemRepository.save(any(Problem.class))).thenReturn(problem);
		ProblemDetailsUi expectedProblemDetails = createProblemDetailsUi(problem);

		ProblemDetailsUi result = problemService.createProblem(request);

		ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);
		verify(problemRepository).save(problemCaptor.capture());
		Problem capturedProblem = problemCaptor.getValue();
		assertEquals(problem.getTitle(), capturedProblem.getTitle());
		assertEquals(problem.getStatement(), capturedProblem.getStatement());
		assertEquals(problem.getDifficulty(), capturedProblem.getDifficulty());
		assertEquals(problem.getTimeLimitSecond(), capturedProblem.getTimeLimitSecond());
		assertEquals(problem.getMemoryLimitMb(), capturedProblem.getMemoryLimitMb());
		assertEquals(problem.getTags().size(), capturedProblem.getTags().size());
		assertEquals(
				problem.getTestCases().size(), capturedProblem.getTestCases().size());
		assertEquals(expectedProblemDetails, result);
	}

	@Test
	void createProblem_whenUserIsNotAdmin_throwsUserNotAuthorizedException() {
		User regularUser = createUser();
		when(userUtil.getCurrentAuthenticatedUser()).thenReturn(regularUser);

		CreateProblemRequest request = new CreateProblemRequest(
				PROBLEM_TITLE,
				PROBLEM_STATEMENT,
				PROBLEM_DIFFICULTY,
				TIME_LIMIT,
				MEMORY_LIMIT,
				PROBLEM_TAGS,
				List.of(new CreateTestCaseRequest(
						"Input 1", "Output 1", /*isSample=*/ false, /* explanation= */ null)));

		assertThrows(
				UserNotAuthorizedException.class,
				() -> problemService.createProblem(request),
				"User is not authorized to create problems.");
		verifyNoInteractions(problemRepository);
	}

	private Problem createProblemFromRequest(CreateProblemRequest request) {
		Problem problem = new Problem();
		problem.setTitle(request.title());
		problem.setStatement(request.statement());
		problem.setDifficulty(request.difficulty());
		problem.setTimeLimitSecond(request.timeLimit());
		problem.setMemoryLimitMb(request.memoryLimit());
		problem.setTags(
				request.tags().stream().map(tag -> createTag(problem, tag)).toList());
		problem.setTestCases(request.testCases().stream()
				.map(testCaseRequest -> createTestCase(
						problem,
						testCaseRequest.isSample(),
						testCaseRequest.input(),
						testCaseRequest.expectedOutput(),
						testCaseRequest.explanation()))
				.toList());
		return problem;
	}
}
