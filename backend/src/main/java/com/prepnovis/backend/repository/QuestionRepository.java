package com.prepnovis.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;

public interface QuestionRepository
        extends JpaRepository<Question, UUID>,
                JpaSpecificationExecutor<Question> {

    Optional<Question> findByIdAndUserId(
            UUID id,
            UUID userId
    );

    List<Question>
            findByUserIdAndCategoryIgnoreCaseAndTopicIgnoreCaseAndDifficultyLevelAndQuestionType(
                    UUID userId,
                    String category,
                    String topic,
                    DifficultyLevel difficultyLevel,
                    QuestionType questionType
            );
}