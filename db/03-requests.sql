CREATE TABLE IF NOT EXISTS requests (
    request_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    monthly_price INTEGER NOT NULL CHECK (monthly_price > 0),
    status VARCHAR(30) NOT NULL
        CHECK (status IN ('NEW', 'APPROVED', 'ACTIVE', 'COMPLETED', 'CANCELLED')),
    notes TEXT,

    CONSTRAINT check_rental_period
        CHECK (end_date > start_date AND (end_date - start_date) >= 30),

    CONSTRAINT fk_requests_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_requests_car
        FOREIGN KEY (car_id) REFERENCES cars(car_id)
);