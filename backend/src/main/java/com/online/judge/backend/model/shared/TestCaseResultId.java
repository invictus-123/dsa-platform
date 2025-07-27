package com.online.judge.backend.model.shared;

import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite primary key for TestCaseResult. Consists of submissionId and
 * testCaseId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TestCaseResultId implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private Long submissionId;
	private UUID testCaseId;
}
