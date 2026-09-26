-- Add stable ordering for questions inside each practice session.
-- Existing rows are numbered using their creation time and UUID
-- so every old session also gets a deterministic sequence.

ALTER TABLE practice_session_questions
ADD COLUMN question_order INTEGER;

WITH ordered_questions AS (
    SELECT
        id,
        ROW_NUMBER() OVER (
            PARTITION BY practice_session_id
            ORDER BY created_at ASC, id ASC
        ) AS rn
    FROM practice_session_questions
)
UPDATE practice_session_questions psq
SET question_order = oq.rn
FROM ordered_questions oq
WHERE psq.id = oq.id;

ALTER TABLE practice_session_questions
ALTER COLUMN question_order SET NOT NULL;

CREATE UNIQUE INDEX uk_practice_session_question_order
ON practice_session_questions (practice_session_id, question_order);