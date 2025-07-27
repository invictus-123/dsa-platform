package com.online.judge.backend.service;

import static com.online.judge.backend.converter.ProblemConverter.toProblemDetailsUi;
import static com.online.judge.backend.converter.ProblemConverter.toProblemFromCreateProblemRequest;
import static com.online.judge.backend.converter.ProblemConverter.toProblemSummaryUi;

import com.online.judge.backend.dto.request.CreateProblemRequest;
import com.online.judge.backend.dto.ui.ProblemDetailsUi;
import com.online.judge.backend.dto.ui.ProblemSummaryUi;
import com.online.judge.backend.exception.ProblemNotFoundException;
import com.online.judge.backend.exception.UserNotAuthorizedException;
import com.online.judge.backend.model.Problem;
import com.online.judge.backend.model.Tag;
import com.online.judge.backend.model.TestCase;
import com.online.judge.backend.model.User;
import com.online.judge.backend.model.shared.ProblemTag;
import com.online.judge.backend.model.shared.UserRole;
import com.online.judge.backend.repository.ProblemRepository;
import com.online.judge.backend.util.UserUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing problems. */
@Service
public class ProblemService {
	private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

	private final int pageSize;
	private final ProblemRepository problemRepository;
	private final UserUtil userUtil;

	public ProblemService(
			@Value("${problems.list.page-size:50}") int pageSize,
			ProblemRepository problemRepository,
			UserUtil userUtil) {
		this.pageSize = pageSize;
		this.problemRepository = problemRepository;
		this.userUtil = userUtil;
	}

	/**
	 * Retrieves a paginated list of all problems, sorted by creation date in
	 * descending order.
	 *
	 * @param page
	 *            The page number (1-based).
	 * @return A List<ProblemSummaryUi> containing the paginated list of problems.
	 */
	@Transactional(readOnly = true)
	public List<ProblemSummaryUi> listProblems(int page) {
		logger.info("Fetching all problems for page {}", page);

		Pageable pageable =
				PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending());
		return problemRepository.findAll(pageable).getContent().stream()
				.map(problem -> toProblemSummaryUi(problem, listTags(problem.getTags())))
				.toList();
	}

	/**
	 * Retrieves the details of a problem by its ID.
	 *
	 * @param problemId
	 *            The ID of the problem to retrieve.
	 * @return A ProblemDetailsUi object containing the problem details.
	 * @throws ProblemNotFoundException
	 *             if the problem with the given ID does not exist.
	 */
	@Transactional(readOnly = true)
	public ProblemDetailsUi getProblemDetailsById(Long problemId) {
		logger.info("Fetching details for problem with ID: {}", problemId);

		return problemRepository
				.findById(problemId)
				.map(problem -> {
					logger.info("Problem found: {}", problem);
					return toProblemDetailsUi(
							problem, listTags(problem.getTags()), listSampleTestCases(problem.getTestCases()));
				})
				.orElseThrow(() -> {
					logger.error("Problem with ID {} not found", problemId);
					return new ProblemNotFoundException("Problem with ID " + problemId + " not found");
				});
	}

	/**
	 * Creates a new problem, including its tags and test cases. This method is transactional to
	 * ensure all data is saved atomically.
	 *
	 * @param request
	 *            The DTO record containing all information for the new problem.
	 * @return A DTO representing the newly created problem's details.
	 */
	@Transactional
	public ProblemDetailsUi createProblem(CreateProblemRequest request) {
		logger.info("Creating a new problem with title: {}", request.title());

		User authenticatedUser = userUtil.getCurrentAuthenticatedUser();
		if (!authenticatedUser.getRole().equals(UserRole.ADMIN)) {
			logger.warn("User {} is not authorized to create problems", authenticatedUser.getHandle());
			throw new UserNotAuthorizedException("User is not authorized to create problems.");
		}

		Problem problem = toProblemFromCreateProblemRequest(request);
		problem.setCreatedBy(authenticatedUser);
		Problem savedProblem = problemRepository.save(problem);
		logger.info("Problem created with ID: {} by user: {}", savedProblem.getId(), authenticatedUser.getHandle());
		return toProblemDetailsUi(
				savedProblem, listTags(savedProblem.getTags()), listSampleTestCases(savedProblem.getTestCases()));
	}

	/**
	 * Lists the tags associated with a problem.
	 *
	 * @param tags
	 *            The list of tag entities associated with the problem.
	 * @return A list of ProblemTag objects.
	 */
	private List<ProblemTag> listTags(List<Tag> tags) {
		return tags.stream().map(Tag::getTagName).toList();
	}

	/**
	 * Lists the sample test cases associated with a problem.
	 *
	 * @param testCases
	 *            The list of test case entities associated with the problem.
	 * @return A list of sample TestCase objects.
	 */
	private List<TestCase> listSampleTestCases(List<TestCase> testCases) {
		return testCases.stream().filter(TestCase::getIsSample).toList();
	}
}
