INSERT INTO roles (
    id,
    created_at,
    updated_at,
    name,
    description
)
SELECT
    gen_random_uuid(),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'USER',
    'Default application user role'
WHERE NOT EXISTS (
    SELECT 1
    FROM roles
    WHERE name = 'USER'
);