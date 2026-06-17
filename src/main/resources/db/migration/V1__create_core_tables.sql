CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(255) NOT NULL,
    category VARCHAR(60) NOT NULL,
    active BIT(1) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BIT(1) NOT NULL,
    CONSTRAINT pk_rooms PRIMARY KEY (id),
    CONSTRAINT uk_rooms_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS anonymous_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_token VARCHAR(120) NOT NULL,
    display_name VARCHAR(120) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    is_deleted BIT(1) NOT NULL,
    CONSTRAINT pk_anonymous_sessions PRIMARY KEY (id),
    CONSTRAINT uk_anonymous_sessions_session_token UNIQUE (session_token)
);
