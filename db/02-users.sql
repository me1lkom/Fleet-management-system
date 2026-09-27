CREATE TABLE IF NOT EXISTS users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(12) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    driver_license VARCHAR(10) UNIQUE,

    CONSTRAINT check_first_name
    CHECK (LENGTH(TRIM(first_name)) > 0),

    CONSTRAINT check_last_name
    CHECK (LENGTH(TRIM(last_name)) > 0),

    CONSTRAINT check_phone
    CHECK (phone ~ '^[+]7[0-9]{10}$'),

    CONSTRAINT check_driver_license
    CHECK (
              driver_license IS NULL
              OR driver_license ~ '^[0-9]{10}$'
          )
    );
