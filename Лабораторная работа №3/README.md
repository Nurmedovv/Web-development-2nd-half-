# Лабораторная работа №3 — Java, JDBC, MySQL (вариант 8)

Консольное приложение **«Диспетчерская автобазы»** на Java с доступом к MySQL по JDBC.  
**Вариант 8**: Автобаза. Управление рейсами.

Диспетчер распределяет заявки на рейсы между водителями, за каждым из которых закреплён свой автомобиль. На рейс назначается автомобиль, находящийся в исправном состоянии и характеристики которого соответствуют заявке. Данные читаются и пишутся в **MySQL** через **пул соединений** и DAO-слой на `PreparedStatement`.

---

## База данных

**Проект:** `test1` | **Точка входа:** `by.bsu.lab3.view.Runner`

| Ресурс | Значение |
|--------|----------|
| База данных | `autobase` — MySQL 8, InnoDB, `utf8mb4` |
| Таблиц | 7: `customer`, `route`, `body_type`, `driver`, `car`, `request`, `trip` |
| Схема + тестовые данные | `test1/schema.sql` |
| Диаграмма | `test1/Схема БД.png` |
| Подключение | `src/main/resources/resources/database.properties` |
| Пул соединений | 5 (`pool.size=5`), таймаут взятия — 10 с |
| Лог-файл | `lab3.log` (INFO) + консоль (WARNING+) |
| Условие | `test1/Лаб3_JDBC.doc` |

---

## Условие

### Вариант 8 — Автобаза

- База данных автобазы: заказчики, маршруты, типы кузова, водители, автомобили, заявки и рейсы
- Диспетчер распределяет заявки на рейсы между водителями
- У каждого водителя закреплён свой автомобиль
- На рейс может быть назначен автомобиль, находящийся в **исправном состоянии** и характеристики которого (тип кузова, грузоподъёмность) **соответствуют заявке**
- По завершении рейса фиксируется состояние автомобиля после рейса

### Что реализовано

- Создание базы MySQL по варианту: 7 таблиц, внешние ключи, `ENUM`-статусы, тестовые данные
- Нормализация: **2НФ** (одноколоночные суррогатные ключи), **3НФ** (справочники `customer`, `route`, `body_type` вынесены в отдельные таблицы)
- JDBC-подключение: драйвер `mysql-connector-j`, параметры в `database.properties`
- **Пул соединений** (`ConnectionPool`): синглтон, `ArrayBlockingQueue`, переиспользование, замена битых соединений
- DAO-слой из 7 классов поверх `PreparedStatement` (без конкатенации строк)
- Бизнес-правила назначения автомобиля и завершения рейса
- Транзакция при завершении рейса (`commit` / `rollback`)
- Консольное меню на 17 пунктов с валидацией ввода
- Свои исключения `DAOException` и `JDBCConnectionException` + `java.util.logging`

### Чек-лист

- [x] Схема БД создана и залита тестовыми данными (`schema.sql`)
- [x] Нормализация 2НФ/3НФ, диаграмма `Схема БД.png`
- [x] Подключение к MySQL через `JdbcConnector` + `ConfigurationManager`
- [x] Пул соединений `ConnectionPool` (размер из properties)
- [x] DAO-слой: `DAOTrip`, `DAODriver`, `DAOCar`, `DAORequest`, `DAOCustomer`, `DAORoute`, `DAOBodyType`
- [x] Все запросы — через `PreparedStatement`
- [x] Транзакция «завершение рейса» с откатом при ошибке
- [x] Проверка: авто исправно + тип кузова + грузоподъёмность
- [x] Логирование в `lab3.log`, консоль — только WARNING+
- [x] Приложение запускается и работает с базой

---

## Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" height="28"/>
  <img src="https://img.shields.io/badge/JDBC-00758F?style=for-the-badge&logoColor=white" alt="JDBC" height="28"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" height="28"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" height="28"/>
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA" height="28"/>
  <img src="https://img.shields.io/badge/java.util.logging-2D2D2D?style=for-the-badge&logoColor=white" alt="java.util.logging" height="28"/>
</p>

---

## Архитектура

```
Runner (меню на 17 пунктов, ввод с валидацией, логирование)
  │
  ├── DAOTrip ────┐
  ├── DAODriver   │   entity/: Trip, Car, Driver, Request,
  ├── DAOCar      ├── Customer, Route, BodyType
  ├── DAORequest  │        ▲
  ├── DAOCustomer │   ResultSet ◄──► PreparedStatement
  ├── DAORoute    │
  └── DAOBodyType ┘
        │
        ▼
     DAO (abstract) ── takeConnection() / releaseConnection() / fail()
        │                     │ коды MySQL 1451, 1062
        ▼
  ConnectionPool (синглтон, ArrayBlockingQueue, 5 соединений)
        │
        ▼
  JdbcConnector ──► MySQL (база autobase)
        ▲
  ConfigurationManager ◄── database.properties

  exept/: DAOException, JDBCConnectionException
```

---

## Ключевые механизмы

### 1. `database.properties` + `ConfigurationManager` — параметры подключения

```properties
url=jdbc:mysql://localhost:3306/autobase?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
driver=com.mysql.cj.jdbc.Driver
user=root
password=1234
pool.size=5
```

`ConfigurationManager` — синглтон на `ResourceBundle`, читает бандл `resources.database` один раз. Если `pool.size` пуст — берётся значение по умолчанию `5`.

### 2. `JdbcConnector.java` — открытие соединения

```java
Class.forName(cfg.getDriver());
connection = DriverManager.getConnection(cfg.getURL(), cfg.getUser(), cfg.getPass());
```

Драйвер подгружается явно; при его отсутствии на classpath бросается `JDBCConnectionException` с сообщением об ошибке в лог.

### 3. `ConnectionPool.java` — пул соединений

```java
private static final ConnectionPool INSTANCE = new ConnectionPool();
private final Set<Connection> allConnections = ConcurrentHashMap.newKeySet();
private volatile BlockingQueue<Connection> freeConnections;
```

**Взятие** (`takeConnection`):

```java
Connection connection = queue.poll(TAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS); // 10 с
if (connection.isClosed()) {                       // битое соединение
    allConnections.remove(connection);
    connection = connector.getConnection();        // подмена новым
}
```

**Возврат** (`releaseConnection`): сбрасывается `autoCommit` в `true`, битое соединение не возвращается в очередь, а закрывается. Инициализация пула ленивая и потокобезопасная (`synchronized init()`).

### 4. `DAO.java` — базовый класс DAO

```java
protected Connection takeConnection() throws DAOException { ... }
protected void releaseConnection(Connection connection) { ... }

protected static DAOException fail(String message, SQLException cause) {
    LOGGER.log(Level.SEVERE, message, cause);
    return new DAOException(message + ": " + cause.getMessage(), cause);
}

// MySQL 1451: на строку есть ссылки в других таблицах
protected static boolean isForeignKeyViolation(SQLException e) {
    return e.getErrorCode() == 1451;
}
// MySQL 1062: дубль уникального ключа
protected static boolean isDuplicateKey(SQLException e) {
    return e.getErrorCode() == 1062;
}
```

`finally { releaseConnection(connection) }` — соединение возвращается в пул при любом исходе, запросы всегда `try-with-resources`.

### 5. `DAOTrip.complete()` — транзакция

```java
connection.setAutoCommit(false);
// 1) рейс → COMPLETED + дата + состояние авто после рейса
// 2) car.status → то же состояние
connection.commit();      // обе записи атомарно
// при ошибке:
connection.rollback();    // в finally, если failed == true
```

Один рейс и одно состояние автомобиля меняются **одним** коммитом — рассинхрон «рейс выполнен, авто не поставлено в ремонт» невозможен.

### 6. `checkCarFitsRequest()` — бизнес-правило варианта

```java
if (!car.isServiceable()) { ... }                       // авто в ремонте
if (car.getBodyTypeId() != request.getRequiredBodyTypeId()) { ... } // тип кузова
if (car.getLoadKg() < request.getRequiredLoadKg()) { ... }          // грузоподъёмность
```

Проверка выполняется и при назначении авто на рейс (`assignCar`), и при создании рейса с автомобилем (`create`).

### 7. `Runner.java` — логирование

```java
FileHandler fileHandler = new FileHandler("lab3.log", true); // INFO → файл
fileHandler.setFormatter(new SimpleFormatter());
// консоль — только warning и выше
for (Handler handler : Logger.getLogger("").getHandlers()) {
    handler.setLevel(Level.WARNING);
}
```

### 8. `pom.xml` — Java 24 и драйвер MySQL

```xml
<maven.compiler.source>24</maven.compiler.source>
<maven.compiler.target>24</maven.compiler.target>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.7.0</version>
</dependency>
```

---

## Меню приложения

Запуск: `by.bsu.lab3.view.Runner`.

| Пункт | Действие |
|-------|----------|
| `1` | Список рейсов |
| `2` | Рейсы заданного водителя |
| `3` | Автомобили в ремонте |
| `4` | Назначить водителя на рейс |
| `5` | Назначить автомобиль на рейс |
| `6` | Отметить рейс выполненным |
| `7` | Поставить автомобиль на ремонт |
| `8` | Добавить водителя |
| `9` | Добавить автомобиль |
| `10` | Добавить заявку |
| `11` | Создать рейс |
| `12` | Удалить водителя |
| `13` | Удалить автомобиль |
| `14` | Удалить рейс |
| `15` | Добавить заказчика |
| `16` | Добавить маршрут |
| `17` | Добавить тип кузова |
| `0` | Выход |

Ввод: `0`, `-`, `отмена`, `назад` — отмена действия и возврат в меню; даты в формате `ГГГГ-ММ-ДД`.

---

## Бизнес-правила

```java
// авто нельзя назначить в ремонте
if (!car.isServiceable()) throw ...;
// тип кузова должен совпадать с заявкой
if (car.getBodyTypeId() != request.getRequiredBodyTypeId()) throw ...;
// грузоподъёмность не меньше требуемой
if (car.getLoadKg() < request.getRequiredLoadKg()) throw ...;
```

| Правило | Где проверяется |
|---------|-----------------|
| Автомобиль только исправный | `DAOTrip.checkCarFitsRequest()` |
| Тип кузова и грузоподъёмность → заявка | `DAOTrip.checkCarFitsRequest()` |
| Завершённый рейс нельзя изменить | `DAOTrip.requireNotCompleted()` |
| Нельзя завершить рейс без водителя и автомобиля | `DAOTrip.complete()` |
| Нельзя удалить строку, на которую ссылаются другие таблицы | MySQL `1451` → `isForeignKeyViolation()` |
| Нельзя вставить дубль уникального ключа | MySQL `1062` → `isDuplicateKey()` |

---

## Структура проекта

```
test1/
├── pom.xml
├── schema.sql                 -- схема БД + тестовые данные
├── Схема БД.png               -- диаграмма
├── Лаб3_JDBC.doc              -- условие
├── lab3.log                   -- лог работы
└── src/main/
    ├── java/by/bsu/lab3/
    │   ├── connector/
    │   │   ├── ConfigurationManager.java   -- чтение database.properties
    │   │   ├── ConnectionPool.java         -- пул соединений
    │   │   └── JdbcConnector.java          -- DriverManager
    │   ├── dao/
    │   │   ├── DAO.java                    -- абстрактный базовый класс
    │   │   ├── DAOTrip.java
    │   │   ├── DAODriver.java
    │   │   ├── DAOCar.java
    │   │   ├── DAORequest.java
    │   │   ├── DAOCustomer.java
    │   │   ├── DAORoute.java
    │   │   └── DAOBodyType.java
    │   ├── entity/
    │   │   ├── Trip.java, Car.java, Driver.java, Request.java
    │   │   └── Customer.java, Route.java, BodyType.java
    │   ├── exept/
    │   │   ├── DAOException.java
    │   │   └── JDBCConnectionException.java
    │   └── view/
    │       └── Runner.java                  -- меню и точка входа
    └── resources/resources/
        └── database.properties
```

---

## Ключевые файлы

| Файл | Назначение |
|------|-----------|
| `ConnectionPool.java` | пул: `ArrayBlockingQueue`, взятие с таймаутом 10 с, подмена битых соединений |
| `DAO.java` | `takeConnection` / `releaseConnection` / `fail`, коды ошибок MySQL 1451 и 1062 |
| `DAOTrip.java` | рейсы: чтение с `JOIN`, назначение водителя/авто, завершение в транзакции |
| `DAOCar.java` | автомобили: `findInRepair`, `updateStatus`, `delete` |
| `ConfigurationManager.java` | параметры подключения и размер пула |
| `Runner.java` | меню на 17 пунктов, валидация ввода, логирование в `lab3.log` |
| `schema.sql` | 7 таблиц, внешние ключи, тестовые данные |
| `database.properties` | URL, пользователь, пароль, `pool.size` |

---

## Схема базы данных

![Схема БД](<test1/Схема БД.png>)

| Таблица | Назначение | Ключевые поля |
|---------|-----------|---------------|
| `customer` | заказчики | `name` (UNIQUE) |
| `route` | маршруты | `city_from` + `city_to` (UNIQUE) |
| `body_type` | справочник типов кузова | `name` (UNIQUE) |
| `driver` | водители | `license_category`, `phone` |
| `car` | автомобили | `load_kg`, `status` (`SERVICEABLE`/`REPAIR`), FK → `body_type` |
| `request` | заявки заказчиков | требуемый кузов/грузоподъёмность, FK → `customer`, `route` |
| `trip` | рейсы | FK → `request`, `driver`, `car`; `status`, `car_condition_after` |

---

## Соответствие условию

| Требование | Файл / факт |
|------------|-------------|
| База данных MySQL по варианту | `autobase`, 7 таблиц, InnoDB, `utf8mb4` |
| Нормализация 2НФ/3НФ | суррогатные PK, справочники вынесены — `schema.sql` |
| JDBC-подключение | `JdbcConnector` + `ConfigurationManager` |
| Пул соединений | `ConnectionPool`, `pool.size=5` |
| DAO поверх `PreparedStatement` | 7 классов в `dao/` |
| Бизнес-правило назначения авто | `DAOTrip.checkCarFitsRequest()` |
| Транзакции | `DAOTrip.complete()` — `commit`/`rollback` |
| Обработка ошибок | `DAOException`, `JDBCConnectionException` |
| Логирование | `java.util.logging` → `lab3.log`, консоль — WARNING+ |
| Консольный интерфейс | `view/Runner.java` — 17 пунктов меню |

---

## Запуск

**1. Создать базу и залить тестовые данные** (MySQL должен быть запущен):

```bash
mysql -uroot -p < test1/schema.sql
```

**2. Проверить подключение** в `src/main/resources/resources/database.properties`
(URL, `user`, `password`, `pool.size`).

**3. Запуск** — проще всего из **IntelliJ IDEA**: открыть `test1`, запустить `by.bsu.lab3.view.Runner`.

Вариант из консоли (нужен установленный Maven):

```powershell
cd test1
mvn -q compile
mvn -q dependency:build-classpath "-Dmdep.outputFile=cp.txt"
$cp = Get-Content cp.txt -Raw
java -cp "target/classes;$cp" by.bsu.lab3.view.Runner
```

Меню появится в консоли, ошибки и ход работы — в `lab3.log`.

---

## Что сделано

- [x] Создана база `autobase` по варианту 8 (`schema.sql`)
- [x] Диаграмма БД `Схема БД.png`, нормализация 2НФ/3НФ
- [x] Тестовые данные: 5 типов кузова, 4 заказчика, 4 маршрута, 4 водителя, 5 авто, 4 заявки, 4 рейса
- [x] `JdbcConnector` + `ConfigurationManager` (параметры в `database.properties`)
- [x] `ConnectionPool` — пул соединений с подменой битых и таймаутом 10 с
- [x] `DAO` — абстрактный базовый класс с `takeConnection` / `releaseConnection`
- [x] 7 DAO-классов, все запросы через `PreparedStatement`
- [x] Чтение рейсов через `LEFT JOIN` (водитель, авто, маршрут — одним запросом)
- [x] Бизнес-правило: авто исправно, кузов и грузоподъёмность соответствуют заявке
- [x] Транзакция «рейс выполнен» с `rollback` при ошибке
- [x] Удаление с обработкой ошибок FK (`1451`) и дублей (`1062`)
- [x] Консольное меню на 17 пунктов с валидацией ввода и отменой действий
- [x] Логирование `java.util.logging`: INFO → `lab3.log`, консоль → WARNING+

---

## Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---
---

# Laboratory Work №3 — Java, JDBC, MySQL (Variant 8)

Console application **"Car Fleet Dispatcher"** written in Java with MySQL access via JDBC.  
**Variant 8**: Car Database. Trip Management.

The dispatcher assigns customer requests to drivers, each of whom has an assigned vehicle. A trip can only be assigned a car that is in **working condition** and whose characteristics (body type, load capacity) **match the request**. Data is read from and written to **MySQL** through a **connection pool** and a DAO layer built on `PreparedStatement`.

---

## Database

**Project:** `test1` | **Entry point:** `by.bsu.lab3.view.Runner`

| Resource | Value |
|----------|-------|
| Database | `autobase` — MySQL 8, InnoDB, `utf8mb4` |
| Tables | 7: `customer`, `route`, `body_type`, `driver`, `car`, `request`, `trip` |
| Schema + seed data | `test1/schema.sql` |
| Diagram | `test1/Схема БД.png` |
| Connection settings | `src/main/resources/resources/database.properties` |
| Connection pool | 5 (`pool.size=5`), take timeout — 10 s |
| Log file | `lab3.log` (INFO) + console (WARNING+) |
| Task sheet | `test1/Лаб3_JDBC.doc` |

---

## Task

### Variant 8 — Car Database

- Car fleet database: customers, routes, body types, drivers, cars, requests, and trips
- The dispatcher assigns requests to trips between drivers
- Each driver has an assigned vehicle
- A trip may be assigned a car that is in **working condition** and whose characteristics (body type, load capacity) **match the request**
- On completion, the car condition after the trip is recorded

### Implemented

- MySQL schema per the variant: 7 tables, foreign keys, `ENUM` statuses, seed data
- Normalization: **2NF** (single-column surrogate keys), **3NF** (lookup tables `customer`, `route`, `body_type` extracted)
- JDBC connection: `mysql-connector-j` driver, settings in `database.properties`
- **Connection pool** (`ConnectionPool`): singleton, `ArrayBlockingQueue`, reuse, broken-connection replacement
- DAO layer of 7 classes on top of `PreparedStatement` (no string concatenation)
- Business rules for car assignment and trip completion
- Transaction on trip completion (`commit` / `rollback`)
- Console menu of 17 items with input validation
- Custom `DAOException` and `JDBCConnectionException` + `java.util.logging`

### Checklist

- [x] Database schema created and seeded (`schema.sql`)
- [x] 2NF/3NF normalization, diagram `Схема БД.png`
- [x] MySQL connection via `JdbcConnector` + `ConfigurationManager`
- [x] Connection pool `ConnectionPool` (size from properties)
- [x] DAO layer: `DAOTrip`, `DAODriver`, `DAOCar`, `DAORequest`, `DAOCustomer`, `DAORoute`, `DAOBodyType`
- [x] Every query goes through `PreparedStatement`
- [x] "Complete trip" transaction with rollback on failure
- [x] Checks: car serviceable + body type + load capacity
- [x] Logging to `lab3.log`, console — WARNING+ only
- [x] Application runs and works against the database

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" height="28"/>
  <img src="https://img.shields.io/badge/JDBC-00758F?style=for-the-badge&logoColor=white" alt="JDBC" height="28"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" height="28"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" height="28"/>
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA" height="28"/>
  <img src="https://img.shields.io/badge/java.util.logging-2D2D2D?style=for-the-badge&logoColor=white" alt="java.util.logging" height="28"/>
</p>

---

## Architecture

```
Runner (17-item menu, validated input, logging)
  │
  ├── DAOTrip ────┐
  ├── DAODriver   │   entity/: Trip, Car, Driver, Request,
  ├── DAOCar      ├── Customer, Route, BodyType
  ├── DAORequest  │        ▲
  ├── DAOCustomer │   ResultSet ◄──► PreparedStatement
  ├── DAORoute    │
  └── DAOBodyType ┘
        │
        ▼
     DAO (abstract) ── takeConnection() / releaseConnection() / fail()
        │                     │ MySQL codes 1451, 1062
        ▼
  ConnectionPool (singleton, ArrayBlockingQueue, 5 connections)
        │
        ▼
  JdbcConnector ──► MySQL (database autobase)
        ▲
  ConfigurationManager ◄── database.properties

  exept/: DAOException, JDBCConnectionException
```

---

## Key Mechanisms

### 1. `database.properties` + `ConfigurationManager` — connection settings

```properties
url=jdbc:mysql://localhost:3306/autobase?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true
driver=com.mysql.cj.jdbc.Driver
user=root
password=1234
pool.size=5
```

`ConfigurationManager` is a `ResourceBundle` singleton that loads the `resources.database` bundle once. An empty `pool.size` falls back to the default `5`.

### 2. `JdbcConnector.java` — opening a connection

```java
Class.forName(cfg.getDriver());
connection = DriverManager.getConnection(cfg.getURL(), cfg.getUser(), cfg.getPass());
```

The driver is loaded explicitly; if it is missing from the classpath a `JDBCConnectionException` is thrown with the reason logged.

### 3. `ConnectionPool.java` — connection pool

```java
private static final ConnectionPool INSTANCE = new ConnectionPool();
private final Set<Connection> allConnections = ConcurrentHashMap.newKeySet();
private volatile BlockingQueue<Connection> freeConnections;
```

**Taking** (`takeConnection`):

```java
Connection connection = queue.poll(TAKE_TIMEOUT_SECONDS, TimeUnit.SECONDS); // 10 s
if (connection.isClosed()) {                       // broken connection
    allConnections.remove(connection);
    connection = connector.getConnection();        // replaced with a new one
}
```

**Releasing** (`releaseConnection`): `autoCommit` is reset to `true`, a broken connection is dropped and closed instead of being queued. Pool initialization is lazy and thread-safe (`synchronized init()`).

### 4. `DAO.java` — base DAO class

```java
protected Connection takeConnection() throws DAOException { ... }
protected void releaseConnection(Connection connection) { ... }

protected static DAOException fail(String message, SQLException cause) {
    LOGGER.log(Level.SEVERE, message, cause);
    return new DAOException(message + ": " + cause.getMessage(), cause);
}

// MySQL 1451: row is referenced by other tables
protected static boolean isForeignKeyViolation(SQLException e) {
    return e.getErrorCode() == 1451;
}
// MySQL 1062: duplicate unique key
protected static boolean isDuplicateKey(SQLException e) {
    return e.getErrorCode() == 1062;
}
```

`finally { releaseConnection(connection) }` returns the connection to the pool on any path; queries always use try-with-resources.

### 5. `DAOTrip.complete()` — transaction

```java
connection.setAutoCommit(false);
// 1) trip → COMPLETED + completion date + car condition after the trip
// 2) car.status → the same condition
connection.commit();      // both writes atomically
// on failure:
connection.rollback();    // in finally, when failed == true
```

The trip and the car status change in a single commit — a "trip completed but car not sent to repair" desync is impossible.

### 6. `checkCarFitsRequest()` — business rule of the variant

```java
if (!car.isServiceable()) { ... }                          // car is in repair
if (car.getBodyTypeId() != request.getRequiredBodyTypeId()) { ... } // body type
if (car.getLoadKg() < request.getRequiredLoadKg()) { ... }           // load capacity
```

The check runs both when assigning a car to a trip (`assignCar`) and when creating a trip with a car (`create`).

### 7. `Runner.java` — logging

```java
FileHandler fileHandler = new FileHandler("lab3.log", true); // INFO → file
fileHandler.setFormatter(new SimpleFormatter());
// console — warnings and above only
for (Handler handler : Logger.getLogger("").getHandlers()) {
    handler.setLevel(Level.WARNING);
}
```

### 8. `pom.xml` — Java 24 and the MySQL driver

```xml
<maven.compiler.source>24</maven.compiler.source>
<maven.compiler.target>24</maven.compiler.target>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.7.0</version>
</dependency>
```

---

## Application Menu

Entry point: `by.bsu.lab3.view.Runner`.

| Item | Action |
|------|--------|
| `1` | List all trips |
| `2` | Trips of a given driver |
| `3` | Cars in repair |
| `4` | Assign a driver to a trip |
| `5` | Assign a car to a trip |
| `6` | Mark a trip as completed |
| `7` | Send a car to repair |
| `8` | Add a driver |
| `9` | Add a car |
| `10` | Add a request |
| `11` | Create a trip |
| `12` | Delete a driver |
| `13` | Delete a car |
| `14` | Delete a trip |
| `15` | Add a customer |
| `16` | Add a route |
| `17` | Add a body type |
| `0` | Exit |

Input: `0`, `-`, `отмена`, `назад` cancel the action and return to the menu; dates use the `YYYY-MM-DD` format.

---

## Business Rules

```java
// a car in repair can never be assigned
if (!car.isServiceable()) throw ...;
// body type must match the request
if (car.getBodyTypeId() != request.getRequiredBodyTypeId()) throw ...;
// load capacity must be at least the required one
if (car.getLoadKg() < request.getRequiredLoadKg()) throw ...;
```

| Rule | Where it is checked |
|------|---------------------|
| Car must be serviceable | `DAOTrip.checkCarFitsRequest()` |
| Body type and load capacity match the request | `DAOTrip.checkCarFitsRequest()` |
| A completed trip cannot be modified | `DAOTrip.requireNotCompleted()` |
| A trip cannot be completed without driver and car | `DAOTrip.complete()` |
| A row referenced by other tables cannot be deleted | MySQL `1451` → `isForeignKeyViolation()` |
| A duplicate unique key cannot be inserted | MySQL `1062` → `isDuplicateKey()` |

---

## Project Structure

```
test1/
├── pom.xml
├── schema.sql                 -- DB schema + seed data
├── Схема БД.png               -- diagram
├── Лаб3_JDBC.doc              -- task sheet
├── lab3.log                   -- runtime log
└── src/main/
    ├── java/by/bsu/lab3/
    │   ├── connector/
    │   │   ├── ConfigurationManager.java   -- reads database.properties
    │   │   ├── ConnectionPool.java         -- connection pool
    │   │   └── JdbcConnector.java          -- DriverManager
    │   ├── dao/
    │   │   ├── DAO.java                    -- abstract base class
    │   │   ├── DAOTrip.java
    │   │   ├── DAODriver.java
    │   │   ├── DAOCar.java
    │   │   ├── DAORequest.java
    │   │   ├── DAOCustomer.java
    │   │   ├── DAORoute.java
    │   │   └── DAOBodyType.java
    │   ├── entity/
    │   │   ├── Trip.java, Car.java, Driver.java, Request.java
    │   │   └── Customer.java, Route.java, BodyType.java
    │   ├── exept/
    │   │   ├── DAOException.java
    │   │   └── JDBCConnectionException.java
    │   └── view/
    │       └── Runner.java                  -- menu and entry point
    └── resources/resources/
        └── database.properties
```

---

## Key Files

| File | Purpose |
|------|---------|
| `ConnectionPool.java` | pool: `ArrayBlockingQueue`, 10 s take timeout, broken-connection replacement |
| `DAO.java` | `takeConnection` / `releaseConnection` / `fail`, MySQL error codes 1451 and 1062 |
| `DAOTrip.java` | trips: `JOIN` reads, driver/car assignment, completion inside a transaction |
| `DAOCar.java` | cars: `findInRepair`, `updateStatus`, `delete` |
| `ConfigurationManager.java` | connection settings and pool size |
| `Runner.java` | 17-item menu, input validation, logging to `lab3.log` |
| `schema.sql` | 7 tables, foreign keys, seed data |
| `database.properties` | URL, user, password, `pool.size` |

---

## Database Schema

![Database schema](<test1/Схема БД.png>)

| Table | Purpose | Key fields |
|-------|---------|------------|
| `customer` | customers | `name` (UNIQUE) |
| `route` | routes | `city_from` + `city_to` (UNIQUE) |
| `body_type` | body type lookup | `name` (UNIQUE) |
| `driver` | drivers | `license_category`, `phone` |
| `car` | cars | `load_kg`, `status` (`SERVICEABLE`/`REPAIR`), FK → `body_type` |
| `request` | customer requests | required body type / load, FK → `customer`, `route` |
| `trip` | trips | FK → `request`, `driver`, `car`; `status`, `car_condition_after` |

---

## Task Compliance

| Requirement | Implementation |
|-------------|----------------|
| MySQL database per the variant | `autobase`, 7 tables, InnoDB, `utf8mb4` |
| 2NF/3NF normalization | surrogate PKs, extracted lookups — `schema.sql` |
| JDBC connection | `JdbcConnector` + `ConfigurationManager` |
| Connection pool | `ConnectionPool`, `pool.size=5` |
| DAO on `PreparedStatement` | 7 classes in `dao/` |
| Car assignment business rule | `DAOTrip.checkCarFitsRequest()` |
| Transactions | `DAOTrip.complete()` — `commit`/`rollback` |
| Error handling | `DAOException`, `JDBCConnectionException` |
| Logging | `java.util.logging` → `lab3.log`, console — WARNING+ |
| Console interface | `view/Runner.java` — 17-item menu |

---

## Run

**1. Create the database and load seed data** (MySQL must be running):

```bash
mysql -uroot -p < test1/schema.sql
```

**2. Check the connection settings** in
`src/main/resources/resources/database.properties` (URL, `user`, `password`, `pool.size`).

**3. Run** — the easiest way is **IntelliJ IDEA**: open `test1` and run `by.bsu.lab3.view.Runner`.

From the console (requires Maven):

```powershell
cd test1
mvn -q compile
mvn -q dependency:build-classpath "-Dmdep.outputFile=cp.txt"
$cp = Get-Content cp.txt -Raw
java -cp "target/classes;$cp" by.bsu.lab3.view.Runner
```

The menu appears in the console; errors and the run trace go to `lab3.log`.

---

## Done

- [x] Created the `autobase` database per variant 8 (`schema.sql`)
- [x] Database diagram `Схема БД.png`, 2NF/3NF normalization
- [x] Seed data: 5 body types, 4 customers, 4 routes, 4 drivers, 5 cars, 4 requests, 4 trips
- [x] `JdbcConnector` + `ConfigurationManager` (settings in `database.properties`)
- [x] `ConnectionPool` with broken-connection replacement and a 10 s timeout
- [x] `DAO` — abstract base class with `takeConnection` / `releaseConnection`
- [x] 7 DAO classes, every query via `PreparedStatement`
- [x] Trip reads via `LEFT JOIN` (driver, car, route in a single query)
- [x] Business rule: car serviceable, body type and load capacity match the request
- [x] "Trip completed" transaction with `rollback` on failure
- [x] Deletion with FK (`1451`) and duplicate (`1062`) error handling
- [x] Console menu of 17 items with input validation and action cancellation
- [x] `java.util.logging`: INFO → `lab3.log`, console → WARNING+

---

## Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com
