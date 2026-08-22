package com.prepnovis.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.prepnovis.backend.entity.Question;

public interface QuestionRepository
        extends JpaRepository<Question, UUID>,
                JpaSpecificationExecutor<Question> {
}