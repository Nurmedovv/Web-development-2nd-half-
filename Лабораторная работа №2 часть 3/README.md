# Лабораторная работа №2 — TypeScript, Angular, Firebase (часть 3) / Laboratory Work No. 2 — TypeScript, Angular, Firebase (Part 3)
**Вариант 8 — Автобаза. Управление рейсами. / Variant 8 — Motor Depot. Flight Management.**

Angular-приложение с CRUD-формой на Signals и облачным хранением данных в **Cloud Firestore**, задеплоенное на **Firebase Hosting**.
An Angular application featuring a Signals-based CRUD form and cloud data storage via **Cloud Firestore**, deployed to **Firebase Hosting**.
---## Демо / Demo
**https://web.app**

| Ресурс / Resource | Значение / Value |
|-------------------|------------------|
| Firebase-проект / Firebase Project | `lab2part3-31c63` |
| Firestore-коллекция / Firestore Collection | `trips` |
| Поля документа / Document Fields | `id`, `description`, `driver` |
| Hosting | https://web.app |
| Console | https://google.com |
---## Условие (часть 3) / Assignment (Part 3)> Развернуть приложение на облачной платформе Firebase. Добавить к Firebase проекту NoSQL базу данных Cloud Firestore. В базе создать коллекцию согласно варианта и добавить документы с полями, соответствующими варианту. В папке `services` сгенерировать сервис `firestore.service` с методами для работы с Cloud Firestore.
> Deploy the application to the Firebase cloud platform. Add the Cloud Firestore NoSQL database to the Firebase project. Create a collection in the database according to the assigned variant and add documents with the fields corresponding to the variant. Generate a `firestore.service` service in the `services` folder containing methods for working with Cloud Firestore.
### Чек-лист выполнения / Execution Checklist
- [x] Приложение развёрнуто на Firebase (Hosting) / Application is deployed to Firebase (Hosting)
- [x] Подключена Cloud Firestore (NoSQL) / Cloud Firestore (NoSQL) is integrated
- [x] Коллекция `trips` (по варианту 8) / `trips` collection is created (as per variant 8)
- [x] Документы с полями `id`, `description`, `driver` / Documents include fields: `id`, `description`, `driver`
- [x] Сервис `services/firestore.service.ts` с CRUD-методами / `services/firestore.service.ts` service with CRUD methods is implemented
---## Стек / Stack<p align="left">
  <img src="https://shields.io" height="28"/>
  <img src="https://shields.io" height="28"/>
  <img src="https://shields.io" height="28"/>
  <img src="https://shields.io" height="28"/>
  <img src="https://shields.io" height="28"/>
  <img src="https://shields.io" height="28"/></p>
---## Структура проекта / Project Structure

Autobaza/
├── firebase.json # Hosting + Firestore (куда деплоить / where to deploy)
├── .firebaserc # привязка к проекту / binding to project lab2part3-31c63
├── firestore.rules # правила доступа к базе / database security rules
├── firestore.indexes.json # индексы / indexes
├── angular.json
├── package.json # + зависимость / dependency: firebase
└── src/
├── main.ts
├── index.html
└── app/
├── app.ts # при старте → loadFromFirestore() / on init → loadFromFirestore()
├── app.html # jumbotron + router-outlet
├── app.routes.ts # loadChildren (lazy)
└── trips/
├── trip.ts # interface Trip
├── mock-trip-list.ts # 5 mock-рейсов / 5 mock trips
├── trip.routes.ts # вложенные маршруты / nested routes
├── trip.store.ts # Signal Store + синхронизация с Firestore / Signal Store + Firestore sync
├── consts/navigation/
│ └── navigation-path.enum.ts
├── services/
│ ├── trip.service.ts # CRUD (часть 1, RxJS) / CRUD (Part 1, RxJS)
│ ├── firebase.config.ts # конфиг Firebase (часть 3) / Firebase config (Part 3)
│ └── firestore.service.ts # CRUD Firestore (часть 3) / Firestore CRUD (Part 3)
├── trip-center/
├── trip-list/
├── trip-details/
└── add-trip/ # форма на Signals (часть 2) / Signals-based form (Part 2)


---

## Что нового в части 3 (по сравнению с частью 2) / What's New in Part 3 (Compared to Part 2)

### 1. `services/firebase.config.ts` — подключение к проекту / Connecting to the Project

```typescript
export const firebaseConfig = {
  apiKey: 'AIzaSy...',
  authDomain: '://firebaseapp.com',
  projectId: 'lab2part3-31c63',
  appId: '1:498338749344:web:...'
};

export function isFirebaseConfigured(): boolean { ... }
```

Конфиг взят из Firebase Console. Если ключи пустые — приложение работает на mock-данных.
The configuration is retrieved from the Firebase Console. If the keys are empty, the application falls back to mock data.

### 2. `services/firestore.service.ts` — сервис Cloud Firestore / Cloud Firestore Service

| Метод / Method | Операция Firestore / Firestore Operation | Описание / Description |
|----------------|------------------------------------------|------------------------|
| `getTrips()` | `onSnapshot` | подписка на всю коллекцию (live) / subscribe to the entire collection (live) |
| `getTrip(id)` | `onSnapshot` | подписка на один документ / subscribe to a single document |
| `create(trip)` | `setDoc` | добавление документа / add a document |
| `update(trip)` | `setDoc` + `merge` | обновление полей / update fields |
| `delete(trip)` | `deleteDoc` | удаление документа / delete a document |
| `seedTrips()` | `setDoc` в цикле / in a loop | первый запуск: mock → Firestore / initial run: mock → Firestore |

Ключевой момент — **реактивное чтение**:
Key highlight — **reactive reading**:

```typescript
return new Observable<Trip[]>(subscriber => {
  const unsub = onSnapshot(collection(db, 'trips'), snapshot => {
    subscriber.next(snapshot.docs.map(...));
  });
  return () => unsub();
});
```

`onSnapshot` присылает данные при **любом** изменении в базе — в т.ч. из консоли Firebase или другого клиента.
`onSnapshot` pushes fresh data upon **any** change in the database — including modifications via the Firebase console or another client.

### 3. `trip.store.ts` — двойная запись / Dual Write

```typescript
create(trip: Trip) {
  this.trips.update(list => [...list, trip]);  // 1) локально, UI мгновенно / locally, UI updates instantly
  if (isFirebaseConfigured()) {
    void this.firestore.create(trip);           // 2) в облако, фоном / to the cloud, in the background
  }
}
```

```typescript
async loadFromFirestore() {
  await this.firestore.seedTrips([...TRIPS]);   // mock → Firestore
  this.firestore.getTrips().subscribe(remote => {
    if (remote.length > 0) this.trips.set(remote);
  });
}
```

### 4. `app.ts` — запуск при старте / Initialization on Startup

```typescript
ngOnInit(): void {
  void this.store.loadFromFirestore();
}
```

### 5. `firestore.rules` — правила доступа / Security Rules


match /{document=**} {
allow read, write: if true; // открыто для демо (без Auth) / open for demo purposes (no Auth)
}


### 6. `firebase.json` + `.firebaserc` — деплой / Deployment

```json
{ "hosting": { "public": "dist/Autobaza/browser" } }
```

---

## Архитектура данных / Data Architecture


┌──────────────┐ signal/computed ┌─────────────┐
│ Компоненты │ ◄─────────────────► │ TripStore │
│ Components │ └──────┬──────┘
└──────────────┘ │ create/update/delete
▼
┌──────────────┐
│ Firestore │
│ Service │
└──────┬───────┘
│ onSnapshot / setDoc
▼
┌──────────────┐
│ Cloud │
│ Firestore │
│ trips/{id} │
└──────────────┘


1. Пользователь нажимает «Добавить» → `store.create()` → Signal обновляет UI
2. Параллельно `firestore.create()` пишет документ в облако
3. `onSnapshot` возвращает данные → `trips.set(remote)` → UI синхронизирован

1. User clicks "Add" → `store.create()` → Signal updates the UI
2. Simultaneously, `firestore.create()` writes the document to the cloud
3. `onSnapshot` returns the updated data → `trips.set(remote)` → UI is fully synchronized

---

## Маршрутизация / Routing

| Путь / Path | Компонент / Component | Описание / Description |
|-------------|-----------------------|------------------------|
| `/` | → redirect | → `/trip-center` |
| `/trip-center` | TripCenter → TripList | список рейсов / trip list |
| `/trip-center/:id` | → TripDetails | детали рейса / trip details |
| `/addTrip` | AddTrip | форма добавления / add form |
| `/addTrip/:id` | AddTrip | форма редактирования / edit form |

Ленивая загрузка: `loadChildren` в `app.routes.ts`.
Lazy loading: handled via `loadChildren` in `app.routes.ts`.

---

## Angular Signals (часть 2, осталось базой) / Angular Signals (Part 2, remains as the foundation)

```typescript
trips = signal<Trip[]>([...TRIPS]);       // хранилище / data store
trips.update(list => [...list, trip]);    // иммутабельное обновление / immutable update
formValid = computed(() => ...);          // производная (валидация) / derived state (validation)
```

---

## Запуск локально / Local Setup

```bash
npm install
npm start          # http://localhost:4200
```

## Деплой после правок / Deployment After Changes

```bash
npm run build
firebase deploy --only hosting
```

## Firestore

```bash
firebase deploy --only firestore:rules
```

Данные: [Firestore Console](https://google.com)

---

## Соответствие условию части 3 / Alignment with Part 3 Requirements

| Требование / Requirement | Файл / факт // File / Evidence |
|--------------------------|--------------------------------|
| Приложение на Firebase / App on Firebase | Hosting: `lab2part3-31c63.web.app` |
| NoSQL Cloud Firestore | база `lab2part3-31c63`, europe-west1 / database `lab2part3-31c63`, europe-west1 |
| Коллекция по варианту / Collection as per variant | `trips` |
| Документы с полями варианта / Document fields | `{ id, description, driver }` |
| `services/firestore.service` | `src/app/trips/services/firestore.service.ts` |
| Методы для Cloud Firestore / Cloud Firestore methods | `getTrips`, `getTrip`, `create`, `update`, `delete`, `seedTrips` |

---

## Условие частей 1–2 (для контекста) / Assignment Requirements for Parts 1–2 (For Context)

**Часть 1:** `ng new`, Bootstrap, jumbotron, папка `trips` (TripCenter/TripList/TripDetails), lazy routing, CRUD-сервис, mock-данные.
**Part 1:** `ng new`, Bootstrap, jumbotron, `trips` folder (TripCenter/TripList/TripDetails), lazy routing, CRUD service, mock data.

**Часть 2:** форма добавления/обновления/удаления, Angular Signals, ссылка на форму, ввод нового водителя (`datalist`).
**Part 2:** add/update/delete form, Angular Signals, links navigation to the form, custom driver input (`datalist`).

---

## Автор / Author

**Нурмедов Азат / Azat Nurmedov**
Telegram: @fakevv | Email: azatnurmedovv2554@gmail.com



