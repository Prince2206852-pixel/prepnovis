package com.prepnovis.backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.prepnovis.backend.entity.Question;
import com.prepnovis.backend.entity.enums.DifficultyLevel;
import com.prepnovis.backend.entity.enums.QuestionType;

public class QuestionSpecification {

    public static Specification<Question> hasCategory(String category) {

        return (root, query, criteriaBuilder) -> {

            if (category == null || category.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("category")),
                    category.toLowerCase()
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
                    topic.toLowerCase()
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
}