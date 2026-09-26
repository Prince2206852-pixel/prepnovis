package com.prepnovis.backend.repository.specification;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;

public final class QuestionSpecification {

    private QuestionSpecification() {
    }

    public static Specification<Question> belongsToUser(UUID userId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    public static Specification<Question> hasCategory(String category) {

        return (root, query, criteriaBuilder) -> {

            if (category == null || category.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category")),
                    category.trim().toLowerCase()
            );
        };
    }

    public static Specification<Question> hasTopic(String topic) {

        return (root, query, criteriaBuilder) -> {

            if (topic == null || topic.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("topic")),
                    topic.trim().toLowerCase()
            );
        };
    }

    public static Specification<Question> hasDifficulty(
            DifficultyLevel difficultyLevel) {

        return (root, query, criteriaBuilder) -> {

            if (difficultyLevel == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("difficultyLevel"),
                    difficultyLevel
            );
        };
    }

    public static Specification<Question> hasQuestionType(
            QuestionType questionType) {

        return (root, query, criteriaBuilder) -> {

            if (questionType == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("questionType"),
                    questionType
            );
        };
    }

    /**
     * Searches the user's saved questions using a general keyword.
     *
     * Supported text fields:
     * - question text
     * - category
     * - topic
     * - tags
     *
     * Question-number search (#25 / 25) is handled separately
     * so that it remains an exact numeric search.
     */
    public static Specification<Question> containsKeyword(
            String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String searchValue =
                    "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("questionText")
                            ),
                            searchValue
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("category")
                            ),
                            searchValue
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("topic")
                            ),
                            searchValue
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("tags")
                            ),
                            searchValue
                    )
            );
        };
    }

    /**
     * Exact search using the permanent saved-question number.
     */
    public static Specification<Question> hasQuestionNumber(
            Long questionNumber) {

        return (root, query, criteriaBuilder) -> {

            if (questionNumber == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("questionNumber"),
                    questionNumber
            );
        };
    }
}