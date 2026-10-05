-- Сначала нужны пользователи и машины, иначе FK не пройдёт.
-- Если таблицы уже заполнены одногруппниками, этот блок можно не выполнять.

INSERT INTO users (first_name, last_name, phone, email, driver_license)
VALUES
    ('Иван', 'Петров', '+79001112233', 'ivan.petrov@mail.ru', '1234567890'),
    ('Анна', 'Сидорова', '+79002223344', 'anna.sidorova@mail.ru', '2345678901'),
    ('Олег', 'Кузнецов', '+79003334455', NULL, NULL)
ON CONFLICT (phone) DO NOTHING;

INSERT INTO cars (brand, model, transmission, year, license_plate, body_type, status, mileage)
VALUES
    ('Toyota', 'Camry', 'AUTOMATIC', 2020, 'А123ВС777', 'SEDAN', 'AVAILABLE', 45000),
    ('Kia', 'Rio', 'MANUAL', 2018, 'В456ОР777', 'SEDAN', 'IN_USE', 87000),
    ('Hyundai', 'Solaris', 'AUTOMATIC', 2021, 'С789ЕТ777', 'HATCHBACK', 'AVAILABLE', 21000)
ON CONFLICT (license_plate) DO NOTHING;

INSERT INTO requests (user_id, car_id, title, description, status, priority, created_at)
VALUES
    (1, 1, 'Шум двигателя', 'При разгоне слышен посторонний шум из моторного отсека', 'NEW', 'HIGH', '2026-09-01'),
    (1, 2, 'Замена масла', 'Плановая замена масла и масляного фильтра', 'IN_PROGRESS', 'MEDIUM', '2026-09-03'),
    (2, 1, 'Царапина на двери', 'Нужна локальная покраска передней правой двери', 'NEW', 'LOW', '2026-09-05'),
    (2, 3, 'Не заводится', 'Автомобиль не заводится после ночной стоянки', 'IN_PROGRESS', 'HIGH', '2026-09-07'),
    (3, 2, 'Проверка тормозов', 'Скрип при торможении на низкой скорости', 'COMPLETED', 'HIGH', '2026-08-20'),
    (3, 1, 'Замена резины', 'Нужен шиномонтаж и балансировка колёс', 'COMPLETED', 'MEDIUM', '2026-08-25'),
    (1, 3, 'Кондиционер', 'Кондиционер слабо холодит в жару', 'CANCELLED', 'LOW', '2026-08-15'),
    (2, 2, 'Диагностика ЭБУ', 'Горит индикатор Check Engine, нужна диагностика', 'NEW', 'HIGH', '2026-09-10'),
    (3, 3, 'Мойка и химчистка', 'Подготовить автомобиль к передаче клиенту', 'IN_PROGRESS', 'LOW', '2026-09-12'),
    (1, 1, 'ТО-40000', 'Регламентное техническое обслуживание по пробегу', 'COMPLETED', 'MEDIUM', '2026-07-30');
