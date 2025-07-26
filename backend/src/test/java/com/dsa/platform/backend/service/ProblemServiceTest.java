package com.dsa.platform.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import com.dsa.platform.backend.dto.ui.TestCaseUi;
import com.dsa.platform.backend.exception.ProblemNotFoundException;
import com.dsa.platform.backend.model.Problem;
import com.dsa.platform.backend.model.Tag;
import com.dsa.platform.backend.model.TestCase;
import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;
import com.dsa.platform.backend.repository.ProblemRepository;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {
    private static final int PAGE_SIZE = 50;

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemService problemService;

    @Test
    void listProblems_whenProblemsExist_returnsProblemSummaryList() {
        int page = 5;
        Problem mockProblem = new Problem();
        mockProblem.setId(1L);
        mockProblem.setTitle("Two Sum");
        mockProblem.setDifficulty(ProblemDifficulty.EASY);
        List<Problem> problems = List.of(mockProblem, mockProblem);
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<Problem> problemPage = new PageImpl<>(problems, pageable, problems.size());
        when(problemRepository.findAll(any(Pageable.class))).thenReturn(problemPage);

        List<ProblemSummaryUi> result = problemService.listProblems(page);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void listProblems_whenNoProblemsExist_returnsEmptyList() {
        int page = 1;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdAt").descending());
        when(problemRepository.findAll(pageable)).thenReturn(Page.empty());

        List<ProblemSummaryUi> result = problemService.listProblems(page);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(problemRepository).findAll(any(Pageable.class));
    }

    @Test
    void getProblemDetailsById_whenProblemExists_shouldReturnProblemDetails() {
        Long problemId = 1L;
        Problem mockProblem = new Problem();
        mockProblem.setId(problemId);
        mockProblem.setTitle("Two Sum");
        mockProblem.setDifficulty(ProblemDifficulty.EASY);
        Tag tag1 = createTagEntity(mockProblem, ProblemTag.ARRAY);
        Tag tag2 = createTagEntity(mockProblem, ProblemTag.GREEDY);
        mockProblem.setTags(List.of(tag1, tag2));
        TestCase sampleTestCase =
                createTestCaseEntity(mockProblem, "Input 1", "Output 1", "Explanation 1", true);
        TestCase hiddenTestCase = createTestCaseEntity(mockProblem, "Hidden Input 1",
                "Hidden Output 1", "Hidden Explanation 1", false);
        mockProblem.setTestCases(List.of(sampleTestCase, hiddenTestCase));
        when(problemRepository.findById(problemId)).thenReturn(Optional.of(mockProblem));
        ProblemDetailsUi expectedProblemDetails = new ProblemDetailsUi(mockProblem.getId(),
                mockProblem.getTitle(), mockProblem.getStatement(),
                mockProblem.getTimeLimitSecond(), mockProblem.getMemoryLimitMb(),
                mockProblem.getDifficulty(), List.of(ProblemTag.ARRAY, ProblemTag.GREEDY),
                List.of(new TestCaseUi(sampleTestCase.getInput(), sampleTestCase.getOutput(),
                        sampleTestCase.getExplanation())));

        ProblemDetailsUi result = problemService.getProblemDetailsById(problemId);

        assertNotNull(result);
        assertEquals(expectedProblemDetails, result);
    }

    @Test
    void getProblemDetailsById_whenProblemDoesNotExist_shouldThrowProblemNotFoundException() {
        Long problemId = 99L;
        when(problemRepository.findById(problemId)).thenReturn(Optional.empty());

        ProblemNotFoundException exception = assertThrows(ProblemNotFoundException.class,
                () -> problemService.getProblemDetailsById(problemId));

        assertEquals("Problem with ID " + problemId + " not found", exception.getMessage());
    }

    private Tag createTagEntity(Problem mockProblem, ProblemTag tag) {
        Tag newTag = new Tag();
        newTag.setTagName(tag);
        newTag.setProblem(mockProblem);
        return newTag;
    }

    private TestCase createTestCaseEntity(Problem mockProblem, String input, String output,
            String explanation, boolean isSample) {
        TestCase testCase = new TestCase();
        testCase.setInput(input);
        testCase.setOutput(output);
        testCase.setExplanation(explanation);
        testCase.setIsSample(isSample);
        testCase.setProblem(mockProblem);
        return testCase;
    }
}
