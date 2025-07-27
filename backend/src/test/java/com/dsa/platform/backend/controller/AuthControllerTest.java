package com.dsa.platform.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dsa.platform.backend.dto.request.LoginRequest;
import com.dsa.platform.backend.dto.request.RegisterRequest;
import com.dsa.platform.backend.dto.response.AuthResponse;
import com.dsa.platform.backend.model.User;
import com.dsa.platform.backend.model.shared.UserRole;
import com.dsa.platform.backend.service.UserService;
import com.dsa.platform.backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserService userService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthController authController;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
	}

	@Test
	void registerUser_whenValidRequest_shouldReturnSuccessMessage() throws Exception {
		RegisterRequest registerRequest =
				new RegisterRequest("testuser", "test@example.com", "password123", "Test", "User", UserRole.USER);
		String dummyToken = "dummy-jwt-token";
		org.springframework.security.core.userdetails.User userDetails =
				new org.springframework.security.core.userdetails.User(
						registerRequest.handle(), registerRequest.password(), new ArrayList<>());
		when(userService.registerUser(registerRequest)).thenReturn(new User());
		Authentication authentication =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtUtil.generateToken(any())).thenReturn(dummyToken);

		ResponseEntity<AuthResponse> response = authController.registerUser(registerRequest);

		assertEquals(200, response.getStatusCode().value());
		assertEquals(dummyToken, response.getBody().token());

		mockMvc.perform(post("/api/v1/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(dummyToken));
	}

	@Test
	void loginUser_whenValidCredentials_shouldReturnAuthToken() throws Exception {
		LoginRequest loginRequest = new LoginRequest("testuser", "password123");
		String dummyToken = "dummy-jwt-token";
		org.springframework.security.core.userdetails.User userDetails =
				new org.springframework.security.core.userdetails.User(
						loginRequest.handle(), loginRequest.password(), new ArrayList<>());
		Authentication authentication =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		when(authenticationManager.authenticate(any())).thenReturn(authentication);
		when(jwtUtil.generateToken(any())).thenReturn(dummyToken);

		ResponseEntity<AuthResponse> response = authController.loginUser(loginRequest);
		assertEquals(200, response.getStatusCode().value());
		assertEquals(dummyToken, response.getBody().token());

		mockMvc.perform(post("/api/v1/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value(dummyToken));
	}

	@Test
	void loginUser_whenInvalidCredentials_shouldThrowException() {
		LoginRequest loginRequest = new LoginRequest("wronguser", "wrongpassword");
		when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

		try {
			authController.loginUser(loginRequest);
		} catch (BadCredentialsException e) {
			assertEquals("Bad credentials", e.getMessage());
		}
	}

	@Test
	void logoutUser_shouldReturnSuccessMessage() throws Exception {
		ResponseEntity<String> response =
				authController.logoutUser(new MockHttpServletRequest(), new MockHttpServletResponse());

		assertEquals(200, response.getStatusCode().value());
		assertEquals("User logged out successfully", response.getBody());
		mockMvc.perform(post("/api/v1/auth/logout")).andExpect(status().isOk());
	}
}
