package com.online.judge.backend.controller;

import com.online.judge.backend.dto.response.GetSubmissionByIdResponse;
import com.online.judge.backend.dto.response.ListSubmissionsResponse;
import com.online.judge.backend.service.SubmissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for handling submission-related requests. */
@RestController
@RequestMapping("/api/v1/submissions")
public class SubmissionController {
	private static final Logger logger = LoggerFactory.getLogger(SubmissionController.class);

	private final SubmissionService submissionService;

	public SubmissionController(SubmissionService submissionService) {
		this.submissionService = submissionService;
	}

	/**
	 * Handles GET requests to fetch a list of all submissions with pagination, sorted by submission
	 * date in descending order.
	 *
	 * @param page The page number to retrieve (default is 1).
	 * @return A ResponseEntity containing a paginated list of submissions.
	 */
	@GetMapping("/list")
	public ResponseEntity<ListSubmissionsResponse> listSubmissions(@RequestParam(defaultValue = "1") int page) {
		logger.info("Received call to fetch all submissions with pagination: page={}", page);

		ListSubmissionsResponse response = new ListSubmissionsResponse(submissionService.listSubmissions(page));
		return ResponseEntity.ok(response);
	}

	/**
	 * Handles GET requests to fetch the details of a specific submission by its ID.
	 *
	 * @param id The ID of the submission to retrieve.
	 * @return A ResponseEntity containing the SubmissionDetailsUi DTO if found, or a 404 Not Found
	 *         status if the submission does not exist.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<GetSubmissionByIdResponse> getSubmissionById(@PathVariable Long id) {
		logger.info("Received call to fetch submission with ID {}", id);

		GetSubmissionByIdResponse response =
				new GetSubmissionByIdResponse(submissionService.getSubmissionDetailsById(id));
		return ResponseEntity.ok(response);
	}

	/**
	 * Handles POST requests to make a submission. Only accessible by authenticated users.
	 *
	 * @param request The request body containing the submission details.
	 * @return A ResponseEntity with the created submission and a 201 Created status. Throws a 401
	 *         error if the user is not authorized to make a submission.
	 */
	@PostMapping
	public ResponseEntity<Void> submit() {
		logger.info("Received request to create a new submission");

		return ResponseEntity.ok(null);
	}
}
