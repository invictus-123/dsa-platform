package com.online.judge.backend.service;

import static com.online.judge.backend.converter.SubmissionConverter.toSubmissionDetailsUi;

import com.online.judge.backend.converter.SubmissionConverter;
import com.online.judge.backend.dto.ui.SubmissionDetailsUi;
import com.online.judge.backend.dto.ui.SubmissionSummaryUi;
import com.online.judge.backend.exception.SubmissionNotFoundException;
import com.online.judge.backend.repository.SubmissionRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

/** Service class for managing submissions. */
public class SubmissionService {
	private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

	private final SubmissionRepository submissionRepository;
	private final int pageSize;

	public SubmissionService(
			SubmissionRepository submissionRepository, @Value("${submissions.list.page-size:50}") int pageSize) {
		this.submissionRepository = submissionRepository;
		this.pageSize = pageSize;
	}

	/**
	 * Retrieves a paginated list of all submissions, sorted by the submission time in descending
	 * order.
	 *
	 * @param page
	 *            The page number (1-based).
	 * @return A List<SubmissionSummaryUi> containing the paginated list of submissions.
	 */
	@Transactional(readOnly = true)
	public List<SubmissionSummaryUi> listSubmissions(int page) {
		logger.info("Fetching all submissions for page {}", page);

		Pageable pageable =
				PageRequest.of(page - 1, pageSize, Sort.by("submittedAt").descending());
		return submissionRepository.findAll(pageable).getContent().stream()
				.map(SubmissionConverter::toSubmissionSummaryUi)
				.toList();
	}

	/**
	 * Retrieves the details of a submission by its ID.
	 *
	 * @param submissionId
	 *            The ID of the submission to retrieve.
	 * @return A SubmissionDetailsUi object containing the submission details.
	 * @throws SubmissionNotFoundException if the submission with the given ID does not exist.
	 */
	@Transactional(readOnly = true)
	public SubmissionDetailsUi getSubmissionDetailsById(Long submissionId) {
		logger.info("Fetching details for submission with ID: {}", submissionId);

		return submissionRepository
				.findById(submissionId)
				.map(submission -> {
					logger.info("Submission found: {}", submission);
					return toSubmissionDetailsUi(submission);
				})
				.orElseThrow(() -> {
					logger.error("Submission with ID {} not found", submissionId);
					return new SubmissionNotFoundException("Submission with ID " + submissionId + " not found");
				});
	}
}
