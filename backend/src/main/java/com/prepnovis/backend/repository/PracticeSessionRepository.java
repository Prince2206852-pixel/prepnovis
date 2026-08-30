package com.prepnovis.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepnovis.backend.entity.PracticeSession;

public interface PracticeSessionRepository
        extends JpaRepository<PracticeSession, UUID> {

    List<PracticeSession> findByUserId(UUID userId);

    List<PracticeSession> findTop5ByUserIdOrderByCreatedAtDesc(UUID userId);
}