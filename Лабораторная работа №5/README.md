# Лабораторная работа №5 — Java Servlet, Thymeleaf, MS SQL Server (вариант 8)

Web-приложение **«Диспетчерская автобазы»** на Java Servlet с серверной шаблонизацией **Thymeleaf** и доступом к MS SQL Server через **JPA**.  
**Вариант 8**: Автобаза. Управление рейсами.

Диспетчер распределяет заявки на рейсы между водителями, за каждым из которых закреплён свой автомобиль. На рейс назначается автомобиль, находящийся в исправном состоянии и характеристики которого соответствуют заявке. Приложение построено по паттерну **фронт-контроллер**: единый сервлет-диспетчер перехватывает все запросы и передаёт их отдельным контроллерам; страницы генерируются **Thymeleaf**, слой DAO перенесён из лабораторной работы №4 (Criteria API + Metamodel).

---

## База данных

**Проект:** `Лабораторная работа №5/lab5` | **Контекст:** `/lab5` → http://localhost:8080/lab5/

| Ресурс | Значение |
|--------|----------|
| База данных | `autobase` — MS SQL Server 2019, T-SQL |
| Таблиц | 7: `customer`, `route`, `body_type`, `driver`, `car`, `request`, `trip` |
| Схема + тестовые данные | `../schema.sql` |
| Подключение | `src/main/resources/META-INF/persistence.xml` |
| Провайдер JPA | EclipseLink 4.0.1 |
| Драйвер БД | `mssql-jdbc` 12.8.1 |
| Шаблонизатор | Thymeleaf 3.1.0.RELEASE |
| Контейнер | Jetty 11 (Maven Plugin, `mvn jetty:run`) |
| Лог-файл | `lab5.log` (INFO) + консоль |
| Условие | `../Лаб5_Servlet.doc` |

---

## Условие

### Вариант 8 — Автобаза

- База данных автобазы: заказчики, маршруты, типы кузова, водители, автомобили, заявки и рейсы
- Диспетчер распределяет заявки на рейсы между водителями
- У каждого водителя закреплён свой автомобиль
- На рейс может быть назначен автомобиль, находящийся в **исправном состоянии** и характеристики которого (тип кузова, грузоподъёмность) **соответствуют заявке**

### Часть 1

1. Разработать центральный сервлет-диспетчер, который: принимает все HTTP-запросы приложения; анализирует URL запроса; определяет необходимый контроллер (команду); передаёт управление выбранному контроллеру
2. Для каждой пользовательской операции создать отдельный контроллер, который: получает данные из запроса; обращается к DAO и выполняет запросы в соответствии с условием лабораторной работы №3 (слой DAO перенести из лабы №3 или №4); передаёт результаты в шаблон Thymeleaf
3. Каждому пользователю поставить в соответствие объект сессии; в cookie — время и дата последнего сеанса и количество посещений ресурса

### Часть 2

1. Создать страницу `index.html` с информацией о системе и меню со ссылками на страницы; использовать таблицы стилей CSS
2. Проработать обработку исключений, подключить логгер: страница `error.html` для краткого сообщения о критической ошибке (например, невозможности соединения с БД); логирование **SLF4J + Log4J2** — запуск приложения, обработка запросов, ошибки, операции с БД

### Что реализовано

- **Сервлет-диспетчер** `DispatcherServlet` на `/*` (Front Controller): лог запроса, сессия/cookie, разбор URL, выбор контроллера, обработка исключений
- **6 контроллеров** (Command): список рейсов, рейсы по водителю, создание рейса, авто в ремонте, список водителей, страница ошибки
- **Слой DAO из лабы 4**: 7 DAO на Criteria API + 7 метамоделей, `JPAUtil`, `DAOException` — запросы `findAll`, `findByDriver`, `findInRepair` (лаба 3)
- **Сессии и cookie**: объект `UserSession` в HTTP-сессии; cookie `visits` (+1 один раз за сессию) и `lastVisit` (30 дней)
- **index.html** + `css/style.css`: герой-шапка, карточка «О системе», сетка плиток-меню
- **Обработка ошибок**: бизнес-ошибки → в форму; критические (сбой БД) → redirect на `error.html` с кратким сообщением
- **Логирование SLF4J + Log4J2**: консоль + `lab5.log` — старт, запросы, ошибки, операции БД
- Запуск **`mvn jetty:run`** → в консоль печатается `http://localhost:8080/lab5/`

### Чек-лист

- [x] Сервлет-диспетчер принимает все запросы, анализирует URL, выбирает контроллер
- [x] Отдельный контроллер на каждую пользовательскую операцию
- [x] Контроллер: данные из запроса → DAO → шаблон Thymeleaf
- [x] Слой DAO перенесён из лабы 4 (Criteria API + Metamodel), запросы лабы 3
- [x] Объект сессии `UserSession` каждому пользователю
- [x] Cookie: последний сеанс + количество посещений (+1 за сессию)
- [x] `index.html` с описанием системы, меню и CSS
- [x] `error.html` с кратким сообщением при критической ошибке
- [x] Логирование SLF4J + Log4J2: запуск, запросы, ошибки, операции с БД
- [x] Приложение запускается через `mvn jetty:run` и работает с базой

---

## Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" height="28"/>
  <img src="https://img.shields.io/badge/Servlet-ED8B00?style=for-the-badge&logo=eclipsejakartaee&logoColor=white" alt="Servlet" height="28"/>
  <img src="https://img.shields.io/badge/Thymeleaf-6DB24B?style=for-the-badge&logoColor=white" alt="Thymeleaf" height="28"/>
  <img src="https://img.shields.io/badge/JPA-00758F?style=for-the-badge&logoColor=white" alt="JPA" height="28"/>
  <img src="https://img.shields.io/badge/EclipseLink-FF7A00?style=for-the-badge&logoColor=white" alt="EclipseLink" height="28"/>
  <img src="https://img.shields.io/badge/MS_SQL_Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" alt="MS SQL Server" height="28"/>
  <img src="https://img.shields.io/badge/Jetty-FC3E24?style=for-the-badge&logo=eclipsejetty&logoColor=white" alt="Jetty" height="28"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" height="28"/>
  <img src="https://img.shields.io/badge/Log4j2-00758F?style=for-the-badge&logoColor=white" alt="Log4j2" height="28"/>
  <img src="https://img.shields.io/badge/SLF4J-2D2D2D?style=for-the-badge&logoColor=white" alt="SLF4J" height="28"/>
</p>

---

## Архитектура

```
Браузер ── HTTP-запрос ──► DispatcherServlet (/*)          [web.xml, load-on-startup=1]
                              │ init(): TemplateEngine + лог "Application started"
                              │ лог запроса: "GET /welcome/trips/list"
                              │ сессия UserSession + cookie visits/lastVisit
                              │
                              ├─ путь с точкой (css, index.html) ──► serveStatic()
                              │
                              ▼
                    ControllerMappings (Map<String, IController>)
                              │ "/welcome/trips/list"  → TripListController
                              │ "/welcome/trips/create"→ TripCreateController
                              │ … 6 маршрутов
                              ▼
                    Контроллер (Command)
                              │ параметры запроса (getParameterValue)
                              │ DAO-вызов
                              │ ctx.setVariable(...) ──► Thymeleaf-шаблон ──► HTML
                              ▼
                    DAO (Criteria API + метамодель Trip_, Car_, …)   [перенесён из лабы 4]
                              ▼
                    JPAUtil ── EntityManagerFactory ("autobasePU")
                              ▼
                    EclipseLink ──► MS SQL Server (autobase)
                              ▲
                    persistence.xml ◄── JDBC URL, sa/1234

                    Исключение ──► isDbFailure()? ──► fail() ──► redirect /welcome/error
                    Логи: SLF4J ──► Log4j2 ──► консоль + lab5.log
```

---

## Ключевые механизмы

### 1. `web.xml` — регистрация диспетчера

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>by.bsu.lab5.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
    <init-param>
        <param-name>httpPort</param-name>
        <param-value>8080</param-value>
    </init-param>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>
```

`/*` — единая точка входа: статика отдаётся напрямую, всё остальное идёт через контроллеры. `load-on-startup=1` — `init()` выполняется при старте Jetty (сразу пишет лог и печатает URL).

### 2. `DispatcherServlet.handle()` — поток запроса

```java
String path = normalizePath(request);
log.info("{} {}", request.getMethod(), path);
if (isStatic(path)) { serveStatic(path, response); return; }
updateSessionAndCookies(request, response);
try {
    IServletWebExchange webExchange = this.application.buildExchange(request, response);
    IController controller = ControllerMappings.resolveControllerForRequest(webExchange.getRequest());
    if (controller == null) { fail(request, response, "Страница не найдена"); return; }
    response.setContentType("text/html;charset=UTF-8");
    controller.process(webExchange, templateEngine, response.getWriter());
} catch (Exception e) {
    log.error("Error handling request {}", path, e);
    fail(request, response, isDbFailure(e)
            ? "Не удалось подключиться к базе данных. Попробуйте позже."
            : "Внутренняя ошибка сервера. Попробуйте позже.");
}
```

Диспетчер не знает предметной области: он логирует, обновляет сессию, находит контроллер и ловит исключения.

### 3. `ControllerMappings` — URL → контроллер

```java
private static final Map<String, IController> CONTROLLERS = new HashMap<>();

static {
    CONTROLLERS.put("/welcome/trips/list", new TripListController());
    CONTROLLERS.put("/welcome/trips/by-driver", new TripsByDriverController());
    CONTROLLERS.put("/welcome/trips/create", new TripCreateController());
    CONTROLLERS.put("/welcome/cars/repair", new CarRepairController());
    CONTROLLERS.put("/welcome/drivers/list", new DriverListController());
    CONTROLLERS.put("/welcome/error", new ErrorController());
}

public static IController resolveControllerForRequest(final IWebRequest request) {
    return CONTROLLERS.get(request.getPathWithinApplication());
}
```

«Определить необходимый контроллер» = `map.get(path)`. Новая операция = контроллер + одна строка в карте.

### 4. Контроллер — запрос → DAO → шаблон

```java
public class TripsByDriverController extends BaseController {
    private final DAOTrip tripDao = new DAOTrip();

    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = createContext(webExchange);
        String driverIdParam = webExchange.getRequest().getParameterValue("driverId");
        if (driverIdParam != null && !driverIdParam.isBlank()) {
            try {
                trips = tripDao.findByDriver(Integer.parseInt(driverIdParam.trim()));
            } catch (NumberFormatException e) {
                ctx.setVariable("searchError", "ID водителя должен быть целым числом");
            } catch (DAOException e) {
                if (e.getCause() == null) ctx.setVariable("searchError", e.getMessage());
                else throw e;
            }
        }
        ctx.setVariable("trips", trips);
        templateEngine.process("trips/by-driver", ctx, writer);
    }
}
```

Три обязанности из условия видны в одном методе: данные из запроса, вызов DAO, передача результата в шаблон. Бизнес-ошибка (`DAOException` без причины) показывается в форме; ошибка с причиной (сбой БД) уходит в диспетчер → страница ошибки.

### 5. `updateSessionAndCookies()` — сессия и cookie

```java
HttpSession session = request.getSession(true);
UserSession userSession = (UserSession) session.getAttribute("userSession");
if (userSession == null) {
    userSession = new UserSession();
    int visits = 1;
    String previousVisit = null;
    // прочитать cookie visits / lastVisit, visits++
    session.setAttribute("userSession", userSession);
    Cookie visitsCookie = new Cookie("visits", String.valueOf(visits));       // Max-Age 30 дней
    Cookie lastVisitCookie = new Cookie("lastVisit", now.format(COOKIE_TIME));
    response.addCookie(visitsCookie);
    response.addCookie(lastVisitCookie);
}
request.setAttribute("visits", userSession.getVisitCount());
```

Объект `UserSession` (UUID, время создания, счётчик) живёт в серверной сессии. Cookie хранятся на клиенте 30 дней: `visits` увеличивается **один раз за сессию** (при первом запросе), `lastVisit` — дата предыдущего сеанса. Оба значения показываются в подвале каждой страницы.

### 6. `fail()` + `isDbFailure()` — обработка критических ошибок

```java
private void fail(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
    if (response.isCommitted()) { response.sendError(500); return; }
    request.getSession(true).setAttribute("errorMessage", message);
    response.sendRedirect(request.getContextPath() + "/welcome/error");
}

private boolean isDbFailure(Throwable e) {
    Throwable current = e;
    while (current != null) {
        String name = current.getClass().getName();
        if (current instanceof java.sql.SQLException || name.contains("Persistence")
                || name.contains("Database") || name.contains("Communication")) return true;
        current = current.getCause() == current ? null : current.getCause();
    }
    return false;
}
```

`isDbFailure()` идёт по цепочке причин исключения: если внутри `SQLException`/Persistence — пользователю «Не удалось подключиться к базе данных», иначе «Внутренняя ошибка». `ErrorController` достаёт текст из сессии и рендерит `error.html`.

### 7. Шаблон Thymeleaf — `trips/list.html`

```html
<table th:unless="${#lists.isEmpty(trips)}">
    <tr th:each="t : ${trips}">
        <td th:text="${t.id}">1</td>
        <td th:text="${t.request.route.cityFrom} + ' — ' + ${t.request.route.cityTo}">Минск — Брест</td>
        <td th:text="${t.driver != null} ? ${t.driver.fullName} : '—'">—</td>
        <td th:text="${t.status}">PLANNED</td>
    </tr>
</table>
```

`th:each` — цикл, `th:text` — подстановка (экранирование автоматическое), тернарник закрывает NULL водителя/автомобиля, `@{/...}` — ссылки с контекстным путём. Движок создаётся в `init()` диспетчера: префикс `/WEB-INF/templates/`, суффикс `.html`, кэш 1 час.

### 8. `log4j2.xml` — логгер

```xml
<Loggers>
    <Logger name="by.bsu.lab5.dao" level="debug"/>
    <Logger name="org.eclipse.persistence" level="warn"/>
    <Root level="info">
        <AppenderRef ref="Console"/>
        <AppenderRef ref="File"/>
    </Root>
</Loggers>
```

Консоль + файл `lab5.log`. Логируется всё по условию: старт (`Application started`), каждый запрос (`GET /welcome/...`), ошибки (`Error handling request ...`), операции с БД (в DAO: `Loaded N trips`, `Can't read ...`).

### 9. `pom.xml` — Jetty Maven Plugin

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>11.0.16</version>
    <configuration>
        <scan>10</scan>
        <webApp>
            <contextPath>/lab5</contextPath>
        </webApp>
    </configuration>
</plugin>
```

Контейнер не устанавливается отдельно: `mvn jetty:run` поднимает встроенный Jetty. При старте диспетчер печатает в консоль адрес — `http://localhost:8080/lab5/`.

---

## Страницы приложения

| URL | Страница | Контроллер | DAO-вызов |
|-----|----------|-----------|-----------|
| `/` | `index.html` — описание и меню | статика | — |
| `/welcome/trips/list` | Список рейсов | `TripListController` | `DAOTrip.findAll()` |
| `/welcome/trips/by-driver` | Рейсы по водителю (форма) | `TripsByDriverController` | `DAOTrip.findByDriver(id)` |
| `/welcome/trips/create` | Создать рейс (форма) | `TripCreateController` | `DAOTrip.create(...)` |
| `/welcome/cars/repair` | Автомобили в ремонте | `CarRepairController` | `DAOCar.findInRepair()` |
| `/welcome/drivers/list` | Список водителей | `DriverListController` | `DAODriver.findAll()` |
| `/welcome/error` | Страница ошибки | `ErrorController` | — |

Форма «Рейсы по водителю» — GET (параметр в URL, можно обновить); форма «Создать рейс» — POST (изменяет данные).

---

## Бизнес-правила

```java
if (!car.isServiceable()) throw ...;                                    // авто в ремонте
if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) throw ...; // тип кузова
if (car.getLoadKg() < request.getRequiredLoadKg()) throw ...;           // грузоподъёмность
```

| Правило | Где проверяется |
|---------|-----------------|
| Автомобиль только исправный | `DAOTrip.checkCarFitsRequest()` |
| Тип кузова и грузоподъёмность → заявка | `DAOTrip.checkCarFitsRequest()` |
| ID рейса ≠ 0 | `DAOTrip.create()` → ошибка в форму |
| ID рейса не занят | `DAOTrip.create()` → «уже существует» в форму |
| Сбой БД → `error.html`, бизнес-ошибка → форма | `e.getCause() == null` в контроллере + `isDbFailure()` |
| Посещение считается один раз за сессию | `DispatcherServlet.updateSessionAndCookies()` |

---

## Структура проекта

```
Лабораторная работа №5/
├── Лаб5_Servlet.doc               -- условие
├── schema.sql                     -- схема БД (T-SQL) + тестовые данные
└── lab5/
    ├── pom.xml                    -- war, Thymeleaf, EclipseLink, Log4j2, Jetty
    ├── lab5.log                   -- лог работы (создаётся при запуске)
    └── src/main/
        ├── java/by/bsu/lab5/
        │   ├── servlet/
        │   │   ├── DispatcherServlet.java      -- сервлет-диспетчер (/*)
        │   │   └── ControllerMappings.java     -- Map URL → контроллер
        │   ├── contr/
        │   │   ├── IController.java            -- интерфейс-команда
        │   │   ├── BaseController.java         -- WebContext: сессия, ошибки, cookie
        │   │   ├── TripListController.java
        │   │   ├── TripsByDriverController.java
        │   │   ├── TripCreateController.java
        │   │   ├── CarRepairController.java
        │   │   ├── DriverListController.java
        │   │   └── ErrorController.java
        │   ├── session/
        │   │   └── UserSession.java            -- объект сессии пользователя
        │   ├── dao/                            -- перенесён из лабы 4
        │   │   ├── JPAUtil.java, DAOTrip.java, DAODriver.java, DAOCar.java
        │   │   ├── DAORequest.java, DAOCustomer.java, DAORoute.java, DAOBodyType.java
        │   ├── entity/                         -- 7 сущностей + 7 метамоделей
        │   │   ├── Trip.java, Car.java, Driver.java, Request.java, …
        │   │   └── Trip_.java, Car_.java, Driver_.java, …
        │   └── exept/
        │       └── DAOException.java
        ├── resources/
        │   ├── META-INF/persistence.xml        -- autobase, sa/1234, DDL=none
        │   └── log4j2.xml                      -- консоль + lab5.log
        └── webapp/
            ├── index.html                      -- главная: описание + меню
            ├── css/style.css
            └── WEB-INF/
                ├── web.xml                     -- диспетчер на /*
                └── templates/
                    ├── error.html
                    ├── trips/list.html, by-driver.html, create.html
                    ├── cars/repair.html
                    └── drivers/list.html
```

---

## Ключевые файлы

| Файл | Назначение |
|------|-----------|
| `web.xml` | регистрация диспетчера на `/*`, `load-on-startup` |
| `DispatcherServlet.java` | Front Controller: лог, сессия/cookie, выбор контроллера, catch → error |
| `ControllerMappings.java` | `HashMap` URL → контроллер (6 маршрутов) |
| `IController.java` / `BaseController.java` | интерфейс-команда + общий `WebContext` |
| `TripCreateController.java` | форма: валидация ввода, бизнес-ошибки в форму |
| `UserSession.java` | объект сессии: UUID, время создания, счётчик |
| `persistence.xml` | JDBC URL, sa/1234, диалект SQL Server, DDL=`none` |
| `DAOTrip.java` | рейсы: `findAll`, `findByDriver`, `create` с проверками (из лабы 4) |
| `log4j2.xml` | INFO → `lab5.log` + консоль, DAO → DEBUG |
| `index.html` / `style.css` | главная страница: герой, карточка, плитки меню |
| `schema.sql` | 7 таблиц (T-SQL), внешние ключи, `CHECK`, тестовые данные |

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
| **Ч1.1** диспетчер принимает все запросы, анализирует URL, выбирает контроллера | `DispatcherServlet` на `/*` + `ControllerMappings.get(path)` |
| **Ч1.1** передаёт управление выбранному контроллеру | `controller.process(webExchange, templateEngine, writer)` |
| **Ч1.2** отдельный контроллер на каждую операцию | 6 классов в `contr/` |
| **Ч1.2** данные из запроса | `request.getParameterValue("driverId")` и др. |
| **Ч1.2** DAO из лабы 3/4, запросы лабы 3 | `dao/` из лабы 4: `findAll`, `findByDriver`, `findInRepair` |
| **Ч1.2** результаты в шаблон Thymeleaf | `ctx.setVariable` → `templateEngine.process` |
| **Ч1.3** объект сессии каждому пользователю | `UserSession` в атрибуте `HttpSession` |
| **Ч1.3** cookie: последний сеанс + число посещений | cookie `lastVisit` и `visits`, Max-Age 30 дней |
| **Ч2.1** `index.html` с описанием, меню и CSS | `webapp/index.html` + `css/style.css` |
| **Ч2.2** `error.html` при критической ошибке | `fail()` → redirect → `ErrorController` → `error.html` |
| **Ч2.2** краткое понятное сообщение | «Не удалось подключиться к базе данных…» / «Внутренняя ошибка…» |
| **Ч2.2** SLF4J + Log4J2: старт, запросы, ошибки, БД | `log4j2.xml`, 4 типа записей в `lab5.log` |
| Запуск через Jetty Maven Plugin | `pom.xml`, `mvn jetty:run` → `http://localhost:8080/lab5/` |

---

## Запуск

**1. Создать базу и залить тестовые данные** (SQL Server должен быть запущен) — `schema.sql` из корня папки:

```powershell
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

**2. Проверить подключение** в `lab5/src/main/resources/META-INF/persistence.xml`
(URL, `user`, `password`). По умолчанию: `sa` / `1234`, порт `1433`.

**3. Запуск** — из папки `lab5`:

```powershell
mvn jetty:run
```

В консоли появятся `Application started` и адрес **http://localhost:8080/lab5/**. Ошибки и ход работы — в `lab5.log`. Остановка — `Ctrl+C`.

---

## Что сделано

- [x] Сервлет-диспетчер `DispatcherServlet` на `/*` (Front Controller): лог, сессия, cookie, выбор контроллера
- [x] `ControllerMappings` — маршрутизация URL → контроллер (6 путей)
- [x] 6 контроллеров по условию (список, поиск по водителю, создание, ремонт, водители, ошибка)
- [x] Слой DAO перенесён из лабы 4: 7 DAO на Criteria API + 7 метамоделей, `JPAUtil`
- [x] Запросы лабы 3: `findAll`, `findByDriver`, `findInRepair`
- [x] Объект сессии `UserSession` + cookie `visits`/`lastVisit` (30 дней, +1 за сессию)
- [x] `index.html` с описанием системы, меню-плитками и `css/style.css`
- [x] `error.html` + перенаправление при критической ошибке (в т.ч. сбой соединения с БД)
- [x] Бизнес-ошибки (дубль ID, кривой ввод) показываются прямо в форме
- [x] Логирование SLF4J + Log4J2: старт, запросы, ошибки, операции с БД → `lab5.log`
- [x] `mvn jetty:run` запускает приложение, в консоль печатается URL
- [x] Все страницы проверены на живом запуске: таблицы из БД, формы, cookie, страница ошибок

---

## Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---
---

# Laboratory Work №5 — Java Servlet, Thymeleaf, MS SQL Server (Variant 8)

A **"Car Fleet Dispatcher"** web application built on Java Servlets with **Thymeleaf** server-side templating and MS SQL Server access via **JPA**.  
**Variant 8**: Car Database. Trip Management.

The dispatcher assigns customer requests to drivers, each of whom has an assigned vehicle. A trip can only be assigned a car that is in **working condition** and whose characteristics (body type, load capacity) **match the request**. The app follows the **Front Controller** pattern: a single dispatcher servlet intercepts all requests and routes them to individual controllers; pages are rendered by **Thymeleaf**; the DAO layer is carried over from lab 4 (Criteria API + Metamodel).

---

## Database

**Project:** `Лабораторная работа №5/lab5` | **Context:** `/lab5` → http://localhost:8080/lab5/

| Resource | Value |
|----------|-------|
| Database | `autobase` — MS SQL Server 2019, T-SQL |
| Tables | 7: `customer`, `route`, `body_type`, `driver`, `car`, `request`, `trip` |
| Schema + seed data | `../schema.sql` |
| Connection settings | `src/main/resources/META-INF/persistence.xml` |
| JPA provider | EclipseLink 4.0.1 |
| DB driver | `mssql-jdbc` 12.8.1 |
| Template engine | Thymeleaf 3.1.0.RELEASE |
| Container | Jetty 11 (Maven Plugin, `mvn jetty:run`) |
| Log file | `lab5.log` (INFO) + console |
| Task sheet | `../Лаб5_Servlet.doc` |

---

## Task

### Variant 8 — Car Database

- Car fleet database: customers, routes, body types, drivers, cars, requests, and trips
- The dispatcher assigns requests to drivers, each with an assigned vehicle
- A trip may be assigned a car that is in **working condition** and whose characteristics **match the request**

### Part 1

1. Develop a central dispatcher servlet that: accepts all application HTTP requests; analyzes the request URL; resolves the required controller (command); passes control to it
2. Create a separate controller for each user operation that: reads request data; calls the DAO and performs the queries from lab 3 (DAO layer transferred from lab 3 or 4); passes results to a Thymeleaf template
3. Map each user to a session object; store the last session date/time and the resource visit count in a cookie

### Part 2

1. Create an `index.html` page with system information and a menu of links; use CSS stylesheets
2. Implement exception handling and a logger: an `error.html` page with a short message for critical errors (e.g. DB connection failure); **SLF4J + Log4J2** logging — app startup, request handling, errors, DB operations

### Implemented

- **Dispatcher servlet** `DispatcherServlet` on `/*` (Front Controller): request logging, session/cookies, URL parsing, controller resolution, exception handling
- **6 controllers** (Command): trip list, trips by driver, trip creation, cars in repair, driver list, error page
- **DAO layer from lab 4**: 7 DAOs on Criteria API + 7 metamodels, `JPAUtil`, `DAOException` — queries `findAll`, `findByDriver`, `findInRepair` (lab 3)
- **Sessions and cookies**: `UserSession` object in the HTTP session; `visits` cookie (+1 once per session) and `lastVisit` (30 days)
- **index.html** + `css/style.css`: hero header, "About" card, menu tile grid
- **Error handling**: business errors → inline in the form; critical errors (DB failure) → redirect to `error.html` with a short message
- **SLF4J + Log4J2 logging**: console + `lab5.log` — startup, requests, errors, DB operations
- Runs with **`mvn jetty:run`** → prints `http://localhost:8080/lab5/` to the console

### Checklist

- [x] Dispatcher servlet accepts all requests, analyzes the URL, resolves the controller
- [x] A separate controller per user operation
- [x] Controller: request data → DAO → Thymeleaf template
- [x] DAO layer transferred from lab 4 (Criteria API + Metamodel), lab 3 queries
- [x] `UserSession` object per user
- [x] Cookies: last session + visit count (+1 per session)
- [x] `index.html` with system info, menu and CSS
- [x] `error.html` with a short message on critical failure
- [x] SLF4J + Log4J2 logging: startup, requests, errors, DB operations
- [x] Application runs via `mvn jetty:run` and works with the database

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" height="28"/>
  <img src="https://img.shields.io/badge/Servlet-ED8B00?style=for-the-badge&logo=eclipsejakartaee&logoColor=white" alt="Servlet" height="28"/>
  <img src="https://img.shields.io/badge/Thymeleaf-6DB24B?style=for-the-badge&logoColor=white" alt="Thymeleaf" height="28"/>
  <img src="https://img.shields.io/badge/JPA-00758F?style=for-the-badge&logoColor=white" alt="JPA" height="28"/>
  <img src="https://img.shields.io/badge/EclipseLink-FF7A00?style=for-the-badge&logoColor=white" alt="EclipseLink" height="28"/>
  <img src="https://img.shields.io/badge/MS_SQL_Server-CC2927?style=for-the-badge&logo=microsoftsqlserver&logoColor=white" alt="MS SQL Server" height="28"/>
  <img src="https://img.shields.io/badge/Jetty-FC3E24?style=for-the-badge&logo=eclipsejetty&logoColor=white" alt="Jetty" height="28"/>
  <img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" height="28"/>
  <img src="https://img.shields.io/badge/Log4j2-00758F?style=for-the-badge&logoColor=white" alt="Log4j2" height="28"/>
  <img src="https://img.shields.io/badge/SLF4J-2D2D2D?style=for-the-badge&logoColor=white" alt="SLF4J" height="28"/>
</p>

---

## Architecture

```
Browser ── HTTP request ──► DispatcherServlet (/*)          [web.xml, load-on-startup=1]
                              │ init(): TemplateEngine + log "Application started"
                              │ request log: "GET /welcome/trips/list"
                              │ UserSession + cookie visits/lastVisit
                              │
                              ├─ path with a dot (css, index.html) ──► serveStatic()
                              │
                              ▼
                    ControllerMappings (Map<String, IController>)
                              │ "/welcome/trips/list"  → TripListController
                              │ "/welcome/trips/create"→ TripCreateController
                              │ … 6 routes
                              ▼
                    Controller (Command)
                              │ request parameters (getParameterValue)
                              │ DAO call
                              │ ctx.setVariable(...) ──► Thymeleaf template ──► HTML
                              ▼
                    DAO (Criteria API + metamodel Trip_, Car_, …)   [from lab 4]
                              ▼
                    JPAUtil ── EntityManagerFactory ("autobasePU")
                              ▼
                    EclipseLink ──► MS SQL Server (autobase)
                              ▲
                    persistence.xml ◄── JDBC URL, sa/1234

                    Exception ──► isDbFailure()? ──► fail() ──► redirect /welcome/error
                    Logs: SLF4J ──► Log4j2 ──► console + lab5.log
```

---

## Key Mechanisms

### 1. `web.xml` — dispatcher registration

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>by.bsu.lab5.servlet.DispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
    <init-param>
        <param-name>httpPort</param-name>
        <param-value>8080</param-value>
    </init-param>
</servlet>
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/*</url-pattern>
</servlet-mapping>
```

`/*` — a single entry point: static files are served directly, everything else goes through controllers. `load-on-startup=1` runs `init()` at Jetty startup (logs immediately and prints the URL).

### 2. `DispatcherServlet.handle()` — request flow

```java
String path = normalizePath(request);
log.info("{} {}", request.getMethod(), path);
if (isStatic(path)) { serveStatic(path, response); return; }
updateSessionAndCookies(request, response);
try {
    IServletWebExchange webExchange = this.application.buildExchange(request, response);
    IController controller = ControllerMappings.resolveControllerForRequest(webExchange.getRequest());
    if (controller == null) { fail(request, response, "Страница не найдена"); return; }
    response.setContentType("text/html;charset=UTF-8");
    controller.process(webExchange, templateEngine, response.getWriter());
} catch (Exception e) {
    log.error("Error handling request {}", path, e);
    fail(request, response, isDbFailure(e)
            ? "Не удалось подключиться к базе данных. Попробуйте позже."
            : "Внутренняя ошибка сервера. Попробуйте позже.");
}
```

The dispatcher knows nothing about the domain: it logs, updates the session, resolves the controller, and catches exceptions.

### 3. `ControllerMappings` — URL → controller

```java
private static final Map<String, IController> CONTROLLERS = new HashMap<>();

static {
    CONTROLLERS.put("/welcome/trips/list", new TripListController());
    CONTROLLERS.put("/welcome/trips/by-driver", new TripsByDriverController());
    CONTROLLERS.put("/welcome/trips/create", new TripCreateController());
    CONTROLLERS.put("/welcome/cars/repair", new CarRepairController());
    CONTROLLERS.put("/welcome/drivers/list", new DriverListController());
    CONTROLLERS.put("/welcome/error", new ErrorController());
}

public static IController resolveControllerForRequest(final IWebRequest request) {
    return CONTROLLERS.get(request.getPathWithinApplication());
}
```

"Resolve the controller" = `map.get(path)`. A new operation = a controller + one line in the map.

### 4. Controller — request → DAO → template

```java
public class TripsByDriverController extends BaseController {
    private final DAOTrip tripDao = new DAOTrip();

    public void process(IWebExchange webExchange, ITemplateEngine templateEngine, Writer writer) throws Exception {
        WebContext ctx = createContext(webExchange);
        String driverIdParam = webExchange.getRequest().getParameterValue("driverId");
        if (driverIdParam != null && !driverIdParam.isBlank()) {
            try {
                trips = tripDao.findByDriver(Integer.parseInt(driverIdParam.trim()));
            } catch (NumberFormatException e) {
                ctx.setVariable("searchError", "ID водителя должен быть целым числом");
            } catch (DAOException e) {
                if (e.getCause() == null) ctx.setVariable("searchError", e.getMessage());
                else throw e;
            }
        }
        ctx.setVariable("trips", trips);
        templateEngine.process("trips/by-driver", ctx, writer);
    }
}
```

All three duties from the task are visible in one method: read request data, call the DAO, pass results to the template. A business error (`DAOException` without a cause) is shown in the form; an error with a cause (DB failure) is rethrown to the dispatcher → error page.

### 5. `updateSessionAndCookies()` — session and cookies

```java
HttpSession session = request.getSession(true);
UserSession userSession = (UserSession) session.getAttribute("userSession");
if (userSession == null) {
    userSession = new UserSession();
    int visits = 1;
    String previousVisit = null;
    // read cookies visits / lastVisit, visits++
    session.setAttribute("userSession", userSession);
    Cookie visitsCookie = new Cookie("visits", String.valueOf(visits));       // Max-Age 30 days
    Cookie lastVisitCookie = new Cookie("lastVisit", now.format(COOKIE_TIME));
    response.addCookie(visitsCookie);
    response.addCookie(lastVisitCookie);
}
request.setAttribute("visits", userSession.getVisitCount());
```

The `UserSession` object (UUID, creation time, counter) lives in the server-side session. Cookies live on the client for 30 days: `visits` is incremented **once per session** (on the first request), `lastVisit` is the previous session's time. Both values are shown in the footer of every page.

### 6. `fail()` + `isDbFailure()` — critical error handling

```java
private void fail(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
    if (response.isCommitted()) { response.sendError(500); return; }
    request.getSession(true).setAttribute("errorMessage", message);
    response.sendRedirect(request.getContextPath() + "/welcome/error");
}

private boolean isDbFailure(Throwable e) {
    Throwable current = e;
    while (current != null) {
        String name = current.getClass().getName();
        if (current instanceof java.sql.SQLException || name.contains("Persistence")
                || name.contains("Database") || name.contains("Communication")) return true;
        current = current.getCause() == current ? null : current.getCause();
    }
    return false;
}
```

`isDbFailure()` walks the exception cause chain: if it contains `SQLException`/Persistence, the user sees "Failed to connect to the database", otherwise "Internal server error". `ErrorController` reads the message from the session and renders `error.html`.

### 7. Thymeleaf template — `trips/list.html`

```html
<table th:unless="${#lists.isEmpty(trips)}">
    <tr th:each="t : ${trips}">
        <td th:text="${t.id}">1</td>
        <td th:text="${t.request.route.cityFrom} + ' — ' + ${t.request.route.cityTo}">Minsk — Brest</td>
        <td th:text="${t.driver != null} ? ${t.driver.fullName} : '—'">—</td>
        <td th:text="${t.status}">PLANNED</td>
    </tr>
</table>
```

`th:each` — loop, `th:text` — substitution (auto-escaped), a ternary handles NULL driver/car, `@{/...}` links include the context path. The engine is created in the dispatcher's `init()`: prefix `/WEB-INF/templates/`, suffix `.html`, 1-hour cache.

### 8. `log4j2.xml` — logger

```xml
<Loggers>
    <Logger name="by.bsu.lab5.dao" level="debug"/>
    <Logger name="org.eclipse.persistence" level="warn"/>
    <Root level="info">
        <AppenderRef ref="Console"/>
        <AppenderRef ref="File"/>
    </Root>
</Loggers>
```

Console + `lab5.log` file. Everything required by the task is logged: startup (`Application started`), every request (`GET /welcome/...`), errors (`Error handling request ...`), DB operations (in DAOs: `Loaded N trips`, `Can't read ...`).

### 9. `pom.xml` — Jetty Maven Plugin

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>11.0.16</version>
    <configuration>
        <scan>10</scan>
        <webApp>
            <contextPath>/lab5</contextPath>
        </webApp>
    </configuration>
</plugin>
```

No separate container installation: `mvn jetty:run` starts embedded Jetty. On startup the dispatcher prints the address to the console — `http://localhost:8080/lab5/`.

---

## Application Pages

| URL | Page | Controller | DAO call |
|-----|------|-----------|----------|
| `/` | `index.html` — info and menu | static | — |
| `/welcome/trips/list` | Trip list | `TripListController` | `DAOTrip.findAll()` |
| `/welcome/trips/by-driver` | Trips by driver (form) | `TripsByDriverController` | `DAOTrip.findByDriver(id)` |
| `/welcome/trips/create` | Create trip (form) | `TripCreateController` | `DAOTrip.create(...)` |
| `/welcome/cars/repair` | Cars in repair | `CarRepairController` | `DAOCar.findInRepair()` |
| `/welcome/drivers/list` | Driver list | `DriverListController` | `DAODriver.findAll()` |
| `/welcome/error` | Error page | `ErrorController` | — |

The "Trips by driver" form uses GET (parameter in the URL, refreshable); the "Create trip" form uses POST (modifies data).

---

## Business Rules

```java
if (!car.isServiceable()) throw ...;                                    // car in repair
if (car.getBodyType().getId() != request.getRequiredBodyType().getId()) throw ...; // body type
if (car.getLoadKg() < request.getRequiredLoadKg()) throw ...;           // load capacity
```

| Rule | Where it is checked |
|------|---------------------|
| Car must be serviceable | `DAOTrip.checkCarFitsRequest()` |
| Body type and load capacity match the request | `DAOTrip.checkCarFitsRequest()` |
| Trip ID ≠ 0 | `DAOTrip.create()` → error in the form |
| Trip ID must not be taken | `DAOTrip.create()` → "already exists" in the form |
| DB failure → `error.html`, business error → form | `e.getCause() == null` in the controller + `isDbFailure()` |
| A visit is counted once per session | `DispatcherServlet.updateSessionAndCookies()` |

---

## Project Structure

```
Лабораторная работа №5/
├── Лаб5_Servlet.doc               -- task sheet
├── schema.sql                     -- DB schema (T-SQL) + seed data
└── lab5/
    ├── pom.xml                    -- war, Thymeleaf, EclipseLink, Log4j2, Jetty
    ├── lab5.log                   -- runtime log (created on start)
    └── src/main/
        ├── java/by/bsu/lab5/
        │   ├── servlet/
        │   │   ├── DispatcherServlet.java      -- dispatcher servlet (/*)
        │   │   └── ControllerMappings.java     -- Map URL → controller
        │   ├── contr/
        │   │   ├── IController.java            -- command interface
        │   │   ├── BaseController.java         -- WebContext: session, errors, cookies
        │   │   ├── TripListController.java
        │   │   ├── TripsByDriverController.java
        │   │   ├── TripCreateController.java
        │   │   ├── CarRepairController.java
        │   │   ├── DriverListController.java
        │   │   └── ErrorController.java
        │   ├── session/
        │   │   └── UserSession.java            -- user session object
        │   ├── dao/                            -- carried over from lab 4
        │   │   ├── JPAUtil.java, DAOTrip.java, DAODriver.java, DAOCar.java
        │   │   ├── DAORequest.java, DAOCustomer.java, DAORoute.java, DAOBodyType.java
        │   ├── entity/                         -- 7 entities + 7 metamodels
        │   │   ├── Trip.java, Car.java, Driver.java, Request.java, …
        │   │   └── Trip_.java, Car_.java, Driver_.java, …
        │   └── exept/
        │       └── DAOException.java
        ├── resources/
        │   ├── META-INF/persistence.xml        -- autobase, sa/1234, DDL=none
        │   └── log4j2.xml                      -- console + lab5.log
        └── webapp/
            ├── index.html                      -- home: description + menu
            ├── css/style.css
            └── WEB-INF/
                ├── web.xml                     -- dispatcher on /*
                └── templates/
                    ├── error.html
                    ├── trips/list.html, by-driver.html, create.html
                    ├── cars/repair.html
                    └── drivers/list.html
```

---

## Key Files

| File | Purpose |
|------|---------|
| `web.xml` | dispatcher registration on `/*`, `load-on-startup` |
| `DispatcherServlet.java` | Front Controller: log, session/cookies, controller resolution, catch → error |
| `ControllerMappings.java` | `HashMap` URL → controller (6 routes) |
| `IController.java` / `BaseController.java` | command interface + shared `WebContext` |
| `TripCreateController.java` | form: input validation, business errors in the form |
| `UserSession.java` | session object: UUID, creation time, counter |
| `persistence.xml` | JDBC URL, sa/1234, SQL Server dialect, DDL=`none` |
| `DAOTrip.java` | trips: `findAll`, `findByDriver`, `create` with checks (from lab 4) |
| `log4j2.xml` | INFO → `lab5.log` + console, DAO → DEBUG |
| `index.html` / `style.css` | home page: hero, card, menu tiles |
| `schema.sql` | 7 tables (T-SQL), foreign keys, `CHECK`, seed data |

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
| **P1.1** dispatcher accepts all requests, analyzes URL, resolves controller | `DispatcherServlet` on `/*` + `ControllerMappings.get(path)` |
| **P1.1** passes control to the selected controller | `controller.process(webExchange, templateEngine, writer)` |
| **P1.2** a separate controller per operation | 6 classes in `contr/` |
| **P1.2** reads request data | `request.getParameterValue("driverId")` etc. |
| **P1.2** DAO from lab 3/4, lab 3 queries | `dao/` from lab 4: `findAll`, `findByDriver`, `findInRepair` |
| **P1.2** passes results to a Thymeleaf template | `ctx.setVariable` → `templateEngine.process` |
| **P1.3** session object per user | `UserSession` in the `HttpSession` attribute |
| **P1.3** cookies: last session + visit count | `lastVisit` and `visits` cookies, Max-Age 30 days |
| **P2.1** `index.html` with info, menu and CSS | `webapp/index.html` + `css/style.css` |
| **P2.2** `error.html` on critical failure | `fail()` → redirect → `ErrorController` → `error.html` |
| **P2.2** short user-friendly message | "Failed to connect to the database…" / "Internal server error…" |
| **P2.2** SLF4J + Log4J2: startup, requests, errors, DB | `log4j2.xml`, 4 record types in `lab5.log` |
| Runs via Jetty Maven Plugin | `pom.xml`, `mvn jetty:run` → `http://localhost:8080/lab5/` |

---

## Run

**1. Create the database and load seed data** (SQL Server must be running) — `schema.sql` from the parent folder:

```powershell
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

**2. Check the connection settings** in `lab5/src/main/resources/META-INF/persistence.xml`
(URL, `user`, `password`). Default: `sa` / `1234`, port `1433`.

**3. Run** — from the `lab5` folder:

```powershell
mvn jetty:run
```

The console shows `Application started` and the address **http://localhost:8080/lab5/**. Errors and the run trace go to `lab5.log`. Stop with `Ctrl+C`.

---

## Done

- [x] `DispatcherServlet` on `/*` (Front Controller): log, session, cookies, controller resolution
- [x] `ControllerMappings` — URL → controller routing (6 routes)
- [x] 6 controllers per the task (list, search by driver, create, repair, drivers, error)
- [x] DAO layer carried over from lab 4: 7 Criteria API DAOs + 7 metamodels, `JPAUtil`
- [x] Lab 3 queries: `findAll`, `findByDriver`, `findInRepair`
- [x] `UserSession` object + `visits`/`lastVisit` cookies (30 days, +1 per session)
- [x] `index.html` with system description, menu tiles and `css/style.css`
- [x] `error.html` + redirect on critical failure (including DB connection loss)
- [x] Business errors (duplicate ID, bad input) shown inline in the form
- [x] SLF4J + Log4J2 logging: startup, requests, errors, DB operations → `lab5.log`
- [x] `mvn jetty:run` starts the app and prints the URL to the console
- [x] All pages verified on a live run: DB tables, forms, cookies, error page

---

## Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com
