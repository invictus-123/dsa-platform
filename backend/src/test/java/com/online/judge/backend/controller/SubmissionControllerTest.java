package com.online.judge.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.online.judge.backend.dto.response.GetSubmissionByIdResponse;
import com.online.judge.backend.dto.response.ListSubmissionsResponse;
import com.online.judge.backend.dto.ui.ProblemSummaryUi;
import com.online.judge.backend.dto.ui.SubmissionDetailsUi;
import com.online.judge.backend.dto.ui.SubmissionSummaryUi;
import com.online.judge.backend.dto.ui.UserSummaryUi;
import com.online.judge.backend.exception.SubmissionNotFoundException;
import com.online.judge.backend.model.shared.ProblemDifficulty;
import com.online.judge.backend.model.shared.SubmissionLanguage;
import com.online.judge.backend.model.shared.SubmissionStatus;
import com.online.judge.backend.service.SubmissionService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper;

	@Mock
	private SubmissionService submissionService;

	@InjectMocks
	private SubmissionController submissionController;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());

		mockMvc = MockMvcBuilders.standaloneSetup(submissionController).build();
	}

	@Test
	void listSubmissions_whenRequestWithoutPageParam_returnsSubmissionListForFirstPage() throws Exception {
		int page = 1;
		List<Long> submissionIds = List.of(1L, 2L);
		List<SubmissionSummaryUi> submissionSummaries =
				submissionIds.stream().map(this::createSubmissionSummaryUi).toList();
		when(submissionService.listSubmissions(page)).thenReturn(submissionSummaries);
		ListSubmissionsResponse expectedResponse = new ListSubmissionsResponse(submissionSummaries);

		ResponseEntity<ListSubmissionsResponse> response = submissionController.listSubmissions(page);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(expectedResponse, response.getBody());

		mockMvc.perform(get("/api/v1/submissions/list"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
	}

	@Test
	void listSubmissions_whenRequestWithPageParam_returnsSubmissionSummaryList() throws Exception {
		int page = 5;
		List<Long> submissionIds = List.of(1L, 2L);
		List<SubmissionSummaryUi> submissionSummaries =
				submissionIds.stream().map(this::createSubmissionSummaryUi).toList();
		when(submissionService.listSubmissions(page)).thenReturn(submissionSummaries);
		ListSubmissionsResponse expectedResponse = new ListSubmissionsResponse(submissionSummaries);

		ResponseEntity<ListSubmissionsResponse> response = submissionController.listSubmissions(page);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(expectedResponse, response.getBody());

		mockMvc.perform(get("/api/v1/submissions/list").param("page", String.valueOf(page)))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
	}

	@Test
	void getSubmissionById_whenSubmissionExists_returnsSubmissionDetails() throws Exception {
		Long submissionId = 1L;
		SubmissionDetailsUi submissionDetailsUi = createSubmissionDetailsUi(submissionId);
		GetSubmissionByIdResponse expectedResponse = new GetSubmissionByIdResponse(submissionDetailsUi);
		when(submissionService.getSubmissionDetailsById(submissionId)).thenReturn(submissionDetailsUi);

		ResponseEntity<GetSubmissionByIdResponse> response = submissionController.getSubmissionById(submissionId);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(expectedResponse, response.getBody());

		mockMvc.perform(get("/api/v1/submissions/{id}", submissionId))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
	}

	@Test
	void getSubmissionById_whenSubmissionDoesNotExist_throwsNotFoundException() throws Exception {
		Long submissionId = 2L;
		when(submissionService.getSubmissionDetailsById(submissionId))
				.thenThrow(new SubmissionNotFoundException("Submission not found with id: " + submissionId));

		mockMvc.perform(get("/api/v1/submissions/{id}", submissionId)).andExpect(status().isNotFound());
	}

	private ProblemSummaryUi createProblemSummaryUi() {
		return new ProblemSummaryUi(1L, "Two Sum", ProblemDifficulty.EASY, List.of());
	}

	private UserSummaryUi createUserSummaryUi() {
		return new UserSummaryUi("testuser");
	}

	private SubmissionSummaryUi createSubmissionSummaryUi(Long id) {
		return new SubmissionSummaryUi(
				id,
				createProblemSummaryUi(),
				createUserSummaryUi(),
				SubmissionStatus.PASSED,
				SubmissionLanguage.JAVA,
				Instant.now());
	}

	private SubmissionDetailsUi createSubmissionDetailsUi(Long id) {
		return new SubmissionDetailsUi(
				id,
				createProblemSummaryUi(),
				createUserSummaryUi(),
				SubmissionStatus.PASSED,
				SubmissionLanguage.JAVA,
				Instant.now(),
				"public class Solution {}",
				0.123,
				128,
				List.of());
	}
}
