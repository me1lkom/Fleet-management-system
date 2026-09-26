CREATE TABLE IF NOT EXISTS cars (
    car_id BIGSERIAL PRIMARY KEY,

    brand VARCHAR(30) NOT NULL,
    model VARCHAR(30) NOT NULL,
    transmission VARCHAR(30) NOT NULL,

    year INTEGER NOT NULL
        CHECK (year >= 1900),

    license_plate VARCHAR(30) UNIQUE NOT NULL,
    body_type VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL
        CHECK (status IN ('AVAILABLE','IN_USE','MAINTENANCE')),

    mileage INTEGER NOT NULL
        CHECK (mileage >= 0)
);