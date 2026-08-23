package com.prepnovis.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;


public interface QuestionRepository
        extends JpaRepository<Question, UUID>,
                JpaSpecificationExecutor<Question> {
                    List<Question> findByCategoryIgnoreCaseAndTopicIgnoreCaseAndDifficultyLevelAndQuestionType(
        String category,
        String topic,
        DifficultyLevel difficultyLevel,
        QuestionType questionType);
}
