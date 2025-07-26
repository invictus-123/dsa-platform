package com.dsa.platform.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception for handling user authorization failures.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotAuthorizedException extends RuntimeException {
	public UserNotAuthorizedException(String message) {
		super(message);
	}
}
