package com.online.judge.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.online.judge.backend.model.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {}
