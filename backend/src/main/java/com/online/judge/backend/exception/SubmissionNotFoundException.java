package com.online.judge.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Custom exception for handling submission not found scenarios. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SubmissionNotFoundException extends RuntimeException {
	public SubmissionNotFoundException(String message) {
		super(message);
	}
}
