package com.dsa.platform.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the 'test_cases' table in the database. Each test case is
 * associated with a problem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_cases")
public class TestCase {

	/** The primary key for the test_cases table. */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	/** The problem this test case belongs to. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id", nullable = false)
	private Problem problem;

	/** Whether this test case is a sample. */
	@NotNull
	@Column(name = "is_sample", nullable = false)
	private Boolean isSample;

	/**
	 * Explanation for the sample test case (only populated if isSample is true).
	 */
	@Column(name = "explanation", columnDefinition = "TEXT")
	private String explanation;

	/** The input for the test case. */
	@NotBlank
	@Column(name = "input", columnDefinition = "TEXT", nullable = false)
	private String input;

	/** The expected output for the test case. */
	@NotBlank
	@Column(name = "output", columnDefinition = "TEXT", nullable = false)
	private String output;
}
