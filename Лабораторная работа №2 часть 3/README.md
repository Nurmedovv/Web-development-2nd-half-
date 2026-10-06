# Лабораторная работа №2 — TypeScript, Angular, Firebase (часть 3)

Разработка Angular-приложения для варианта **Автобаза**.  
**Вариант 8**: Автобаза. Управление рейсами.

Angular-приложение с CRUD-формой на Signals и облачным хранением данных в **Cloud Firestore**, задеплоенное на **Firebase Hosting**.

---

## Демо

**https://lab2part3-31c63.web.app**

| Ресурс | Значение |
|--------|----------|
| Firebase-проект | `lab2part3-31c63` |
| Firestore-коллекция | `trips` |
| Поля документа | `id`, `description`, `driver` |
| Hosting | https://lab2part3-31c63.web.app |
| Console | https://console.firebase.google.com/project/lab2part3-31c63/overview |
| Данные Firestore | https://console.firebase.google.com/project/lab2part3-31c63/firestore/databases/-default-/data |

---

## Условие

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

### Часть 3
- Развернуть приложение на облачной платформе Firebase
- Добавить к Firebase проекту NoSQL базу данных Cloud Firestore
- В базе создать коллекцию согласно варианта и документы с полями варианта
- В папке `services` сгенерировать сервис `firestore.service` с методами для Cloud Firestore

### Чек-лист части 3

- [x] Приложение развёрнуто на Firebase (Hosting)
- [x] Подключена Cloud Firestore (NoSQL)
- [x] Коллекция `trips` (по варианту 8)
- [x] Документы с полями `id`, `description`, `driver`
- [x] Сервис `services/firestore.service.ts` с CRUD-методами
- [x] Публичная ссылка работает

---

## Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" height="28"/>
  <img src="https://img.shields.io/badge/Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firestore" height="28"/>
  <img src="https://img.shields.io/badge/Firebase_Hosting-FFA000?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase Hosting" height="28"/>
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
       │
       └── FirestoreService ──► Cloud Firestore (trips)
              ▲
              └── firebase.config.ts
```

---

## Что нового в части 3 (по сравнению с частью 2)

### 1. `services/firebase.config.ts` — подключение к проекту

```typescript
export const firebaseConfig = {
  apiKey: 'AIzaSy...',
  authDomain: 'lab2part3-31c63.firebaseapp.com',
  projectId: 'lab2part3-31c63',
  appId: '1:498338749344:web:...'
};

export const FIRESTORE_COLLECTION = 'trips';

export function isFirebaseConfigured(): boolean {
  return Boolean(
    firebaseConfig.apiKey &&
      firebaseConfig.projectId &&
      firebaseConfig.appId
  );
}
```

Конфиг взят из Firebase Console. Если ключи пустые — приложение работает на mock-данных и не падает.

### 2. `services/firestore.service.ts` — сервис Cloud Firestore

| Метод | Операция Firestore | Описание |
|-------|--------------------|----------|
| `getTrips()` | `onSnapshot` | подписка на всю коллекцию (live) |
| `getTrip(id)` | `onSnapshot` | подписка на один документ |
| `create(trip)` | `setDoc` | добавление документа |
| `update(trip)` | `setDoc` + `merge` | обновление полей |
| `delete(trip)` | `deleteDoc` | удаление документа |
| `seedTrips()` | `setDoc` в цикле | первый запуск: mock → Firestore |

**Ленивая инициализация:**

```typescript
private ensureDb(): Firestore | null {
  if (!this.useFirestore) return null;       // нет конфига → без облака
  if (!this.db) {
    this.app = initializeApp(firebaseConfig); // подключение 1 раз
    this.db = getFirestore(this.app);
  }
  return this.db;                             // дальше — кэш
}
```

**Реактивное чтение (`onSnapshot` вместо обычного GET):**

```typescript
return new Observable<Trip[]>((subscriber) => {
  const unsubscribe = onSnapshot(
    collection(db, this.collectionName),
    (snapshot) => {
      const trips = snapshot.docs.map((d) => ({ ... }));
      subscriber.next(trips);
    },
    (error) => subscriber.error(error)
  );
  return () => unsubscribe();
});
```

`onSnapshot` присылает данные при **любом** изменении в Firestore — из сайта, консоли Firebase или другого клиента.

**id документа = id рейса:**

```typescript
const ref = doc(db, this.collectionName, String(trip.id)); // trips/3
await setDoc(ref, { id, description, driver });
```

### 3. `trip.store.ts` — двойная запись

```typescript
create(trip: Trip) {
  this.trips.update((list) => [...list, trip]);  // 1) локально, UI мгновенно
  if (isFirebaseConfigured()) {
    void this.firestore.create(trip);            // 2) в облако, фоном
  }
}
```

```typescript
async loadFromFirestore(): Promise<void> {
  if (!isFirebaseConfigured()) return;
  await this.firestore.seedTrips([...TRIPS]);    // mock → Firestore
  this.firestore.getTrips().subscribe((remote) => {
    if (remote.length > 0) this.trips.set(remote);
  });
}
```

### 4. `app.ts` — запуск при старте

```typescript
ngOnInit(): void {
  void this.store.loadFromFirestore(); // seed + подписка
}
```

### 5. `trip.service.ts` — RxJS-сервис с fallback на mock

```typescript
getTrips(): Observable<Trip[]> {
  if (isFirebaseConfigured()) {
    return this.firestore.getTrips().pipe(
      map((remote) => (remote.length > 0 ? remote : this.trips))
    );
  }
  return of(this.trips);
}
```

### 6. `firestore.rules` — правила доступа

```
match /{document=**} {
  allow read, write: if true;   // открыто для демо (без Auth)
}
```

### 7. `firebase.json` + `.firebaserc` — конфиг деплоя

```json
{ "hosting": { "public": "dist/Autobaza/browser" } }
```

`.firebaserc` привязывает папку к проекту `lab2part3-31c63`.

### 8. `package.json` — + зависимость firebase

```diff
+ "firebase": "^12.19.0"
```

---

## Маршрутизация

Ленивая загрузка через `loadChildren` в `app.routes.ts`.

| Путь | Компонент | Описание |
|------|-----------|----------|
| `/` | → redirect | Редирект на `/trip-center` |
| `/trip-center` | TripCenter → TripList | Список рейсов |
| `/trip-center/:id` | → TripDetails | Детали рейса |
| `/addTrip` | AddTrip | Форма добавления |
| `/addTrip/:id` | AddTrip | Форма редактирования |

---

## Angular Signals — что использовано

```typescript
trips = signal<Trip[]>([...TRIPS]);       // хранилище
trips.update(list => [...list, trip]);    // иммутабельное обновление
formValid = computed(() => ...);          // производная (валидация)
```

Иммутабельное обновление: всегда новый массив. `push` мутирует тот же массив — сигнал не видит изменения.

---

## Структура проекта

```
Autobaza/
├── firebase.json
├── .firebaserc
├── firestore.rules
├── firestore.indexes.json
├── angular.json
├── package.json
└── src/
    ├── main.ts
    ├── index.html
    └── app/
        ├── app.ts
        ├── app.html
        ├── app.routes.ts
        └── trips/
            ├── trip.ts
            ├── mock-trip-list.ts
            ├── trip.routes.ts
            ├── trip.store.ts
            ├── consts/navigation/
            │   └── navigation-path.enum.ts
            ├── services/
            │   ├── trip.service.ts
            │   ├── firebase.config.ts
            │   └── firestore.service.ts
            ├── trip-center/
            ├── trip-list/
            ├── trip-details/
            └── add-trip/
```

---

## Ключевые файлы

| Файл | Назначение |
|------|-----------|
| `trip.store.ts` | Signal Store + синхронизация с Firestore |
| `firestore.service.ts` | CRUD для Cloud Firestore (`onSnapshot`, `setDoc`) |
| `firebase.config.ts` | конфиг проекта + `isFirebaseConfigured()` |
| `add-trip.component.ts` | форма на сигналах: валидация, CRUD |
| `trip.routes.ts` | вложенная маршрутизация |
| `firestore.rules` | правила доступа к базе |
| `firebase.json` | куда деплоить Hosting и правила |

---

## Соответствие условию части 3

| Требование | Файл / факт |
|------------|-------------|
| Приложение на Firebase | Hosting: `lab2part3-31c63.web.app` |
| NoSQL Cloud Firestore | база `lab2part3-31c63`, europe-west1 |
| Коллекция по варианту | `trips` |
| Документы с полями варианта | `{ id, description, driver }` |
| `services/firestore.service` | `src/app/trips/services/firestore.service.ts` |
| Методы для Cloud Firestore | `getTrips`, `getTrip`, `create`, `update`, `delete`, `seedTrips` |

---

## Запуск

```bash
npm install
npm start
```

Приложение доступно по адресу `http://localhost:4200`.

## Деплой после правок

```bash
npm run build
firebase deploy --only hosting
firebase deploy --only firestore:rules
```

## Данные Firestore

[Firestore Console](https://console.firebase.google.com/project/lab2part3-31c63/firestore/databases/-default-/data) → коллекция **`trips`**.

Пример документа:

```json
{ "id": 1, "description": "Перевозка грузов", "driver": "Дроздов А.В." }
```

---

## Что сделано

- [x] Создано Angular-приложение через `ng new`
- [x] Подключена Bootstrap (CSS в `angular.json`)
- [x] Добавлен jumbotron с заголовком «Автобаза» и кнопкой «Форма рейса»
- [x] Создана папка `trips` с компонентами TripCenter, TripList, TripDetails
- [x] Реализован сервис `TripService` с CRUD-операциями
- [x] Созданы mock-данные с 5 рейсами
- [x] Настроена маршрутизация с ленивой загрузкой (`loadChildren`)
- [x] **Signal Store** (`trip.store.ts`) — управление состоянием через `signal` / `computed` / `update`
- [x] **Signal Form** (`add-trip`) — форма добавления/редактирования/удаления на сигналах
- [x] Валидация формы через `computed`
- [x] Поле водителя с datalist — возможность ввода нового водителя
- [x] **Part 3 — Firebase/Firestore**: `firestore.service.ts` с CRUD-методами
- [x] Конфигурация Firebase в `firebase.config.ts`
- [x] Коллекция Firestore: `trips` (id, description, driver)
- [x] Seed mock-данных в Firestore при первом запуске
- [x] Fallback на mock-данные, если Firebase не настроен
- [x] Приложение задеплоено на Firebase Hosting

---

## Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---
---

# Laboratory Work №2 — TypeScript, Angular, Firebase (Part 3)

Angular application for **Car Database** variant.  
**Variant 8**: Car Database. Trip Management.

Angular application with a CRUD form on **Signals** and cloud storage in **Cloud Firestore**, deployed to **Firebase Hosting**.

---

## Demo

**https://lab2part3-31c63.web.app**

| Resource | Value |
|----------|-------|
| Firebase project | `lab2part3-31c63` |
| Firestore collection | `trips` |
| Document fields | `id`, `description`, `driver` |
| Hosting URL | https://lab2part3-31c63.web.app |
| Firebase Console | https://console.firebase.google.com/project/lab2part3-31c63/overview |
| Firestore data | https://console.firebase.google.com/project/lab2part3-31c63/firestore/databases/-default-/data |

---

## Task

### Part 1
- Create the app with `ng new`
- Integrate Bootstrap, add a jumbotron to the root component
- Create a `trips` folder with components: **TripCenter**, **TripList**, **TripDetails**
- Configure routing with lazy loading (`loadChildren`)
- Implement a CRUD service
- Create mock trip data (`id`, `description`, `driver`)

### Part 2
- Develop a form for adding, updating, and deleting items
- Use **Angular Signals** for state management
- Add a link to the form in the application
- Implement a form with the ability to enter a new driver

### Part 3
- Deploy the application on the Firebase cloud platform
- Add a Cloud Firestore NoSQL database to the Firebase project
- Create a collection per the variant with documents and matching fields
- Generate `firestore.service` in the `services` folder with methods for Cloud Firestore

### Part 3 checklist

- [x] Application deployed on Firebase (Hosting)
- [x] Cloud Firestore (NoSQL) connected
- [x] Collection `trips` (variant 8)
- [x] Documents with fields `id`, `description`, `driver`
- [x] Service `services/firestore.service.ts` with CRUD methods
- [x] Public demo link works

---

## Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" height="28"/>
  <img src="https://img.shields.io/badge/Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firestore" height="28"/>
  <img src="https://img.shields.io/badge/Firebase_Hosting-FFA000?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase Hosting" height="28"/>
  <img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white" alt="Bootstrap" height="28"/>
  <img src="https://img.shields.io/badge/Angular_Signals-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular Signals" height="28"/>
</p>

---

## Architecture

```
app (jumbotron + router-outlet + "Trip form" link)
 └── trip.routes (lazy loading)
      ├── trip-center ──► trip-list ──► trip-details (by :id)
      ├── addTrip            (add form)
      └── addTrip/:id        (edit form)

TripStore (signal) ◄── all components read from here
       │
       └── FirestoreService ──► Cloud Firestore (trips)
              ▲
              └── firebase.config.ts
```

---

## What Is New in Part 3 (compared to Part 2)

### 1. `services/firebase.config.ts` — project connection

```typescript
export const firebaseConfig = {
  apiKey: 'AIzaSy...',
  authDomain: 'lab2part3-31c63.firebaseapp.com',
  projectId: 'lab2part3-31c63',
  appId: '1:498338749344:web:...'
};

export const FIRESTORE_COLLECTION = 'trips';

export function isFirebaseConfigured(): boolean {
  return Boolean(
    firebaseConfig.apiKey &&
      firebaseConfig.projectId &&
      firebaseConfig.appId
  );
}
```

Config from Firebase Console. If keys are empty, the app falls back to mock data.

### 2. `services/firestore.service.ts` — Cloud Firestore service

| Method | Firestore operation | Description |
|--------|--------------------|-------------|
| `getTrips()` | `onSnapshot` | live subscription to the collection |
| `getTrip(id)` | `onSnapshot` | live subscription to one document |
| `create(trip)` | `setDoc` | add a document |
| `update(trip)` | `setDoc` + `merge` | update fields |
| `delete(trip)` | `deleteDoc` | remove a document |
| `seedTrips()` | `setDoc` in a loop | first run: mock → Firestore |

**Lazy initialization:**

```typescript
private ensureDb(): Firestore | null {
  if (!this.useFirestore) return null;
  if (!this.db) {
    this.app = initializeApp(firebaseConfig);
    this.db = getFirestore(this.app);
  }
  return this.db;
}
```

**Reactive reading (`onSnapshot` instead of a one-shot GET):**

```typescript
return new Observable<Trip[]>((subscriber) => {
  const unsubscribe = onSnapshot(
    collection(db, this.collectionName),
    (snapshot) => {
      const trips = snapshot.docs.map((d) => ({ ... }));
      subscriber.next(trips);
    },
    (error) => subscriber.error(error)
  );
  return () => unsubscribe();
});
```

`onSnapshot` pushes data on every Firestore change — from the site, the Firebase console, or any other client.

**Document id = business id:**

```typescript
const ref = doc(db, this.collectionName, String(trip.id)); // trips/3
await setDoc(ref, { id, description, driver });
```

### 3. `trip.store.ts` — dual write

```typescript
create(trip: Trip) {
  this.trips.update((list) => [...list, trip]);  // 1) local — UI is instant
  if (isFirebaseConfigured()) {
    void this.firestore.create(trip);            // 2) cloud — background
  }
}
```

```typescript
async loadFromFirestore(): Promise<void> {
  if (!isFirebaseConfigured()) return;
  await this.firestore.seedTrips([...TRIPS]);
  this.firestore.getTrips().subscribe((remote) => {
    if (remote.length > 0) this.trips.set(remote);
  });
}
```

### 4. `app.ts` — startup hook

```typescript
ngOnInit(): void {
  void this.store.loadFromFirestore();
}
```

### 5. `trip.service.ts` — RxJS service with mock fallback

```typescript
getTrips(): Observable<Trip[]> {
  if (isFirebaseConfigured()) {
    return this.firestore.getTrips().pipe(
      map((remote) => (remote.length > 0 ? remote : this.trips))
    );
  }
  return of(this.trips);
}
```

### 6. `firestore.rules` — security rules

```
match /{document=**} {
  allow read, write: if true;  // open access for the demo (no Auth)
}
```

### 7. `firebase.json` + `.firebaserc` — deploy config

```json
{ "hosting": { "public": "dist/Autobaza/browser" } }
```

`.firebaserc` binds the folder to project `lab2part3-31c63`.

### 8. `package.json` — new dependency

```diff
+ "firebase": "^12.19.0"
```

---

## Routing

Lazy loading via `loadChildren` in `app.routes.ts`.

| Path | Component | Description |
|------|-----------|-------------|
| `/` | → redirect | to `/trip-center` |
| `/trip-center` | TripCenter → TripList | trip list |
| `/trip-center/:id` | → TripDetails | trip details |
| `/addTrip` | AddTrip | add form |
| `/addTrip/:id` | AddTrip | edit form |

---

## Angular Signals — Usage

```typescript
trips = signal<Trip[]>([...TRIPS]);
trips.update(list => [...list, trip]);  // immutable update
formValid = computed(() => ...);        // derived validation
```

Always create a new array — `push` mutates in place and the Signal will not detect the change.

---

## Project Structure

```
Autobaza/
├── firebase.json
├── .firebaserc
├── firestore.rules
├── firestore.indexes.json
├── angular.json
├── package.json
└── src/
    ├── main.ts
    ├── index.html
    └── app/
        ├── app.ts
        ├── app.html
        ├── app.routes.ts
        └── trips/
            ├── trip.ts
            ├── mock-trip-list.ts
            ├── trip.routes.ts
            ├── trip.store.ts
            ├── consts/navigation/
            │   └── navigation-path.enum.ts
            ├── services/
            │   ├── trip.service.ts
            │   ├── firebase.config.ts
            │   └── firestore.service.ts
            ├── trip-center/
            ├── trip-list/
            ├── trip-details/
            └── add-trip/
```

---

## Key Files

| File | Purpose |
|------|---------|
| `trip.store.ts` | Signal Store + Firestore sync |
| `firestore.service.ts` | Cloud Firestore CRUD (`onSnapshot`, `setDoc`) |
| `firebase.config.ts` | project config + `isFirebaseConfigured()` |
| `add-trip.component.ts` | Signal form: validation, CRUD |
| `trip.routes.ts` | nested routing |
| `firestore.rules` | database access rules |
| `firebase.json` | Hosting and rules deploy target |

---

## Part 3 Compliance

| Requirement | Implementation |
|-------------|----------------|
| Application on Firebase | Hosting: `lab2part3-31c63.web.app` |
| NoSQL Cloud Firestore | database `lab2part3-31c63`, europe-west1 |
| Collection per variant | `trips` |
| Documents with variant fields | `{ id, description, driver }` |
| `services/firestore.service` | `src/app/trips/services/firestore.service.ts` |
| Methods for Cloud Firestore | `getTrips`, `getTrip`, `create`, `update`, `delete`, `seedTrips` |

---

## Run

```bash
npm install
npm start
```

Application is available at `http://localhost:4200`.

## Deploy after changes

```bash
npm run build
firebase deploy --only hosting
firebase deploy --only firestore:rules
```

## Firestore data

[Firestore Console](https://console.firebase.google.com/project/lab2part3-31c63/firestore/databases/-default-/data) → collection **`trips`**.

Document example:

```json
{ "id": 1, "description": "Freight delivery", "driver": "Drozdov A.V." }
```

---

## Done

- [x] Created Angular application via `ng new`
- [x] Integrated Bootstrap with jumbotron
- [x] Created `trips` folder with TripCenter, TripList, TripDetails
- [x] Implemented `TripService` with CRUD operations
- [x] Created mock data with 5 trips
- [x] Configured routing with lazy loading (`loadChildren`)
- [x] **Signal Store** — state management via `signal` / `computed` / `update`
- [x] **Signal Form** — add/edit/delete form on signals
- [x] Form validation via `computed`
- [x] Driver input with datalist for new drivers
- [x] **Part 3 — Firebase/Firestore**: `firestore.service.ts` with CRUD methods
- [x] Firebase config in `firebase.config.ts`
- [x] Firestore collection: `trips` (id, description, driver)
- [x] Seed mock data to Firestore on first run
- [x] Fallback to mock data if Firebase not configured
- [x] Application deployed to Firebase Hosting

---

## Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com
