package com.online.judge.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Custom exception for handling problem not found scenarios. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProblemNotFoundException extends RuntimeException {
	public ProblemNotFoundException(String message) {
		super(message);
	}
}
