# Лабораторная работа №2, часть 1 — TypeScript, Angular

Разработка Angular-приложения для варианта **Автобаза**.  
**Вариант**: Автобаза. Управление рейсами.

---

## Условие

На основе предметной области "Автобаза" спроектировать Angular-приложение:

- Создать приложение командой `ng new`
- Подключить Bootstrap, добавить jumbotron в корневой компонент
- Создать папку `trips` с компонентами: **TripCenter**, **TripList**, **TripDetails**
- Настроить маршрутизацию с ленивой загрузкой (`loadChildren`)
- Реализировать сервис CRUD-операций
- Создать mock-данные с рейсами (id, description, driver)

---

## Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" height="28"/>
  <img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" height="28"/>
  <img src="https://img.shields.io/badge/RxJS-B7178C?style=for-the-badge&logo=reactivex&logoColor=white" alt="RxJS" height="28"/>
</p>

---

## Маршрутизация

Ленивая загрузка через `loadChildren` в `app.routes.ts`.

| Путь | Компонент | Описание |
|------|-----------|----------|
| `/` | → redirect | Редирект на `/trip-center` |
| `/trip-center` | TripCenter | Корневой контейнер |
| `/trip-center` (дочерний) | TripList | Список рейсов |
| `/trip-center/:id` | TripDetails | Детали выбранного рейса |

---

## Структура проекта

```
Autobaza/
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
└── src/
    ├── main.ts
    ├── index.html
    ├── styles.css
    └── app/
        ├── app.ts
        ├── app.html
        ├── app.css
        ├── app.routes.ts
        ├── app.config.ts
        └── trips/
            ├── trip.ts
            ├── mock-trip-list.ts
            ├── trip.routes.ts
            ├── consts/navigation/
            │   └── navigation-path.enum.ts
            ├── services/
            │   └── trip.service.ts
            ├── trip-center/
            │   ├── trip-center.component.ts
            │   ├── trip-center.component.html
            │   └── trip-center.component.css
            ├── trip-list/
            │   ├── trip-list.component.ts
            │   ├── trip-list.component.html
            │   └── trip-list.component.css
            └── trip-details/
                ├── trip-details.component.ts
                ├── trip-details.component.html
                └── trip-details.component.css
```

---

## Запуск

```bash
# Установка зависимостей
npm install

# Запуск сервера разработки
ng serve
```

Приложение доступно по адресу `http://localhost:4200`.

---

## Что сделано

- [x] Создано Angular-приложение через `ng new`
- [x] Подключена Bootstrap (CSS в `angular.json`)
- [x] Добавлен jumbotron с заголовком "Автобаза" (прижат к левому краю)
- [x] Создана папка `trips` с компонентами TripCenter, TripList, TripDetails
- [x] Реализован сервис `TripService` с CRUD-операциями
- [x] Созданы mock-данные с 5 рейсами (на русском языке)
- [x] Настроена маршрутизация с ленивой загрузкой (`loadChildren`)
- [x] Реактивное обновление TripDetails при смене рейса (`paramMap.subscribe`)
- [x] Выделение активного рейса в списке

---

## Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---

# Laboratory Work #2, Part 1 — TypeScript, Angular

Angular application for **Car Database** variant.  
**Variant**: Car Database. Trip Management.

---

## Task

Design an Angular application based on the "Car Database" domain:

- Create application with `ng new`
- Integrate Bootstrap, add jumbotron to the root component
- Create `trips` folder with components: **TripCenter**, **TripList**, **TripDetails**
- Configure routing with lazy loading (`loadChildren`)
- Implement a CRUD service
- Create mock data with trips (id, description, driver)

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" height="28"/>
  <img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" height="28"/>
  <img src="https://img.shields.io/badge/RxJS-B7178C?style=for-the-badge&logo=reactivex&logoColor=white" alt="RxJS" height="28"/>
</p>

---

## Routing

Lazy loading via `loadChildren` in `app.routes.ts`.

| Path | Component | Description |
|------|-----------|-------------|
| `/` | → redirect | Redirects to `/trip-center` |
| `/trip-center` | TripCenter | Root container |
| `/trip-center` (child) | TripList | Trip list |
| `/trip-center/:id` | TripDetails | Selected trip details |

---

## Project Structure

```
Autobaza/
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
└── src/
    ├── main.ts
    ├── index.html
    ├── styles.css
    └── app/
        ├── app.ts
        ├── app.html
        ├── app.css
        ├── app.routes.ts
        ├── app.config.ts
        └── trips/
            ├── trip.ts
            ├── mock-trip-list.ts
            ├── trip.routes.ts
            ├── consts/navigation/
            │   └── navigation-path.enum.ts
            ├── services/
            │   └── trip.service.ts
            ├── trip-center/
            │   ├── trip-center.component.ts
            │   ├── trip-center.component.html
            │   └── trip-center.component.css
            ├── trip-list/
            │   ├── trip-list.component.ts
            │   ├── trip-list.component.html
            │   └── trip-list.component.css
            └── trip-details/
                ├── trip-details.component.ts
                ├── trip-details.component.html
                └── trip-details.component.css
```

---

## Run

```bash
# Install dependencies
npm install

# Start development server
ng serve
```

Application is available at `http://localhost:4200`.

---

## Done

- [x] Created Angular application via `ng new`
- [x] Integrated Bootstrap (CSS in `angular.json`)
- [x] Added jumbotron with "Автобаза" title (left-aligned)
- [x] Created `trips` folder with TripCenter, TripList, TripDetails components
- [x] Implemented `TripService` with CRUD operations
- [x] Created mock data with 5 trips (in Russian)
- [x] Configured routing with lazy loading (`loadChildren`)
- [x] Reactive TripDetails update on trip selection (`paramMap.subscribe`)
- [x] Active trip highlighting in the list

---

## Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com
