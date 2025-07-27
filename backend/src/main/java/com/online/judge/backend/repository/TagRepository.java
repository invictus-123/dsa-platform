package com.online.judge.backend.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.online.judge.backend.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {}
