ALTER TABLE users
    ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE users
SET email_verified = TRUE
WHERE auth_provider = 'GOOGLE';

UPDATE users
SET email_verified = TRUE
WHERE auth_provider = 'LOCAL';