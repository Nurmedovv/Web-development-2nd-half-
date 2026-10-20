-- Лабораторная работа №3 (JDBC), вариант 8 "Автобаза"
--
-- Диспетчер распределяет заявки на рейсы между водителями, за каждым из которых
-- закреплён свой автомобиль. На рейс может быть назначен автомобиль, находящийся
-- в исправном состоянии и характеристики которого соответствуют заявке.
--
-- Как создать базу (из корня проекта):
--   "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -uroot -p < schema.sql
-- Параметры подключения хранятся в src/main/resources/resources/database.properties

DROP DATABASE IF EXISTS autobase;
CREATE DATABASE autobase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE autobase;

-- Таблицы во 2НФ: первичные ключи одноколоночные (суррогатные id), частичных
-- зависимостей нет. Справочники (customer, route, body_type) вынесены в отдельные
-- таблицы, поэтому неключевые атрибуты зависят только от ключа — нет и
-- транзитивных зависимостей (3НФ): заказчик/маршрут/кузов в заявке хранятся
-- только по id, а не строкой.

CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE route (
    route_id  INT AUTO_INCREMENT PRIMARY KEY,
    city_from VARCHAR(100) NOT NULL,
    city_to   VARCHAR(100) NOT NULL,
    CONSTRAINT uq_route UNIQUE (city_from, city_to)
) ENGINE = InnoDB;

CREATE TABLE body_type (
    body_type_id INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(30) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE driver (
    driver_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name       VARCHAR(100) NOT NULL,
    license_category VARCHAR(10) NOT NULL,
    phone           VARCHAR(20)
) ENGINE = InnoDB;

CREATE TABLE car (
    car_id       INT AUTO_INCREMENT PRIMARY KEY,
    plate_number VARCHAR(20) NOT NULL UNIQUE,
    model        VARCHAR(50) NOT NULL,
    body_type_id INT         NOT NULL,
    load_kg      INT         NOT NULL, -- грузоподъёмность, кг, сверяется с заявкой
    status       ENUM ('SERVICEABLE', 'REPAIR') NOT NULL DEFAULT 'SERVICEABLE',
    CONSTRAINT fk_car_body_type FOREIGN KEY (body_type_id) REFERENCES body_type (body_type_id)
) ENGINE = InnoDB;

CREATE TABLE request (
    request_id             INT AUTO_INCREMENT PRIMARY KEY,
    customer_id            INT NOT NULL,
    route_id               INT NOT NULL,
    required_body_type_id  INT NOT NULL,
    required_load_kg       INT NOT NULL,
    departure_date         DATE NOT NULL,
    CONSTRAINT fk_request_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id),
    CONSTRAINT fk_request_route FOREIGN KEY (route_id) REFERENCES route (route_id),
    CONSTRAINT fk_request_body_type FOREIGN KEY (required_body_type_id) REFERENCES body_type (body_type_id)
) ENGINE = InnoDB;

CREATE TABLE trip (
    trip_id              INT AUTO_INCREMENT PRIMARY KEY,
    request_id           INT NOT NULL,
    driver_id            INT NULL, -- водитель назначается диспетчером
    car_id               INT NULL, -- автомобиль назначается с проверкой характеристик
    status               ENUM ('PLANNED', 'COMPLETED') NOT NULL DEFAULT 'PLANNED',
    departure_date       DATE NOT NULL,
    completion_date      DATE NULL,
    car_condition_after  ENUM ('SERVICEABLE', 'REPAIR') NULL, -- отметка водителя
    CONSTRAINT fk_trip_request FOREIGN KEY (request_id) REFERENCES request (request_id),
    CONSTRAINT fk_trip_driver FOREIGN KEY (driver_id) REFERENCES driver (driver_id),
    CONSTRAINT fk_trip_car FOREIGN KEY (car_id) REFERENCES car (car_id)
) ENGINE = InnoDB;

-- ----------------------------------------------------------------------------
-- Тестовые данные
-- ----------------------------------------------------------------------------

INSERT INTO body_type (name)
VALUES ('фургон'),
       ('борт'),
       ('рефрижератор'),
       ('седан'),
       ('внедорожник');

INSERT INTO customer (name)
VALUES ('ООО "Ромашка"'),
       ('ИП "Козлов"'),
       ('ЗАО "Холод"'),
       ('ООО "Стройдом"');

INSERT INTO route (city_from, city_to)
VALUES ('Минск', 'Брест'),
       ('Минск', 'Гомель'),
       ('Гродно', 'Минск'),
       ('Витебск', 'Могилёв');

INSERT INTO driver (full_name, license_category, phone)
VALUES ('Иванов Иван Иванович', 'B', '+375291111111'),
       ('Петров Пётр Петрович', 'C', '+375292222222'),
       ('Сидоров Алексей Викторович', 'C', '+375293333333'),
       ('Козлов Дмитрий Сергеевич', 'BE', '+375294444444');

INSERT INTO car (plate_number, model, body_type_id, load_kg, status)
VALUES ('1234AB-7', 'МАЗ-4370', 1, 3000, 'SERVICEABLE'),
       ('5678CD-7', 'КАМАЗ-4308', 2, 5000, 'SERVICEABLE'),
       ('9012EF-7', 'ГАЗ-3309', 1, 1500, 'SERVICEABLE'),
       ('3456GH-7', 'МАЗ-5440', 3, 8000, 'SERVICEABLE'),
       ('7890JK-7', 'УАЗ-3909', 1, 1000, 'REPAIR');

INSERT INTO request (customer_id, route_id, required_body_type_id, required_load_kg, departure_date)
VALUES (1, 1, 1, 1000, '2026-10-15'),
       (2, 2, 2, 4500, '2026-10-16'),
       (3, 3, 3, 7000, '2026-10-17'),
       (4, 4, 2, 3000, '2026-10-18');

INSERT INTO trip (request_id, driver_id, car_id, status, departure_date, completion_date, car_condition_after)
VALUES (1, 1, 1, 'COMPLETED', '2026-10-15', '2026-10-15', 'SERVICEABLE'),
       (2, 2, 2, 'PLANNED', '2026-10-16', NULL, NULL),
       (3, NULL, 4, 'PLANNED', '2026-10-17', NULL, NULL),
       (4, NULL, NULL, 'PLANNED', '2026-10-18', NULL, NULL);
