package com.online.judge.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	private SubmissionController submissionController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(submissionController).build();
	}

	@Test
	void listSubmissions_whenRequestWithoutPageParam_returnsSubmissionListForFirstPage() throws Exception {
		mockMvc.perform(get("/api/v1/submissions/list")).andExpect(status().isOk());
	}

	@Test
	void listSubmissions_whenRequestWithPageParam_returnsSubmissionSummaryList() throws Exception {
		mockMvc.perform(get("/api/v1/submissions/list").param("page", "1")).andExpect(status().isOk());
	}

	@Test
	void getSubmissionById_whenSubmissionExists_returnsSubmissionDetails() throws Exception {
		Long submissionId = 1L;

		mockMvc.perform(get("/api/v1/submissions/{id}", submissionId)).andExpect(status().isOk());
	}
}
