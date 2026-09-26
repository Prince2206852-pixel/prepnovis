-- Add a permanent user-facing number to every saved question.
-- Numbering is independent for each user.
--
-- Example:
-- User A -> #1, #2, #3
-- User B -> #1, #2
--
-- Existing questions are numbered using creation time.
-- Once assigned, these numbers must not be changed when a question is deleted.

ALTER TABLE questions
ADD COLUMN question_number BIGINT;

-- Assign numbers to all existing questions separately for each user.
WITH numbered_questions AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY user_id
            ORDER BY created_at ASC, id ASC
        ) AS question_number
    FROM questions
)
UPDATE questions q
SET question_number = nq.question_number
FROM numbered_questions nq
WHERE q.id = nq.id;

-- Every saved question must have a permanent number.
ALTER TABLE questions
ALTER COLUMN question_number SET NOT NULL;

-- The same user cannot have two questions with the same number.
CREATE UNIQUE INDEX uk_questions_user_question_number
ON questions (user_id, question_number);