package com.dsa.platform.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.platform.backend.model.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
