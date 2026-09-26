-- Permanent counter used for Saved Question numbering.
--
-- The counter stores the NEXT number that should be assigned.
-- Deleting a saved question never decreases this value, so deleted
-- question numbers are never reused.

ALTER TABLE users
ADD COLUMN next_question_number BIGINT;

-- Existing users may already have saved questions.
-- Initialize each counter to MAX(existing question number) + 1.
UPDATE users u
SET next_question_number =
    COALESCE(
        (
            SELECT MAX(q.question_number) + 1
            FROM questions q
            WHERE q.user_id = u.id
        ),
        1
    );

ALTER TABLE users
ALTER COLUMN next_question_number SET NOT NULL;

ALTER TABLE users
ALTER COLUMN next_question_number SET DEFAULT 1;