package com.dsa.platform.backend.repository;

import com.dsa.platform.backend.model.Tag;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
}
