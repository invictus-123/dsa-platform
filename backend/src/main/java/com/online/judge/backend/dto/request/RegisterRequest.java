package com.online.judge.backend.dto.request;

import com.online.judge.backend.model.shared.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** A DTO for encapsulating user registration data from the API request. */
public record RegisterRequest(
		@NotBlank(message = "Handle cannot be empty")
				@Size(min = 3, max = 20, message = "Handle must be between 3 and 20 characters")
				String handle,
		@NotBlank(message = "Email cannot be empty") @Email(message = "Please provide a valid email address")
				String email,
		@NotBlank(message = "Password cannot be empty")
				@Size(min = 8, message = "Password must be at least 8 characters long")
				String password,
		@Pattern(regexp = "^[a-zA-Z]*$", message = "First name must contain only alphabets") String firstName,
		@Pattern(regexp = "^[a-zA-Z]*$", message = "Last name must contain only alphabets") String lastName,
		UserRole userRole) {}
