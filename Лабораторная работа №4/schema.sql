-- Лабораторная работа №4 (JPA), вариант 8 "Автобаза"
-- Схема для Microsoft SQL Server (T-SQL)
--
-- Диспетчер распределяет заявки на рейсы между водителями, за каждым из которых
-- закреплён свой автомобиль. На рейс может быть назначен автомобиль, находящийся
-- в исправном состоянии и характеристики которого соответствуют заявке.
--
-- Как создать базу (PowerShell, от имени администратора):
--   & "C:\Program Files\Microsoft SQL Server\Client SDK\ODBC\170\Tools\Binn\SQLCMD.EXE" `
--       -S localhost -E -i schema.sql
--   либо выполнить скрипт в SSMS.
-- Параметры подключения хранятся в src/main/resources/META-INF/persistence.xml

USE master;
GO

IF DB_ID('autobase') IS NOT NULL
BEGIN
    ALTER DATABASE autobase SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE autobase;
END
GO

CREATE DATABASE autobase;
GO

USE autobase;
GO

-- Таблицы во 2НФ: первичные ключи одноколоночные (суррогатные id), частичных
-- зависимостей нет. Справочники (customer, route, body_type) вынесены в отдельные
-- таблицы, поэтому неключевые атрибуты зависят только от ключа — нет и
-- транзитивных зависимостей (3НФ): заказчик/маршрут/кузов в заявке хранятся
-- только по id, а не строкой.

CREATE TABLE customer (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE route (
    route_id  INT IDENTITY(1,1) PRIMARY KEY,
    city_from NVARCHAR(100) NOT NULL,
    city_to   NVARCHAR(100) NOT NULL,
    CONSTRAINT uq_route UNIQUE (city_from, city_to)
);

CREATE TABLE body_type (
    body_type_id INT IDENTITY(1,1) PRIMARY KEY,
    name         NVARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE driver (
    driver_id        INT IDENTITY(1,1) PRIMARY KEY,
    full_name        NVARCHAR(100) NOT NULL,
    license_category NVARCHAR(10)  NOT NULL,
    phone            NVARCHAR(20)
);

CREATE TABLE car (
    car_id       INT IDENTITY(1,1) PRIMARY KEY,
    plate_number NVARCHAR(20) NOT NULL UNIQUE,
    model        NVARCHAR(50) NOT NULL,
    body_type_id INT          NOT NULL,
    load_kg      INT          NOT NULL, -- грузоподъёмность, кг, сверяется с заявкой
    status       NVARCHAR(12) NOT NULL DEFAULT 'SERVICEABLE',
    CONSTRAINT fk_car_body_type FOREIGN KEY (body_type_id) REFERENCES body_type (body_type_id),
    CONSTRAINT ck_car_status CHECK (status IN ('SERVICEABLE', 'REPAIR'))
);

CREATE TABLE request (
    request_id            INT IDENTITY(1,1) PRIMARY KEY,
    customer_id           INT NOT NULL,
    route_id              INT NOT NULL,
    required_body_type_id INT NOT NULL,
    required_load_kg      INT NOT NULL,
    departure_date        DATE NOT NULL,
    CONSTRAINT fk_request_customer FOREIGN KEY (customer_id) REFERENCES customer (customer_id),
    CONSTRAINT fk_request_route FOREIGN KEY (route_id) REFERENCES route (route_id),
    CONSTRAINT fk_request_body_type FOREIGN KEY (required_body_type_id) REFERENCES body_type (body_type_id)
);

CREATE TABLE trip (
    trip_id             INT PRIMARY KEY, -- id задаётся вручную, без auto-increment
    request_id          INT NOT NULL,
    driver_id           INT NULL, -- водитель назначается диспетчером
    car_id              INT NULL, -- автомобиль назначается с проверкой характеристик
    status              NVARCHAR(10) NOT NULL DEFAULT 'PLANNED',
    departure_date      DATE NOT NULL,
    completion_date     DATE NULL,
    car_condition_after NVARCHAR(12) NULL, -- отметка водителя
    CONSTRAINT fk_trip_request FOREIGN KEY (request_id) REFERENCES request (request_id),
    CONSTRAINT fk_trip_driver FOREIGN KEY (driver_id) REFERENCES driver (driver_id),
    CONSTRAINT fk_trip_car FOREIGN KEY (car_id) REFERENCES car (car_id),
    CONSTRAINT ck_trip_status CHECK (status IN ('PLANNED', 'COMPLETED')),
    CONSTRAINT ck_trip_car_condition CHECK (car_condition_after IS NULL OR car_condition_after IN ('SERVICEABLE', 'REPAIR'))
);
GO

-- ----------------------------------------------------------------------------
-- Тестовые данные
-- ----------------------------------------------------------------------------

INSERT INTO body_type (name) VALUES (N'фургон'), (N'борт'), (N'рефрижератор'), (N'седан'), (N'внедорожник');

INSERT INTO customer (name) VALUES (N'ООО "Ромашка"'), (N'ИП "Козлов"'), (N'ЗАО "Холод"'), (N'ООО "Стройдом"');

INSERT INTO route (city_from, city_to) VALUES (N'Минск', N'Брест'), (N'Минск', N'Гомель'), (N'Гродно', N'Минск'), (N'Витебск', N'Могилёв');

INSERT INTO driver (full_name, license_category, phone) VALUES
    (N'Иванов Иван Иванович', 'B', '+375291111111'),
    (N'Петров Пётр Петрович', 'C', '+375292222222'),
    (N'Сидоров Алексей Викторович', 'C', '+375293333333'),
    (N'Козлов Дмитрий Сергеевич', 'BE', '+375294444444');

INSERT INTO car (plate_number, model, body_type_id, load_kg, status) VALUES
    ('1234AB-7', N'МАЗ-4370', 1, 3000, 'SERVICEABLE'),
    ('5678CD-7', N'КАМАЗ-4308', 2, 5000, 'SERVICEABLE'),
    ('9012EF-7', N'ГАЗ-3309', 1, 1500, 'SERVICEABLE'),
    ('3456GH-7', N'МАЗ-5440', 3, 8000, 'SERVICEABLE'),
    ('7890JK-7', N'УАЗ-3909', 1, 1000, 'REPAIR');

INSERT INTO request (customer_id, route_id, required_body_type_id, required_load_kg, departure_date) VALUES
    (1, 1, 1, 1000, '2026-10-15'),
    (2, 2, 2, 4500, '2026-10-16'),
    (3, 3, 3, 7000, '2026-10-17'),
    (4, 4, 2, 3000, '2026-10-18');

INSERT INTO trip (trip_id, request_id, driver_id, car_id, status, departure_date, completion_date, car_condition_after) VALUES
    (1, 1, 1, 1, 'COMPLETED', '2026-10-15', '2026-10-15', 'SERVICEABLE'),
    (2, 2, 2, 2, 'PLANNED', '2026-10-16', NULL, NULL),
    (3, 3, NULL, 4, 'PLANNED', '2026-10-17', NULL, NULL),
    (4, 4, NULL, NULL, 'PLANNED', '2026-10-18', NULL, NULL);
GO
