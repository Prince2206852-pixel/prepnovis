-- Add ownership to saved questions

ALTER TABLE questions
ADD COLUMN user_id UUID;

-- Assign existing development questions to the existing PrepNovis user
UPDATE questions
SET user_id = '48cb28c9-d755-413d-9991-45e99434040e'
WHERE user_id IS NULL;

-- Every saved question must belong to a user
ALTER TABLE questions
ALTER COLUMN user_id SET NOT NULL;

-- Add foreign key to users
ALTER TABLE questions
ADD CONSTRAINT fk_questions_user
FOREIGN KEY (user_id)
REFERENCES users(id);

-- Improve queries that fetch saved questions for a user
CREATE INDEX idx_questions_user_id
ON questions(user_id);