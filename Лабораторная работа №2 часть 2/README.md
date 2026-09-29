# Лабораторная работа №2 — TypeScript, Angular (части 1 + 2)

Разработка Angular-приложения для варианта **Автобаза**.  
**Вариант**: Автобаза. Управление рейсами.

---

## Условие

На основе предметной области "Автобаза" спроектировать Angular-приложение:

### Часть 1
- Создать приложение командой `ng new`
- Подключить Bootstrap, добавить jumbotron в корневой компонент
- Создать папку `trips` с компонентами: **TripCenter**, **TripList**, **TripDetails**
- Настроить маршрутизацию с ленивой загрузкой (`loadChildren`)
- Реализировать сервис CRUD-операций
- Создать mock-данные с рейсами (id, description, driver)

### Часть 2
- Разработать форму для добавления, обновления и удаления элементов
- Использовать **Angular Signals** для управления состоянием
- Добавить ссылку на форму в приложение
- Реализовать форму с возможностью ввода нового водителя

---

## Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" height="28"/>
  <img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" height="28"/>
  <img src="https://img.shields.io/badge/Angular_Signals-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular Signals" height="28"/>
</p>

---

## Архитектура

```
app (jumbotron + router-outlet + ссылка «Форма рейса»)
 └── trip.routes (ленивая загрузка)
      ├── trip-center ──► trip-list ──► trip-details (по :id)
      ├── addTrip            (форма добавления)
      └── addTrip/:id        (форма редактирования)

TripStore (signal) ◄── все компоненты читают отсюда
```

- **TripStore** — единый источник данных на Angular Signals
- **add-trip** — форма добавления/редактирования/удаления рейсов
- Все CRUD-операции работают через обновление сигнала → UI обновляется автоматически

---

## Маршрутизация

Ленивая загрузка через `loadChildren` в `app.routes.ts`.

| Путь | Компонент | Описание |
|------|-----------|----------|
| `/` | → redirect | Редирект на `/trip-center` |
| `/trip-center` | TripCenter → TripList | Список рейсов |
| `/trip-center/:id` | TripCenter → TripList → TripDetails | Детали рейса |
| `/addTrip` | AddTrip | Форма добавления нового рейса |
| `/addTrip/:id` | AddTrip | Форма редактирования существующего рейса |

---

## Angular Signals — что использовано

### signal() — реактивное хранилище
```typescript
trips = signal<Trip[]>([...TRIPS]);  // начальное значение
trips.set([...]);                     // замена
trips.update(list => [...list, x]);   // обновление на основе предыдущего
```

### computed() — производный сигнал
```typescript
formValid = computed(() => this.tripId() > 0 && this.tripDescription().trim().length > 0);
```
Пересчитывается автоматически при изменении зависимостей. Ленивый и кэшируемый.

### Иммутабельное обновление
```typescript
// Правильно — новый массив, новая ссылка → сигнал обновляется
this.trips.update(list => [...list, trip]);

// Неправильно — push мутирует тот же массив → сигнал не видит изменения
```

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
        ├── app.ts                          # Корневой компонент
        ├── app.html                        # Jumbotron + router-outlet
        ├── app.css
        ├── app.routes.ts                   # Ленивая загрузка trips
        ├── app.config.ts
        └── trips/
            ├── trip.ts                     # Интерфейс Trip
            ├── mock-trip-list.ts           # Mock-данные (5 рейсов)
            ├── trip.routes.ts              # Маршруты Trips
            ├── trip.store.ts               # Signal Store (CRUD)
            ├── consts/navigation/
            │   └── navigation-path.enum.ts # Enum путей
            ├── services/
            │   └── trip.service.ts         # Сервис (часть 1)
            ├── trip-center/
            │   ├── trip-center.component.ts
            │   └── trip-center.component.html
            ├── trip-list/
            │   ├── trip-list.component.ts  # Читает trips из store
            │   └── trip-list.component.html
            ├── trip-details/
            │   ├── trip-details.component.ts  # computed по id
            │   └── trip-details.component.html
            └── add-trip/
                ├── add-trip.component.ts   # Signal Form (часть 2)
                ├── add-trip.component.html
                └── add-trip.component.css
```

---

## Ключевые файлы

| Файл | Назначение |
|------|-----------|
| `trip.store.ts` | Signal Store — CRUD через signal/computed/update |
| `add-trip.component.ts` | Форма на сигналах: валидация, добавление, редактирование, удаление |
| `add-trip.component.html` | Шаблон формы с привязкой сигналов к DOM |
| `trip-list.component.ts` | Список рейсов, читает сигнал из store |
| `trip-details.component.ts` | Детали рейса через computed |
| `trip.routes.ts` | Вложенная маршрутизация с параметрами |

---

## Запуск

```bash
npm install
ng serve
```

Приложение доступно по адресу `http://localhost:4200`.

---

## Что сделано

- [x] Создано Angular-приложение через `ng new`
- [x] Подключена Bootstrap (CSS в `angular.json`)
- [x] Добавлен jumbotron с заголовком «Автобаза» и кнопкой «Форма рейса»
- [x] Создана папка `trips` с компонентами TripCenter, TripList, TripDetails
- [x] Реализован сервис `TripService` с CRUD-операциями
- [x] Созданы mock-данные с 5 рейсами (на русском языке)
- [x] Настроена маршрутизация с ленивой загрузкой (`loadChildren`)
- [x] Реактивное обновление TripDetails при смене рейса (`paramMap.subscribe`)
- [x] Выделение активного рейса в списке
- [x] **Signal Store** (`trip.store.ts`) — управление состоянием через `signal` / `computed` / `update`
- [x] **Signal Form** (`add-trip`) — форма добавления/редактирования/удаления на сигналах
- [x] Валидация формы через `computed` (id > 0, описание и водитель не пустые)
- [x] Добавление/обновление/удаление рейсов через обновление сигнала
- [x] Автоматическое обновление списка при CRUD-операциях
- [x] Поле водителя с datalist — возможность ввода нового водителя
- [x] Навигация: форма ↔ список ↔ детали

---

## Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---

---

# Laboratory Work #2 — TypeScript, Angular (Parts 1 + 2)

Angular application for **Car Database** variant.  
**Variant**: Car Database. Trip Management.

---

## Task

Design an Angular application based on the "Car Database" domain:

### Part 1
- Create application with `ng new`
- Integrate Bootstrap, add jumbotron to the root component
- Create `trips` folder with components: **TripCenter**, **TripList**, **TripDetails**
- Configure routing with lazy loading (`loadChildren`)
- Implement a CRUD service
- Create mock data with trips (id, description, driver)

### Part 2
- Develop a form for adding, updating, and deleting elements
- Use **Angular Signals** for state management
- Add a link to the form in the application
- Implement a form with the ability to enter a new driver

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" height="28"/>
  <img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" height="28"/>
  <img src="https://img.shields.io/badge/Angular_Signals-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular Signals" height="28"/>
</p>

---

## Routing

Lazy loading via `loadChildren` in `app.routes.ts`.

| Path | Component | Description |
|------|-----------|-------------|
| `/` | → redirect | Redirects to `/trip-center` |
| `/trip-center` | TripCenter → TripList | Trip list |
| `/trip-center/:id` | TripCenter → TripList → TripDetails | Trip details |
| `/addTrip` | AddTrip | Add new trip form |
| `/addTrip/:id` | AddTrip | Edit existing trip form |

---

## Angular Signals — Usage

### signal() — reactive storage
```typescript
trips = signal<Trip[]>([...TRIPS]);
trips.update(list => [...list, trip]);
```

### computed() — derived signal
```typescript
formValid = computed(() => this.tripId() > 0 && this.tripDescription().trim().length > 0);
```

### Immutable updates
```typescript
this.trips.update(list => [...list, trip]);  // new array → signal detects change
```

---

## Project Structure

```
Autobaza/
├── angular.json
├── package.json
└── src/app/
    ├── app.ts / app.html / app.routes.ts
    └── trips/
        ├── trip.ts                     # Trip interface
        ├── mock-trip-list.ts           # Mock data
        ├── trip.routes.ts              # Trips routing
        ├── trip.store.ts               # Signal Store (CRUD)
        ├── consts/navigation/
        │   └── navigation-path.enum.ts
        ├── services/
        │   └── trip.service.ts
        ├── trip-center/
        ├── trip-list/
        ├── trip-details/
        └── add-trip/                   # Signal Form (Part 2)
```

---

## Run

```bash
npm install
ng serve
```

Application is available at `http://localhost:4200`.

---

## Done

- [x] Created Angular application via `ng new`
- [x] Integrated Bootstrap with jumbotron
- [x] Created `trips` folder with TripCenter, TripList, TripDetails components
- [x] Implemented `TripService` with CRUD operations
- [x] Created mock data with 5 trips (in Russian)
- [x] Configured routing with lazy loading (`loadChildren`)
- [x] Reactive TripDetails update (`paramMap.subscribe`)
- [x] Active trip highlighting in the list
- [x] **Signal Store** — state management via `signal` / `computed` / `update`
- [x] **Signal Form** — add/edit/delete form on signals
- [x] Form validation via `computed`
- [x] CRUD via signal updates with automatic UI refresh
- [x] Driver input with datalist for new drivers
- [x] Navigation: form ↔ list ↔ details

---

## Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com
