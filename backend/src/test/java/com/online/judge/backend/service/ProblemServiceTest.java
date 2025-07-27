package com.online.judge.backend.service;

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
import com.online.judge.backend.dto.ui.TestCaseUi;
import com.online.judge.backend.exception.ProblemNotFoundException;
import com.online.judge.backend.exception.UserNotAuthorizedException;
import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.Tag;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

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

	@InjectMocks
	private ProblemService problemService;

	@BeforeEach
	void setUp() {
		// Use ReflectionTestUtils to set the private 'pageSize' field
		// This simulates the behavior of @Value for this unit test
		ReflectionTestUtils.setField(problemService, "pageSize", PAGE_SIZE);
	}

	@Test
	void listProblems_whenProblemsExist_returnsProblemSummaryList() {
		int page = 5;
		Problem mockProblem = new Problem();
		mockProblem.setId(1L);
		mockProblem.setTitle(PROBLEM_TITLE);
		mockProblem.setDifficulty(PROBLEM_DIFFICULTY);
		List<Problem> problems = List.of(mockProblem, mockProblem);
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
		Problem mockProblem = new Problem();
		mockProblem.setId(problemId);
		mockProblem.setTitle(PROBLEM_TITLE);
		mockProblem.setDifficulty(PROBLEM_DIFFICULTY);
		Tag tag1 = createTagEntity(mockProblem, ProblemTag.ARRAY);
		Tag tag2 = createTagEntity(mockProblem, ProblemTag.GREEDY);
		mockProblem.setTags(List.of(tag1, tag2));
		TestCase sampleTestCase = createTestCaseEntity(mockProblem, "Input 1", "Output 1", "Explanation 1", true);
		TestCase hiddenTestCase =
				createTestCaseEntity(mockProblem, "Hidden Input 1", "Hidden Output 1", "Hidden Explanation 1", false);
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
		User adminUser = createUserWithRole(UserRole.ADMIN);
		when(userUtil.getCurrentAuthenticatedUser()).thenReturn(adminUser);
		CreateProblemRequest request = new CreateProblemRequest(
				PROBLEM_TITLE,
				PROBLEM_STATEMENT,
				PROBLEM_DIFFICULTY,
				TIME_LIMIT,
				MEMORY_LIMIT,
				PROBLEM_TAGS,
				List.of(new CreateTestCaseRequest(
						"Input 1", "Output 1", /*isSample=*/ false, /* explanation= */ null)));
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
		User regularUser = new User();
		regularUser.setHandle("user");
		regularUser.setRole(UserRole.USER);
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
		problem.setTags(request.tags().stream()
				.map(tag -> createTagEntity(problem, tag))
				.toList());
		problem.setTestCases(request.testCases().stream()
				.map(testCase -> createTestCaseEntity(
						problem,
						testCase.input(),
						testCase.expectedOutput(),
						testCase.explanation(),
						testCase.isSample()))
				.toList());
		return problem;
	}

	private ProblemDetailsUi createProblemDetailsUi(Problem problem) {
		return new ProblemDetailsUi(
				problem.getId(),
				problem.getTitle(),
				problem.getStatement(),
				problem.getTimeLimitSecond(),
				problem.getMemoryLimitMb(),
				problem.getDifficulty(),
				problem.getTags().stream().map(Tag::getTagName).toList(),
				problem.getTestCases().stream()
						.filter(TestCase::getIsSample)
						.map(this::createTestCaseUi)
						.toList());
	}

	private Tag createTagEntity(Problem mockProblem, ProblemTag tag) {
		Tag newTag = new Tag();
		newTag.setTagName(tag);
		newTag.setProblem(mockProblem);
		return newTag;
	}

	private TestCase createTestCaseEntity(
			Problem mockProblem, String input, String output, String explanation, boolean isSample) {
		TestCase testCase = new TestCase();
		testCase.setInput(input);
		testCase.setOutput(output);
		testCase.setExplanation(explanation);
		testCase.setIsSample(isSample);
		testCase.setProblem(mockProblem);
		return testCase;
	}

	private TestCaseUi createTestCaseUi(TestCase testCase) {
		return new TestCaseUi(testCase.getInput(), testCase.getOutput(), testCase.getExplanation());
	}

	private User createUserWithRole(UserRole role) {
		User user = new User();
		user.setHandle("testUser");
		user.setRole(role);
		return user;
	}
}
