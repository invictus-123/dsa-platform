package com.dsa.platform.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** A DTO for encapsulating user login data from the API request. */
public record LoginRequest(
		@NotBlank(message = "Handle cannot be empty")
				@Size(min = 3, max = 20, message = "Handle must be between 3 and 20 characters")
				String handle,
		@NotBlank(message = "Password cannot be empty")
				@Size(min = 8, message = "Password must be at least 8 characters long")
				String password) {}
