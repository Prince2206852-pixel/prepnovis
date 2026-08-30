package com.prepnovis.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepnovis.backend.entity.PracticeSessionQuestion;

public interface PracticeSessionQuestionRepository
        extends JpaRepository<PracticeSessionQuestion, UUID> {

    List<PracticeSessionQuestion> findByPracticeSessionId(UUID practiceSessionId);

    List<PracticeSessionQuestion> findByPracticeSessionUserId(UUID userId);
}