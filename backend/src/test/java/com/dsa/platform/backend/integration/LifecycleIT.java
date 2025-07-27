package com.dsa.platform.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.dsa.platform.backend.dto.request.CreateProblemRequest;
import com.dsa.platform.backend.dto.request.CreateTestCaseRequest;
import com.dsa.platform.backend.dto.request.LoginRequest;
import com.dsa.platform.backend.dto.request.RegisterRequest;
import com.dsa.platform.backend.dto.response.AuthResponse;
import com.dsa.platform.backend.dto.response.CreateProblemResponse;
import com.dsa.platform.backend.dto.response.GetProblemByIdResponse;
import com.dsa.platform.backend.dto.response.ListProblemsResponse;
import com.dsa.platform.backend.dto.ui.ProblemDetailsUi;
import com.dsa.platform.backend.dto.ui.ProblemSummaryUi;
import com.dsa.platform.backend.model.shared.ProblemDifficulty;
import com.dsa.platform.backend.model.shared.ProblemTag;
import com.dsa.platform.backend.model.shared.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test for user and problem-related API flows. Uses Testcontainers to spin up a real
 * PostgreSQL database for testing. The tests are ordered because they represent a logical flow of
 * actions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
class LifecycleIT {
	private static final String BASE_URL = "/api/v1";
	private static final String PROBLEM_TITLE = "Title";
	private static final String PROBLEM_STATEMENT = "Statement";
	private static final int PROBLEM_COUNT = 60;
	private static final ProblemDifficulty PROBLEM_DIFFICULTY = ProblemDifficulty.EASY;
	private static final Double PROBLEM_TIME_LIMIT = 1.0;
	private static final Integer PROBLEM_MEMORY_LIMIT = 256;
	private static final List<ProblemTag> PROBLEM_TAGS = List.of(ProblemTag.DP, ProblemTag.GRAPH);
	private static final List<CreateTestCaseRequest> PROBLEM_TEST_CASES = List.of(
			new CreateTestCaseRequest("Input 1", "Output 1", true, "Explanation"),
			new CreateTestCaseRequest("Input 2", "Output 2", false, null));
	private static final CreateProblemRequest CREATE_PROBLEM_REQUEST = new CreateProblemRequest(
			PROBLEM_TITLE,
			PROBLEM_STATEMENT,
			PROBLEM_DIFFICULTY,
			PROBLEM_TIME_LIMIT,
			PROBLEM_MEMORY_LIMIT,
			PROBLEM_TAGS,
			PROBLEM_TEST_CASES);

	@Container
	private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

	@DynamicPropertySource
	private static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
	}

	@Autowired
	private TestRestTemplate restTemplate;

	private static String userToken;
	private static String adminToken;
	private static Long problemId;

	@Test
	@Order(1)
	void userAndAdminRegistrationAndLogin() {
		// 1. Register a standard USER
		String userHandle = ("testuser-" + UUID.randomUUID()).substring(0, 20);
		String userEmail = "testuser-" + UUID.randomUUID() + "@example.com";
		String password = "password123";
		RegisterRequest userRegisterRequest =
				new RegisterRequest(userHandle, userEmail, password, "First", "Last", UserRole.USER);
		ResponseEntity<String> userRegisterResponse =
				restTemplate.postForEntity(BASE_URL + "/auth/register", userRegisterRequest, String.class);
		assertThat(userRegisterResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// 2. Login as the registered USER and retrieve the JWT
		LoginRequest userLoginRequest = new LoginRequest(userHandle, password);
		ResponseEntity<AuthResponse> userLoginResponse =
				restTemplate.postForEntity(BASE_URL + "/auth/login", userLoginRequest, AuthResponse.class);
		assertThat(userLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(userLoginResponse.getBody()).isNotNull();
		userToken = userLoginResponse.getBody().token();
		assertThat(userToken).isNotBlank();

		// 3. Register an ADMIN
		String adminHandle = ("testadmin-" + UUID.randomUUID()).substring(0, 20);
		String adminEmail = "testadmin-" + UUID.randomUUID() + "@example.com";
		RegisterRequest adminRegisterRequest =
				new RegisterRequest(adminHandle, adminEmail, password, "Admin", "User", UserRole.ADMIN);
		ResponseEntity<String> adminRegisterResponse =
				restTemplate.postForEntity(BASE_URL + "/auth/register", adminRegisterRequest, String.class);
		assertThat(adminRegisterResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// 4. Login as the registered ADMIN and retrieve the JWT
		LoginRequest adminLoginRequest = new LoginRequest(adminHandle, password);
		ResponseEntity<AuthResponse> adminLoginResponse =
				restTemplate.postForEntity(BASE_URL + "/auth/login", adminLoginRequest, AuthResponse.class);
		assertThat(adminLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(adminLoginResponse.getBody()).isNotNull();
		adminToken = adminLoginResponse.getBody().token();
		assertThat(adminToken).isNotBlank();
	}

	@Test
	@Order(2)
	void onlyAdminCanCreateProblem() {
		assertThat(userToken).as("User token should not be null").isNotNull();

		HttpHeaders userHeaders = new HttpHeaders();
		userHeaders.setBearerAuth(userToken);
		HttpEntity<CreateProblemRequest> userCreateProblemRequest =
				new HttpEntity<>(CREATE_PROBLEM_REQUEST, userHeaders);
		ResponseEntity<CreateProblemResponse> userCreateProblemResponse = restTemplate.postForEntity(
				BASE_URL + "/problems", userCreateProblemRequest, CreateProblemResponse.class);

		assertThat(userCreateProblemResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

		assertThat(adminToken).as("Admin token should not be null").isNotNull();

		HttpHeaders adminHeaders = new HttpHeaders();
		adminHeaders.setBearerAuth(adminToken);
		HttpEntity<CreateProblemRequest> adminCreateProblemRequest =
				new HttpEntity<>(CREATE_PROBLEM_REQUEST, adminHeaders);
		ResponseEntity<CreateProblemResponse> adminCreateProblemResponse = restTemplate.postForEntity(
				BASE_URL + "/problems", adminCreateProblemRequest, CreateProblemResponse.class);

		assertThat(adminCreateProblemResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		ProblemDetailsUi problemDetails = adminCreateProblemResponse.getBody().problemDetails();
		problemId = problemDetails.id();
		assertProblemDetails(problemDetails);

		// Create additional problems to test the list and get APIs
		for (int i = 2; i <= PROBLEM_COUNT; i++) {
			adminCreateProblemResponse = restTemplate.postForEntity(
					BASE_URL + "/problems", adminCreateProblemRequest, CreateProblemResponse.class);
			assertThat(adminCreateProblemResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		}
	}

	@Test
	@Order(3)
	void retrieveProblems_withoutJwtHeaders() {
		ResponseEntity<ListProblemsResponse> listResponse =
				restTemplate.getForEntity(BASE_URL + "/problems/list", ListProblemsResponse.class);

		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		List<ProblemSummaryUi> problems = listResponse.getBody().problems();
		// 50 is the page size
		assertThat(problems).isNotNull().hasSize(50);

		listResponse = restTemplate.getForEntity(BASE_URL + "/problems?page=2", ListProblemsResponse.class);

		assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		problems = listResponse.getBody().problems();
		assertThat(problems).isNotNull().hasSize(10);

		ResponseEntity<GetProblemByIdResponse> problemDetailsResponse =
				restTemplate.getForEntity(BASE_URL + "/problems/" + problemId, GetProblemByIdResponse.class);

		assertThat(problemDetailsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertProblemDetails(problemDetailsResponse.getBody().problemDetails());
	}

	@Test
	@Order(4)
	void logoutUsers() {
		HttpHeaders userHeaders = new HttpHeaders();
		userHeaders.setBearerAuth(userToken);
		HttpEntity<Void> userLogoutRequest = new HttpEntity<>(userHeaders);
		ResponseEntity<Void> userLogoutResponse =
				restTemplate.postForEntity(BASE_URL + "/auth/logout", userLogoutRequest, Void.class);

		assertThat(userLogoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		HttpHeaders adminHeaders = new HttpHeaders();
		adminHeaders.setBearerAuth(adminToken);
		HttpEntity<Void> adminLogoutRequest = new HttpEntity<>(adminHeaders);
		ResponseEntity<Void> adminLogoutResponse =
				restTemplate.postForEntity(BASE_URL + "/auth/logout", adminLogoutRequest, Void.class);

		assertThat(adminLogoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private void assertProblemDetails(ProblemDetailsUi problemDetails) {
		assertThat(problemDetails).isNotNull();
		assertThat(problemDetails.title()).isEqualTo(PROBLEM_TITLE);
		assertThat(problemDetails.statement()).isEqualTo(PROBLEM_STATEMENT);
		assertThat(problemDetails.difficulty()).isEqualTo(PROBLEM_DIFFICULTY);
		assertThat(problemDetails.timeLimitInSecond()).isEqualTo(PROBLEM_TIME_LIMIT);
		assertThat(problemDetails.memoryLimitInMb()).isEqualTo(PROBLEM_MEMORY_LIMIT);
		assertThat(problemDetails.tags()).containsExactlyInAnyOrderElementsOf(PROBLEM_TAGS);
		assertThat(problemDetails.sampleTestCases()).hasSize(1);
	}
}
