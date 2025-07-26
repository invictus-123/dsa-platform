package com.dsa.platform.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
import com.dsa.platform.backend.dto.response.GetProblemByIdResponse;
import com.dsa.platform.backend.dto.response.ListProblemsResponse;
import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import com.dsa.platform.backend.dto.ui.TestCaseUi;
import com.dsa.platform.backend.exception.ProblemNotFoundException;
import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;
import com.dsa.platform.backend.service.ProblemService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProblemControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProblemService problemService;

    @InjectMocks
    private ProblemController problemController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(problemController).build();
    }

    @Test
    void listProblems_whenRequestWithoutPageParam_returnsProblemSummaryListForFirstPage()
            throws Exception {
        int page = 1;
        List<Long> problemIds = List.of(1L, 2L);
        List<ProblemSummaryUi> problemSummaries =
                problemIds.stream().map(this::createProblemSummaryUi).toList();
        when(problemService.listProblems(page)).thenReturn(problemSummaries);
        ListProblemsResponse expectedResponse = new ListProblemsResponse(problemSummaries);

        ResponseEntity<ListProblemsResponse> response = problemController.listProblems(page);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());

        mockMvc.perform(get("/api/v1/problems/list")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void listProblems_whenRequestWithPageParam_returnsProblemSummaryList() throws Exception {
        int page = 5;
        List<Long> problemIds = List.of(1L, 2L);
        List<ProblemSummaryUi> problemSummaries =
                problemIds.stream().map(this::createProblemSummaryUi).toList();
        when(problemService.listProblems(page)).thenReturn(problemSummaries);
        ListProblemsResponse expectedResponse = new ListProblemsResponse(problemSummaries);

        ResponseEntity<ListProblemsResponse> response = problemController.listProblems(page);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());

        mockMvc.perform(get("/api/v1/problems/list").param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void getProblemById_whenProblemExists_returnsProblemDetails() throws Exception {
        Long problemId = 1L;
        ProblemDetailsUi problemDetailsUi = createProblemDetailsUi(problemId);
        GetProblemByIdResponse expectedResponse = new GetProblemByIdResponse(problemDetailsUi);
        when(problemService.getProblemDetailsById(problemId)).thenReturn(problemDetailsUi);

        ResponseEntity<GetProblemByIdResponse> response =
                problemController.getProblemById(problemId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());

        mockMvc.perform(get("/api/v1/problems/{id}", problemId)).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    void getProblemById_whenProblemDoesNotExist_throwsNotFoundException() throws Exception {
        Long problemId = 2L;
        when(problemService.getProblemDetailsById(problemId))
                .thenThrow(new ProblemNotFoundException("Problem not found with id: " + problemId));

        mockMvc.perform(get("/api/v1/problems/{id}", problemId)).andExpect(status().isNotFound());
    }

    private ProblemSummaryUi createProblemSummaryUi(Long id) {
        return new ProblemSummaryUi(id, "Title", ProblemDifficulty.EASY, List.of(ProblemTag.ARRAY));
    }

    private ProblemDetailsUi createProblemDetailsUi(Long id) {
        return new ProblemDetailsUi(id, "Title", "Statement", /* timeLimitInSecond= */2.0,
                /* memoryLimitInMb= */256, ProblemDifficulty.EASY, List.of(ProblemTag.ARRAY),
                List.of(new TestCaseUi("Sample input", "Sample output", "Explanation")));
    }
}
