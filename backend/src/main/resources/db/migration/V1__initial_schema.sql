CREATE TABLE roles (
    id UUID NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    description VARCHAR(500),
    name VARCHAR(255) NOT NULL,

    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
);


CREATE TABLE users (
    id UUID NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
);


CREATE TABLE questions (
    id UUID NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    answer VARCHAR(5000),
    category VARCHAR(255) NOT NULL,
    difficulty_level VARCHAR(255) NOT NULL,
    question_text VARCHAR(2000) NOT NULL,
    question_type VARCHAR(255) NOT NULL,
    tags VARCHAR(1000),
    topic VARCHAR(255) NOT NULL,

    CONSTRAINT questions_pkey PRIMARY KEY (id),

    CONSTRAINT questions_difficulty_level_check
        CHECK (difficulty_level IN ('EASY', 'MEDIUM', 'HARD')),

    CONSTRAINT questions_question_type_check
        CHECK (
            question_type IN (
                'TECHNICAL',
                'CODING',
                'SYSTEM_DESIGN',
                'BEHAVIORAL',
                'MCQ',
                'OTHER'
            )
        )
);


CREATE TABLE practice_sessions (
    id UUID NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    category VARCHAR(255) NOT NULL,
    difficulty_level VARCHAR(255) NOT NULL,
    question_type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    total_questions INTEGER NOT NULL,
    user_id UUID NOT NULL,
    completed_at TIMESTAMP(6),
    question_source VARCHAR(255) NOT NULL,

    CONSTRAINT practice_sessions_pkey PRIMARY KEY (id),

    CONSTRAINT fk_practice_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT practice_sessions_difficulty_level_check
        CHECK (difficulty_level IN ('EASY', 'MEDIUM', 'HARD')),

    CONSTRAINT practice_sessions_question_type_check
        CHECK (
            question_type IN (
                'TECHNICAL',
                'CODING',
                'SYSTEM_DESIGN',
                'BEHAVIORAL',
                'MCQ',
                'OTHER'
            )
        ),

    CONSTRAINT practice_sessions_status_check
        CHECK (
            status IN (
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED'
            )
        )
);


CREATE TABLE practice_session_questions (
    id UUID NOT NULL,
    created_at TIMESTAMP(6),
    updated_at TIMESTAMP(6),
    answered BOOLEAN NOT NULL,
    feedback TEXT,
    score DOUBLE PRECISION,
    user_answer TEXT,
    practice_session_id UUID NOT NULL,
    question_id UUID,
    improvements TEXT,
    strengths TEXT,
    mock_question_text VARCHAR(2000),
    mock_reference_answer VARCHAR(5000),

    CONSTRAINT practice_session_questions_pkey PRIMARY KEY (id),

    CONSTRAINT fk_practice_session_questions_session
        FOREIGN KEY (practice_session_id)
        REFERENCES practice_sessions(id),

    CONSTRAINT fk_practice_session_questions_question
        FOREIGN KEY (question_id)
        REFERENCES questions(id)
);