# Лабораторная работа №4 — Java, JPA (EclipseLink), MS SQL Server (вариант 8)

Консольное приложение **«Диспетчерская автобазы»** на Java с доступом к MS SQL Server через **Java Persistence API**.  
**Вариант 8**: Автобаза. Управление рейсами.

Диспетчер распределяет заявки на рейсы между водителями, за каждым из которых закреплён свой автомобиль. На рейс назначается автомобиль, находящийся в исправном состоянии и характеристики которого соответствуют заявке. Данные читаются и пишутся через **JPA** (EclipseLink): **часть 1** — JPA-QL с `@NamedQueries`, **часть 2** — динамические типобезопасные запросы **Criteria API + Metamodel API**.

---

## База данных

**Проект:** `Лабораторная работа №4` | **Точка входа:** `by.bsu.lab4.view.Runner`

| Ресурс | Значение |
|--------|----------|
| База данных | `autobase` — MS SQL Server 2019, T-SQL |
| Таблиц | 7: `customer`, `route`, `body_type`, `driver`, `car`, `request`, `trip` |
| Схема + тестовые данные | `schema.sql` |
| Подключение | `src/main/resources/META-INF/persistence.xml` |
| Провайдер JPA | EclipseLink 5.0.0-B01 |
| Драйвер БД | `mssql-jdbc` 12.8.1 |
| Лог-файл | `lab4.log` (INFO) + консоль (WARNING+) |
| Условие | `../Лаб4_JPA.doc` |

---

## Условие

### Вариант 8 — Автобаза

- База данных автобазы: заказчики, маршруты, типы кузова, водители, автомобили, заявки и рейсы
- Диспетчер распределяет заявки на рейсы между водителями
- У каждого водителя закреплён свой автомобиль
- На рейс может быть назначен автомобиль, находящийся в **исправном состоянии** и характеристики которого (тип кузова, грузоподъёмность) **соответствуют заявке**
- По завершении рейса фиксируется состояние автомобиля после рейса

### Часть 1

1. На основе предметной области лабораторной работы 3 определить совокупность сущностей и связей между ними
2. Реализовать эти сущности с помощью **Java Persistence API**
3. Реализовать запросы (из лабораторной работы 3) с помощью **JPA-QL** — аннотации `@NamedQueries` / `@NamedQuery` в классах сущностей
4. Слой DAO сохранён, но модифицирован для использования JPA
5. Взаимодействие с пользователем — в консольном виде

### Часть 2

1. Заменить статические запросы части 1 на динамические типобезопасные запросы с использованием **Criteria API** и **Metamodel API**
2. Проработать обработку исключений, подключить логгер

### Что реализовано

- Схема базы **MS SQL Server** (T-SQL): 7 таблиц, `IDENTITY`, внешние ключи, `CHECK`-ограничения, тестовые данные
- **7 JPA-сущностей** со связями `@ManyToOne`: Car→BodyType, Request→Customer/Route/BodyType, Trip→Request/Driver/Car
- **16 `@NamedQuery`** на 7 сущностях (часть 1): `findAll`, `findById`, `findByDriver`, `findInRepair`
- **7 статических метамоделей** (`Trip_`, `Car_`, …) + **Criteria API** во всех DAO (часть 2)
- Слой DAO сохранён: те же 7 классов и методы, `EntityManager` вместо JDBC
- Обработка исключений: `DAOException` с контекстом, rollback транзакций
- Логгер **SLF4J + Logback**: INFO → `lab4.log`, WARNING+ → консоль
- Валидация при создании рейса: запрет ID `0`, проверка дубликата с показом существующего рейса
- Консольное меню на 17 пунктов с валидацией ввода

### Чек-лист

- [x] Схема БД MS SQL Server создана и залита тестовыми данными (`schema.sql`)
- [x] 7 JPA-сущностей со связями `@ManyToOne` / `@JoinColumn`
- [x] `@NamedQueries` / `@NamedQuery` на всех сущностях (JPA-QL)
- [x] 7 статических метамоделей `@StaticMetamodel`
- [x] DAO-слой: `DAOTrip`, `DAODriver`, `DAOCar`, `DAORequest`, `DAOCustomer`, `DAORoute`, `DAOBodyType`
- [x] Все SELECT-запросы — через Criteria API + Metamodel (типобезопасные)
- [x] Транзакция «завершение рейса» с откатом при ошибке
- [x] Проверка: авто исправно + тип кузова + грузоподъёмность
- [x] Запрет ID рейса `0`, проверка дубликата с показом рейса
- [x] Логирование SLF4J/Logback в `lab4.log`, консоль — WARNING+
- [x] Приложение запускается и работает с базой

---

## Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" height="28"/>
  <img src="https://img.shields.io/badge/JPA-00758F?style=for-the-badge&logoColor=white" alt="JPA" height="28"/>
  <img src="https://img.shields.io/badge/EclipseLink-FF7A00?style=for-the-badge&logoColor=white" alt="EclipseLink" height="28"/>
  <img src="https://img.shields.io/badge/MS_SQL_Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" alt="MS SQL Server" height="28"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" height="28"/>
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA" height="28"/>
  <img src="https://img.shields.io/badge/SLF4J-2D2D2D?style=for-the-badge&logoColor=white" alt="SLF4J" height="28"/>
</p>

---

## Архитектура

```
Runner (меню на 17 пунктов, ввод с валидацией, логирование)
  │
  ├── DAOTrip ────┐
  ├── DAODriver   │   entity/: Trip, Car, Driver, Request,
  ├── DAOCar      ├── Customer, Route, BodyType
  ├── DAORequest  │        ▲ @ManyToOne / @JoinColumn
  ├── DAOCustomer │   CriteriaQuery ◄──► EntityManager
  ├── DAORoute    │        ▲
  └── DAOBodyType ┘   Trip_, Car_, Driver_, …  (метамодель)
        │
        ▼
   JPAUtil ── EntityManagerFactory (persistence-unit "autobasePU")
        │
        ▼
   EclipseLink ──► MS SQL Server (база autobase)
        ▲
   persistence.xml ◄── JDBC URL, user, password

   entity/: @NamedQueries / @NamedQuery (JPA-QL, часть 1)
   exept/: DAOException
```

---

## Ключевые механизмы

### 1. `persistence.xml` — единица персистентности

```xml
<persistence-unit name="autobasePU" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>by.bsu.lab4.entity.Trip</class>
    <!-- … все 7 сущностей … -->
    <properties>
        <property name="jakarta.persistence.jdbc.url"
                  value="jdbc:sqlserver://localhost:1433;databaseName=autobase;..."/>
        <property name="jakarta.persistence.jdbc.user" value="sa"/>
        <property name="eclipselink.target-database" value="SQLServer"/>
        <property name="eclipselink.ddl-generation" value="none"/>
    </properties>
</persistence-unit>
```

Схему создаёт `schema.sql`; JPA не трогает DDL. `eclipselink.target-database` — явный диалект SQL Server.

### 2. `Trip.java` — JPA-сущность со связями

```java
@Entity
@Table(name = "trip")
@NamedQueries({
    @NamedQuery(name = "Trip.findAll",
            query = "SELECT t FROM Trip t ORDER BY t.id"),
    @NamedQuery(name = "Trip.findByDriver",
            query = "SELECT t FROM Trip t WHERE t.driver.id = :driverId ORDER BY t.id")
})
public class Trip {
    @Id
    @Column(name = "trip_id")
    private int id;                            // id задаётся вручную

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;                     // nullable

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;                           // nullable
}
```

Связи `@ManyToOne` заменяют плоские FK из лабы 3: `trip.getRequest().getRoute().getCityFrom()` вместо JOIN-выборки в ResultSet.

### 3. Метамодель — `Trip_.java`

```java
@StaticMetamodel(Trip.class)
public abstract class Trip_ {
    public static volatile SingularAttribute<Trip, Integer> id;
    public static volatile SingularAttribute<Trip, Request> request;
    public static volatile SingularAttribute<Trip, Driver> driver;
    public static volatile SingularAttribute<Trip, Car> car;
    public static volatile SingularAttribute<Trip, String> status;
}
```

Классы `BodyType_`, `Car_`, `Customer_`, `Driver_`, `Request_`, `Route_`, `Trip_` — 7 метамоделей. Строка `"id"` заменена на `Trip_.id`: опечатка не скомпилируется.

### 4. Criteria API + Metamodel — запрос в `DAOTrip.findAll()`

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
Root<Trip> root = cq.from(Trip.class);
root.fetch(Trip_.request).fetch(Request_.route);
root.fetch(Trip_.driver, JoinType.LEFT);
root.fetch(Trip_.car, JoinType.LEFT);
cq.select(root).orderBy(cb.asc(root.get(Trip_.id)));
List<Trip> list = em.createQuery(cq).getResultList();
```

Все SELECT-запросы 7 DAO построены через `CriteriaBuilder` / `CriteriaQuery` с атрибутами метамодели. Строковых JPQL-запросов в коде нет.

### 5. `DAOTrip.complete()` — транзакция

```java
tx.begin();
Trip managed = em.find(Trip.class, tripId);
managed.setStatus(Trip.STATUS_COMPLETED);
managed.setCompletionDate(LocalDate.now());
managed.setCarConditionAfter(carCondition);
Car car = em.find(Car.class, trip.getCar().getId());
car.setStatus(carCondition);            // состояние авто — тем же коммитом
tx.commit();
// при ошибке:
tx.rollback();
```

Один рейс и одно состояние автомобиля меняются **одним** коммитом — рассинхрон невозможен.

### 6. `checkCarFitsRequest()` — бизнес-правило варианта

```java
if (!car.isServiceable()) { ... }                                   // авто в ремонте
if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) { ... } // тип кузова
if (car.getLoadKg() < request.getRequiredLoadKg()) { ... }          // грузоподъёмность
```

Проверка выполняется и при назначении авто на рейс (`assignCar`), и при создании рейса с автомобилем (`create`).

### 7. `Runner.createTrip()` — валидация ID рейса

```java
int tripId = readTripId("Введите id нового рейса: ");  // 0 → ошибка, не отмена
Trip existing = tripDao.findById(tripId);
if (existing != null) {
    System.err.println("Ошибка: рейс с id = " + tripId + " уже существует.");
    printTrips(List.of(existing));                     // показ существующего рейса
    return;
}
```

`readTripId()` — отдельный метод ввода: `0` и отрицательные → конкретная ошибка (не «отмена»); `-`, `отмена`, `назад` → отмена действия.

### 8. `logback.xml` — логгер

```xml
<!-- INFO и выше → файл lab4.log -->
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>lab4.log</file>
</appender>
<!-- WARNING и выше → консоль -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>WARN</level>
    </filter>
</appender>
```

71 лог-вызов (`debug` / `info` / `warn` / `error`) во всех DAO и Runner.

### 9. `pom.xml` — Java 17, EclipseLink, mssql-jdbc

```xml
<maven.compiler.source>17</maven.compiler.source>

<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.8.1.jre11</version>
</dependency>
<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink</artifactId>
    <version>5.0.0-B01</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.16</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.12</version>
</dependency>
```

---

## Меню приложения

Запуск: `by.bsu.lab4.view.Runner`.

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
| `11` | Создать рейс (с вводом id, запрет 0, проверка дубликата) |
| `12` | Удалить водителя |
| `13` | Удалить автомобиль |
| `14` | Удалить рейс |
| `15` | Добавить заказчика |
| `16` | Добавить маршрут |
| `17` | Добавить тип кузова |
| `0` | Выход |

Ввод: `0`, `-`, `отмена`, `назад` — отмена действия и возврат в меню; даты в формате `ГГГГ-ММ-ДД`.  
При создании рейса (пункт `11`) ввод id рейса — отдельный метод `readTripId()`: `0` → ошибка (не отмена), дубликат → ошибка + показ рейса.

---

## Бизнес-правила

```java
// авто нельзя назначить в ремонте
if (!car.isServiceable()) throw ...;
// тип кузова должен совпадать с заявкой
if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) throw ...;
// грузоподъёмность не меньше требуемой
if (car.getLoadKg() < request.getRequiredLoadKg()) throw ...;
```

| Правило | Где проверяется |
|---------|-----------------|
| Автомобиль только исправный | `DAOTrip.checkCarFitsRequest()` |
| Тип кузова и грузоподъёмность → заявка | `DAOTrip.checkCarFitsRequest()` |
| Завершённый рейс нельзя изменить | `DAOTrip.requireNotCompleted()` |
| Нельзя завершить рейс без водителя и автомобиля | `DAOTrip.complete()` |
| ID рейса ≠ 0 | `DAOTrip.create()` + `Runner.readTripId()` |
| ID рейса не занят | `Runner.createTrip()` → показ существующего рейса |

---

## Структура проекта

```
Лабораторная работа №4/
├── pom.xml
├── schema.sql                 -- схема БД (T-SQL) + тестовые данные
├── lab4.log                   -- лог работы
└── src/main/
    ├── java/by/bsu/lab4/
    │   ├── dao/
    │   │   ├── JPAUtil.java                    -- EntityManagerFactory
    │   │   ├── DAOTrip.java
    │   │   ├── DAODriver.java
    │   │   ├── DAOCar.java
    │   │   ├── DAORequest.java
    │   │   ├── DAOCustomer.java
    │   │   ├── DAORoute.java
    │   │   └── DAOBodyType.java
    │   ├── entity/
    │   │   ├── Trip.java, Car.java, Driver.java, Request.java
    │   │   ├── Customer.java, Route.java, BodyType.java
    │   │   └── Trip_.java, Car_.java, Driver_.java, Request_.java
    │   │       Customer_.java, Route_.java, BodyType_.java   -- метамодель
    │   ├── exept/
    │   │   └── DAOException.java
    │   └── view/
    │       └── Runner.java                      -- меню и точка входа
    └── resources/
        ├── META-INF/
        │   └── persistence.xml                  -- единица персистентности
        └── logback.xml                          -- конфигурация логгера
```

---

## Ключевые файлы

| Файл | Назначение |
|------|-----------|
| `persistence.xml` | JDBC URL, user/password, диалект SQL Server, DDL=`none` |
| `JPAUtil.java` | синглтон `EntityManagerFactory`, ленивая инициализация |
| `Trip.java` | сущность: `@NamedQueries`, `@ManyToOne` → Request/Driver/Car |
| `Trip_.java` | метамодель: `SingularAttribute` для типобезопасного Criteria API |
| `DAOTrip.java` | рейсы: чтение с `fetch`, назначение, завершение в транзакции, валидация ID |
| `DAOCar.java` | автомобили: `findInRepair`, `updateStatus`, `delete` |
| `DAOException.java` | исключение DAO с сообщением и причиной |
| `Runner.java` | меню на 17 пунктов, `readTripId()`, логирование в `lab4.log` |
| `schema.sql` | 7 таблиц (T-SQL), внешние ключи, `CHECK`, тестовые данные |
| `logback.xml` | INFO → `lab4.log`, WARNING+ → консоль |

---

## Схема базы данных

| Таблица | Назначение | Ключевые поля |
|---------|-----------|---------------|
| `customer` | заказчики | `name` (UNIQUE) |
| `route` | маршруты | `city_from` + `city_to` (UNIQUE) |
| `body_type` | справочник типов кузова | `name` (UNIQUE) |
| `driver` | водители | `license_category`, `phone` |
| `car` | автомобили | `load_kg`, `status` (`SERVICEABLE`/`REPAIR`), FK → `body_type` |
| `request` | заявки заказчиков | требуемый кузов/грузоподъёмность, FK → `customer`, `route`, `body_type` |
| `trip` | рейсы | FK → `request`, `driver`, `car`; `status`, `car_condition_after` |

---

## Соответствие условию

| Требование | Файл / факт |
|------------|-------------|
| **Часть 1** — сущности и связи | 7 `@Entity` с `@ManyToOne` / `@JoinColumn` |
| JPA-сущности | `entity/`: Trip, Car, Driver, Request, Customer, Route, BodyType |
| JPA-QL + `@NamedQueries` / `@NamedQuery` | 16 аннотаций на 7 сущностях |
| DAO сохранён, модифицирован под JPA | 7 классов в `dao/`, `EntityManager` вместо JDBC |
| Консольный интерфейс | `view/Runner.java` — 17 пунктов меню |
| **Часть 2** — Criteria API + Metamodel | все SELECT в DAO через `CriteriaBuilder` |
| Metamodel API | 7 классов `@StaticMetamodel` (`Trip_`, `Car_`, …) |
| Типобезопасность | `root.get(Trip_.id)` вместо `root.get("id")` |
| Обработка исключений | `DAOException`, rollback транзакций |
| Логгер | SLF4J + Logback → `lab4.log`, консоль — WARNING+ |

---

## Запуск

**1. Создать базу и залить тестовые данные** (SQL Server должен быть запущен):

```powershell
# выполнить schema.sql в SSMS либо через PowerShell:
$sql = Get-Content schema.sql -Raw -Encoding UTF8
$batches = $sql -split '(?m)^GO\s*$'
$conn = New-Object System.Data.SqlClient.SqlConnection(
    "Server=localhost;Database=master;Integrated Security=True;TrustServerCertificate=True")
$conn.Open()
foreach ($batch in $batches) {
    if ($batch.Trim()) {
        $cmd = $conn.CreateCommand(); $cmd.CommandText = $batch
        [void]$cmd.ExecuteNonQuery()
    }
}
$conn.Close()
```

**2. Проверить подключение** в `src/main/resources/META-INF/persistence.xml`
(URL, `user`, `password`). По умолючению: `sa` / `1234`, порт `1433`.

**3. Запуск** — проще всего из **IntelliJ IDEA**: открыть проект, запустить `by.bsu.lab4.view.Runner`.

Вариант из консоли (нужен установленный Maven):

```powershell
mvn -q compile exec:java
```

Меню появится в консоли, ошибки и ход работы — в `lab4.log`.

---

## Что сделано

- [x] Создана база `autobase` MS SQL Server по варианту 8 (`schema.sql`, T-SQL)
- [x] Тестовые данные: 5 типов кузова, 4 заказчика, 4 маршрута, 4 водителя, 5 авто, 4 заявки, 4 рейса
- [x] 7 JPA-сущностей со связями `@ManyToOne` / `@JoinColumn`
- [x] `@NamedQueries` / `@NamedQuery` — JPA-QL на всех сущностях (часть 1)
- [x] 7 статических метамоделей `@StaticMetamodel` (часть 2)
- [x] `JPAUtil` — синглтон `EntityManagerFactory`
- [x] 7 DAO-классов: методы сохранены, реализация на Criteria API + Metamodel
- [x] Чтение рейсов через `fetch` (маршрут, авто, водитель — одним запросом)
- [x] Бизнес-правило: авто исправно, кузов и грузоподъёмность соответствуют заявке
- [x] Транзакция «рейс выполнен» с `rollback` при ошибке
- [x] Валидация ID рейса: запрет `0`, проверка дубликата с показом рейса
- [x] Консольное меню на 17 пунктов с валидацией ввода и отменой действий
- [x] Логирование SLF4J/Logback: INFO → `lab4.log`, консоль → WARNING+

---

## Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---
---

# Laboratory Work №4 — Java, JPA (EclipseLink), MS SQL Server (Variant 8)

Console application **"Car Fleet Dispatcher"** written in Java with MS SQL Server access via the **Java Persistence API**.  
**Variant 8**: Car Database. Trip Management.

The dispatcher assigns customer requests to drivers, each of whom has an assigned vehicle. A trip can only be assigned a car that is in **working condition** and whose characteristics (body type, load capacity) **match the request**. Data is read and written through **JPA** (EclipseLink): **Part 1** — JPA-QL with `@NamedQueries`, **Part 2** — dynamic type-safe queries using **Criteria API + Metamodel API**.

---

## Database

**Project:** `Лабораторная работа №4` | **Entry point:** `by.bsu.lab4.view.Runner`

| Resource | Value |
|----------|-------|
| Database | `autobase` — MS SQL Server 2019, T-SQL |
| Tables | 7: `customer`, `route`, `body_type`, `driver`, `car`, `request`, `trip` |
| Schema + seed data | `schema.sql` |
| Connection settings | `src/main/resources/META-INF/persistence.xml` |
| JPA provider | EclipseLink 5.0.0-B01 |
| DB driver | `mssql-jdbc` 12.8.1 |
| Log file | `lab4.log` (INFO) + console (WARNING+) |
| Task sheet | `../Лаб4_JPA.doc` |

---

## Task

### Variant 8 — Car Database

- Car fleet database: customers, routes, body types, drivers, cars, requests, and trips
- The dispatcher assigns requests to trips between drivers
- Each driver has an assigned vehicle
- A trip may be assigned a car that is in **working condition** and whose characteristics (body type, load capacity) **match the request**
- On completion, the car condition after the trip is recorded

### Part 1

1. Determine the set of entities and relationships based on the lab 3 subject domain
2. Implement these entities using **Java Persistence API**
3. Implement the queries (from lab 3) using **JPA-QL** — `@NamedQueries` / `@NamedQuery` annotations in entity classes
4. The DAO layer is preserved but modified to use JPA
5. User interaction is console-based

### Part 2

1. Replace static queries from Part 1 with dynamic type-safe queries using **Criteria API** and **Metamodel API**
2. Work on exception handling, connect a logger

### Implemented

- **MS SQL Server** schema (T-SQL): 7 tables, `IDENTITY`, foreign keys, `CHECK` constraints, seed data
- **7 JPA entities** with `@ManyToOne` relationships: Car→BodyType, Request→Customer/Route/BodyType, Trip→Request/Driver/Car
- **16 `@NamedQuery`** on 7 entities (Part 1): `findAll`, `findById`, `findByDriver`, `findInRepair`
- **7 static metamodels** (`Trip_`, `Car_`, …) + **Criteria API** in all DAOs (Part 2)
- DAO layer preserved: same 7 classes and methods, `EntityManager` instead of JDBC
- Exception handling: `DAOException` with context, transaction rollback
- Logger **SLF4J + Logback**: INFO → `lab4.log`, WARNING+ → console
- Trip creation validation: rejects ID `0`, duplicate check with existing trip display
- Console menu of 17 items with input validation

### Checklist

- [x] MS SQL Server schema created and seeded (`schema.sql`)
- [x] 7 JPA entities with `@ManyToOne` / `@JoinColumn` relationships
- [x] `@NamedQueries` / `@NamedQuery` on all entities (JPA-QL)
- [x] 7 static metamodels `@StaticMetamodel`
- [x] DAO layer: `DAOTrip`, `DAODriver`, `DAOCar`, `DAORequest`, `DAOCustomer`, `DAORoute`, `DAOBodyType`
- [x] All SELECT queries via Criteria API + Metamodel (type-safe)
- [x] "Complete trip" transaction with rollback on failure
- [x] Checks: car serviceable + body type + load capacity
- [x] Trip ID `0` rejected, duplicate check with trip display
- [x] Console menu of 17 items with input validation and action cancellation
- [x] SLF4J/Logback logging to `lab4.log`, console — WARNING+ only
- [x] Application runs and works against the database

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" height="28"/>
  <img src="https://img.shields.io/badge/JPA-00758F?style=for-the-badge&logoColor=white" alt="JPA" height="28"/>
  <img src="https://img.shields.io/badge/EclipseLink-FF7A00?style=for-the-badge&logoColor=white" alt="EclipseLink" height="28"/>
  <img src="https://img.shields.io/badge/MS_SQL_Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" alt="MS SQL Server" height="28"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" height="28"/>
  <img src="https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white" alt="IntelliJ IDEA" height="28"/>
  <img src="https://img.shields.io/badge/SLF4J-2D2D2D?style=for-the-badge&logoColor=white" alt="SLF4J" height="28"/>
</p>

---

## Architecture

```
Runner (17-item menu, validated input, logging)
  │
  ├── DAOTrip ────┐
  ├── DAODriver   │   entity/: Trip, Car, Driver, Request,
  ├── DAOCar      ├── Customer, Route, BodyType
  ├── DAORequest  │        ▲ @ManyToOne / @JoinColumn
  ├── DAOCustomer │   CriteriaQuery ◄──► EntityManager
  ├── DAORoute    │        ▲
  └── DAOBodyType ┘   Trip_, Car_, Driver_, …  (metamodel)
        │
        ▼
   JPAUtil ── EntityManagerFactory (persistence-unit "autobasePU")
        │
        ▼
   EclipseLink ──► MS SQL Server (database autobase)
        ▲
   persistence.xml ◄── JDBC URL, user, password

   entity/: @NamedQueries / @NamedQuery (JPA-QL, Part 1)
   exept/: DAOException
```

---

## Key Mechanisms

### 1. `persistence.xml` — persistence unit

```xml
<persistence-unit name="autobasePU" transaction-type="RESOURCE_LOCAL">
    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>
    <class>by.bsu.lab4.entity.Trip</class>
    <!-- … all 7 entities … -->
    <properties>
        <property name="jakarta.persistence.jdbc.url"
                  value="jdbc:sqlserver://localhost:1433;databaseName=autobase;..."/>
        <property name="jakarta.persistence.jdbc.user" value="sa"/>
        <property name="eclipselink.target-database" value="SQLServer"/>
        <property name="eclipselink.ddl-generation" value="none"/>
    </properties>
</persistence-unit>
```

The schema is created by `schema.sql`; JPA does not touch DDL. `eclipselink.target-database` sets the explicit SQL Server dialect.

### 2. `Trip.java` — JPA entity with relationships

```java
@Entity
@Table(name = "trip")
@NamedQueries({
    @NamedQuery(name = "Trip.findAll",
            query = "SELECT t FROM Trip t ORDER BY t.id"),
    @NamedQuery(name = "Trip.findByDriver",
            query = "SELECT t FROM Trip t WHERE t.driver.id = :driverId ORDER BY t.id")
})
public class Trip {
    @Id
    @Column(name = "trip_id")
    private int id;                            // set manually

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;                     // nullable

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;                           // nullable
}
```

`@ManyToOne` relationships replace flat FKs from lab 3: `trip.getRequest().getRoute().getCityFrom()` instead of JOIN-based ResultSet extraction.

### 3. Metamodel — `Trip_.java`

```java
@StaticMetamodel(Trip.class)
public abstract class Trip_ {
    public static volatile SingularAttribute<Trip, Integer> id;
    public static volatile SingularAttribute<Trip, Request> request;
    public static volatile SingularAttribute<Trip, Driver> driver;
    public static volatile SingularAttribute<Trip, Car> car;
    public static volatile SingularAttribute<Trip, String> status;
}
```

Classes `BodyType_`, `Car_`, `Customer_`, `Driver_`, `Request_`, `Route_`, `Trip_` — 7 metamodels. The string `"id"` is replaced by `Trip_.id`: a typo fails compilation.

### 4. Criteria API + Metamodel — query in `DAOTrip.findAll()`

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Trip> cq = cb.createQuery(Trip.class);
Root<Trip> root = cq.from(Trip.class);
root.fetch(Trip_.request).fetch(Request_.route);
root.fetch(Trip_.driver, JoinType.LEFT);
root.fetch(Trip_.car, JoinType.LEFT);
cq.select(root).orderBy(cb.asc(root.get(Trip_.id)));
List<Trip> list = em.createQuery(cq).getResultList();
```

All SELECT queries in 7 DAOs are built via `CriteriaBuilder` / `CriteriaQuery` with metamodel attributes. No string-based JPQL in the code.

### 5. `DAOTrip.complete()` — transaction

```java
tx.begin();
Trip managed = em.find(Trip.class, tripId);
managed.setStatus(Trip.STATUS_COMPLETED);
managed.setCompletionDate(LocalDate.now());
managed.setCarConditionAfter(carCondition);
Car car = em.find(Car.class, trip.getCar().getId());
car.setStatus(carCondition);            // car status in the same commit
tx.commit();
// on failure:
tx.rollback();
```

The trip and the car status change in a single commit — a desync is impossible.

### 6. `checkCarFitsRequest()` — business rule of the variant

```java
if (!car.isServiceable()) { ... }                                           // car in repair
if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) { ... } // body type
if (car.getLoadKg() < request.getRequiredLoadKg()) { ... }                  // load capacity
```

The check runs both when assigning a car to a trip (`assignCar`) and when creating a trip with a car (`create`).

### 7. `Runner.createTrip()` — trip ID validation

```java
int tripId = readTripId("Введите id нового рейса: ");  // 0 → error, not cancel
Trip existing = tripDao.findById(tripId);
if (existing != null) {
    System.err.println("Error: trip with id = " + tripId + " already exists.");
    printTrips(List.of(existing));                     // display existing trip
    return;
}
```

`readTripId()` — a dedicated input method: `0` and negatives → specific error (not "cancel"); `-`, `отмена`, `назад` → cancel the action.

### 8. `logback.xml` — logger

```xml
<!-- INFO and above → file lab4.log -->
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>lab4.log</file>
</appender>
<!-- WARNING and above → console -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>WARN</level>
    </filter>
</appender>
```

71 log calls (`debug` / `info` / `warn` / `error`) across all DAOs and Runner.

### 9. `pom.xml` — Java 17, EclipseLink, mssql-jdbc

```xml
<maven.compiler.source>17</maven.compiler.source>

<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.8.1.jre11</version>
</dependency>
<dependency>
    <groupId>org.eclipse.persistence</groupId>
    <artifactId>eclipselink</artifactId>
    <version>5.0.0-B01</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.16</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.5.12</version>
</dependency>
```

---

## Application Menu

Entry point: `by.bsu.lab4.view.Runner`.

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
| `11` | Create a trip (enter ID, reject 0, check duplicates) |
| `12` | Delete a driver |
| `13` | Delete a car |
| `14` | Delete a trip |
| `15` | Add a customer |
| `16` | Add a route |
| `17` | Add a body type |
| `0` | Exit |

Input: `0`, `-`, `отмена`, `назад` cancel the action and return to the menu; dates use the `YYYY-MM-DD` format.  
When creating a trip (item `11`), the trip ID input uses a dedicated `readTripId()` method: `0` → error (not cancel), duplicate → error + trip display.

---

## Business Rules

```java
// a car in repair can never be assigned
if (!car.isServiceable()) throw ...;
// body type must match the request
if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) throw ...;
// load capacity must be at least the required one
if (car.getLoadKg() < request.getRequiredLoadKg()) throw ...;
```

| Rule | Where it is checked |
|------|---------------------|
| Car must be serviceable | `DAOTrip.checkCarFitsRequest()` |
| Body type and load capacity match the request | `DAOTrip.checkCarFitsRequest()` |
| A completed trip cannot be modified | `DAOTrip.requireNotCompleted()` |
| A trip cannot be completed without driver and car | `DAOTrip.complete()` |
| Trip ID ≠ 0 | `DAOTrip.create()` + `Runner.readTripId()` |
| Trip ID must not be taken | `Runner.createTrip()` → display existing trip |

---

## Project Structure

```
Лабораторная работа №4/
├── pom.xml
├── schema.sql                 -- DB schema (T-SQL) + seed data
├── lab4.log                   -- runtime log
└── src/main/
    ├── java/by/bsu/lab4/
    │   ├── dao/
    │   │   ├── JPAUtil.java                    -- EntityManagerFactory
    │   │   ├── DAOTrip.java
    │   │   ├── DAODriver.java
    │   │   ├── DAOCar.java
    │   │   ├── DAORequest.java
    │   │   ├── DAOCustomer.java
    │   │   ├── DAORoute.java
    │   │   └── DAOBodyType.java
    │   ├── entity/
    │   │   ├── Trip.java, Car.java, Driver.java, Request.java
    │   │   ├── Customer.java, Route.java, BodyType.java
    │   │   └── Trip_.java, Car_.java, Driver_.java, Request_.java
    │   │       Customer_.java, Route_.java, BodyType_.java   -- metamodel
    │   ├── exept/
    │   │   └── DAOException.java
    │   └── view/
    │       └── Runner.java                      -- menu and entry point
    └── resources/
        ├── META-INF/
        │   └── persistence.xml                  -- persistence unit
        └── logback.xml                          -- logger configuration
```

---

## Key Files

| File | Purpose |
|------|---------|
| `persistence.xml` | JDBC URL, user/password, SQL Server dialect, DDL=`none` |
| `JPAUtil.java` | `EntityManagerFactory` singleton, lazy initialization |
| `Trip.java` | entity: `@NamedQueries`, `@ManyToOne` → Request/Driver/Car |
| `Trip_.java` | metamodel: `SingularAttribute` for type-safe Criteria API |
| `DAOTrip.java` | trips: `fetch` reads, assignment, completion in transaction, ID validation |
| `DAOCar.java` | cars: `findInRepair`, `updateStatus`, `delete` |
| `DAOException.java` | DAO exception with message and cause |
| `Runner.java` | 17-item menu, `readTripId()`, logging to `lab4.log` |
| `schema.sql` | 7 tables (T-SQL), foreign keys, `CHECK`, seed data |
| `logback.xml` | INFO → `lab4.log`, WARNING+ → console |

---

## Database Schema

| Table | Purpose | Key fields |
|-------|---------|------------|
| `customer` | customers | `name` (UNIQUE) |
| `route` | routes | `city_from` + `city_to` (UNIQUE) |
| `body_type` | body type lookup | `name` (UNIQUE) |
| `driver` | drivers | `license_category`, `phone` |
| `car` | cars | `load_kg`, `status` (`SERVICEABLE`/`REPAIR`), FK → `body_type` |
| `request` | customer requests | required body type / load, FK → `customer`, `route`, `body_type` |
| `trip` | trips | FK → `request`, `driver`, `car`; `status`, `car_condition_after` |

---

## Task Compliance

| Requirement | Implementation |
|-------------|----------------|
| **Part 1** — entities and relationships | 7 `@Entity` with `@ManyToOne` / `@JoinColumn` |
| JPA entities | `entity/`: Trip, Car, Driver, Request, Customer, Route, BodyType |
| JPA-QL + `@NamedQueries` / `@NamedQuery` | 16 annotations on 7 entities |
| DAO preserved, modified for JPA | 7 classes in `dao/`, `EntityManager` instead of JDBC |
| Console interface | `view/Runner.java` — 17-item menu |
| **Part 2** — Criteria API + Metamodel | all SELECTs in DAO via `CriteriaBuilder` |
| Metamodel API | 7 `@StaticMetamodel` classes (`Trip_`, `Car_`, …) |
| Type safety | `root.get(Trip_.id)` instead of `root.get("id")` |
| Exception handling | `DAOException`, transaction rollback |
| Logger | SLF4J + Logback → `lab4.log`, console — WARNING+ |

---

## Run

**1. Create the database and load seed data** (SQL Server must be running):

```powershell
# execute schema.sql in SSMS or via PowerShell:
$sql = Get-Content schema.sql -Raw -Encoding UTF8
$batches = $sql -split '(?m)^GO\s*$'
$conn = New-Object System.Data.SqlClient.SqlConnection(
    "Server=localhost;Database=master;Integrated Security=True;TrustServerCertificate=True")
$conn.Open()
foreach ($batch in $batches) {
    if ($batch.Trim()) {
        $cmd = $conn.CreateCommand(); $cmd.CommandText = $batch
        [void]$cmd.ExecuteNonQuery()
    }
}
$conn.Close()
```

**2. Check the connection settings** in `src/main/resources/META-INF/persistence.xml`
(URL, `user`, `password`). Default: `sa` / `1234`, port `1433`.

**3. Run** — the easiest way is **IntelliJ IDEA**: open the project and run `by.bsu.lab4.view.Runner`.

From the console (requires Maven):

```powershell
mvn -q compile exec:java
```

The menu appears in the console; errors and the run trace go to `lab4.log`.

---

## Done

- [x] Created the `autobase` MS SQL Server database per variant 8 (`schema.sql`, T-SQL)
- [x] Seed data: 5 body types, 4 customers, 4 routes, 4 drivers, 5 cars, 4 requests, 4 trips
- [x] 7 JPA entities with `@ManyToOne` / `@JoinColumn` relationships
- [x] `@NamedQueries` / `@NamedQuery` — JPA-QL on all entities (Part 1)
- [x] 7 static metamodels `@StaticMetamodel` (Part 2)
- [x] `JPAUtil` — `EntityManagerFactory` singleton
- [x] 7 DAO classes: methods preserved, Criteria API + Metamodel implementation
- [x] Trip reads via `fetch` (route, car, driver in a single query)
- [x] Business rule: car serviceable, body type and load capacity match the request
- [x] "Trip completed" transaction with `rollback` on failure
- [x] Trip ID validation: reject `0`, duplicate check with trip display
- [x] Console menu of 17 items with input validation and action cancellation
- [x] SLF4J/Logback logging: INFO → `lab4.log`, console → WARNING+

---

## Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com
