// src/main/java/com/dsa/platform/backend/controller/ProblemController.java
package com.dsa.platform.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dsa.platform.backend.dto.response.GetProblemByIdResponse;
import com.dsa.platform.backend.dto.response.ListProblemsResponse;
import com.dsa.platform.backend.service.ProblemService;

/**
 * REST controller for handling problem-related requests.
 */
@RestController
@RequestMapping("/api/problem")
public class ProblemController {
    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    /**
     * Handles GET requests to fetch a list of all problems with pagination, sorted by creation date
     * in descending order.
     *
     * @param page The page number to retrieve (default is 1).
     * @return A ResponseEntity containing a paginated list of problems.
     */
    @GetMapping("/list")
    public ResponseEntity<ListProblemsResponse> listProblems(
            @RequestParam(defaultValue = "1") int page) {
        logger.info("Received call to fetch all problems with pagination: page={}", page);

        ListProblemsResponse response = new ListProblemsResponse(problemService.listProblems(page));
        return ResponseEntity.ok(response);
    }

    /**
     * Handles GET requests to fetch the details of a specific problem by its ID.
     *
     * @param id The ID of the problem to retrieve.
     * @return A ResponseEntity containing the ProblemDetailsUi DTO if found, or a 404 Not Found
     *         status if the problem does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GetProblemByIdResponse> getProblemById(@PathVariable Long id) {
        logger.info("Received call to fetch problem with ID {}", id);

        GetProblemByIdResponse response =
                new GetProblemByIdResponse(problemService.getProblemDetailsById(id));
        return ResponseEntity.ok(response);
    }
}
