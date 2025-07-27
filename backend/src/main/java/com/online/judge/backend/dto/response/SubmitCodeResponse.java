package com.online.judge.backend.dto.response;

import com.online.judge.backend.dto.ui.SubmissionDetailsUi;

/** A DTO for encapsulating the response of user's code submission to a problem. */
public record SubmitCodeResponse(SubmissionDetailsUi submissionDetails) {}
