package com.dsa.platform.backend.service;

import static com.dsa.platform.backend.converter.ProblemConverter.toProblemDetailsUi;
import static com.dsa.platform.backend.converter.ProblemConverter.toProblemFromCreateProblemRequest;
import static com.dsa.platform.backend.converter.ProblemConverter.toProblemSummaryUi;

import com.dsa.platform.backend.dto.request.CreateProblemRequest;
import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import com.dsa.platform.backend.exception.ProblemNotFoundException;
import com.dsa.platform.backend.exception.UserNotAuthorizedException;
import com.dsa.platform.backend.model.Problem;
import com.dsa.platform.backend.model.Tag;
import com.dsa.platform.backend.model.TestCase;
import com.dsa.platform.backend.model.User;
import com.dsa.platform.backend.model.shared.ProblemTag;
import com.dsa.platform.backend.model.shared.UserRole;
import com.dsa.platform.backend.repository.ProblemRepository;
import com.dsa.platform.backend.util.UserUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing problems. */
@Service
public class ProblemService {
	private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);
	private static final int PAGE_SIZE = 50; // Default page size for pagination

	private final ProblemRepository problemRepository;
	private final UserUtil userUtil;

	public ProblemService(ProblemRepository problemRepository, UserUtil userUtil) {
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
				PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdAt").descending());
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
