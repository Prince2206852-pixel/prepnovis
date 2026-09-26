package com.prepnovis.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Returns the highest permanent question number already assigned
     * to a particular user.
     *
     * If the user has no saved questions yet, returns 0.
     */
    @Query("""
            SELECT COALESCE(MAX(q.questionNumber), 0)
            FROM Question q
            WHERE q.user.id = :userId
            """)
    Long findMaxQuestionNumberByUserId(
            @Param("userId") UUID userId
    );

    /**
     * Find a saved question using its permanent user-facing number.
     *
     * Example:
     * User A can have #5 and User B can also have #5.
     */
    Optional<Question> findByUserIdAndQuestionNumber(
            UUID userId,
            Long questionNumber
    );
}