package com.dsa.platform.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsa.platform.backend.model.Problem;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    /**
     * Finds all problems that are associated with any of the given tag names.
     *
     * @param tagNames A list of tag names to search for.
     * @return A list of problems that have at least one of the specified tags.
     */
    List<Problem> findAllByTags(List<String> tagNames);
}
