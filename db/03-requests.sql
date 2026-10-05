CREATE TABLE IF NOT EXISTS requests (
    request_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL
        CHECK (status IN ('NEW', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    priority VARCHAR(30) NOT NULL
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    created_at DATE NOT NULL,

    CONSTRAINT check_title
        CHECK (LENGTH(TRIM(title)) > 0),

    CONSTRAINT check_description
        CHECK (LENGTH(TRIM(description)) >= 5),

    CONSTRAINT fk_requests_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_requests_car
        FOREIGN KEY (car_id) REFERENCES cars(car_id)
);
