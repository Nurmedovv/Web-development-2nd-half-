# Лабораторная работа №2 — TypeScript, Angular, Firebase (часть 3)

**Вариант 8 — Автобаза. Управление рейсами.**

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

---

## Условие (часть 3)

> Развернуть приложение на облачной платформе Firebase. Добавить к Firebase проекту NoSQL базу данных Cloud Firestore. В базе создать коллекцию согласно варианта и добавить документы с полями, соответствующими варианту. В папке `services` сгенерировать сервис `firestore.service` с методами для работы с Cloud Firestore.

### Чек-лист выполнения

- [x] Приложение развёрнуто на Firebase (Hosting)
- [x] Подключена Cloud Firestore (NoSQL)
- [x] Коллекция `trips` (по варианту 8)
- [x] Документы с полями `id`, `description`, `driver`
- [x] Сервис `services/firestore.service.ts` с CRUD-методами

---

## Стек

<p align="left">
  <img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" height="28"/>
  <img src="https://img.shields.io/badge/Firebase_Hosting-FFA000?style=for-the-badge&logo=firebase&logoColor=black" height="28"/>
  <img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white" height="28"/>
  <img src="https://img.shields.io/badge/Angular_Signals-DD0031?style=for-the-badge&logo=angular&logoColor=white" height="28"/>
</p>

---

## Структура проекта

```
Autobaza/
├── firebase.json              # Hosting + Firestore (куда деплоить)
├── .firebaserc                # привязка к проекту lab2part3-31c63
├── firestore.rules            # правила доступа к базе
├── firestore.indexes.json     # индексы
├── angular.json
├── package.json               # + зависимость firebase
└── src/
    ├── main.ts
    ├── index.html
    └── app/
        ├── app.ts                 # при старте → loadFromFirestore()
        ├── app.html               # jumbotron + router-outlet
        ├── app.routes.ts          # loadChildren (lazy)
        └── trips/
            ├── trip.ts            # interface Trip
            ├── mock-trip-list.ts  # 5 mock-рейсов
            ├── trip.routes.ts     # вложенные маршруты
            ├── trip.store.ts      # Signal Store + синхронизация с Firestore
            ├── consts/navigation/
            │   └── navigation-path.enum.ts
            ├── services/
            │   ├── trip.service.ts       # CRUD (часть 1, RxJS)
            │   ├── firebase.config.ts    # конфиг Firebase (часть 3)
            │   └── firestore.service.ts  # CRUD Firestore (часть 3)
            ├── trip-center/
            ├── trip-list/
            ├── trip-details/
            └── add-trip/           # форма на Signals (часть 2)
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

export function isFirebaseConfigured(): boolean { ... }
```

Конфиг взят из Firebase Console. Если ключи пустые — приложение работает на mock-данных.

### 2. `services/firestore.service.ts` — сервис Cloud Firestore

| Метод | Операция Firestore | Описание |
|-------|--------------------|----------|
| `getTrips()` | `onSnapshot` | подписка на всю коллекцию (live) |
| `getTrip(id)` | `onSnapshot` | подписка на один документ |
| `create(trip)` | `setDoc` | добавление документа |
| `update(trip)` | `setDoc` + `merge` | обновление полей |
| `delete(trip)` | `deleteDoc` | удаление документа |
| `seedTrips()` | `setDoc` в цикле | первый запуск: mock → Firestore |

Ключевой момент — **реактивное чтение**:

```typescript
return new Observable<Trip[]>(subscriber => {
  const unsub = onSnapshot(collection(db, 'trips'), snapshot => {
    subscriber.next(snapshot.docs.map(...));
  });
  return () => unsub();
});
```

`onSnapshot` присылает данные при **любом** изменении в базе — в т.ч. из консоли Firebase или другого клиента.

### 3. `trip.store.ts` — двойная запись

```typescript
create(trip: Trip) {
  this.trips.update(list => [...list, trip]);  // 1) локально, UI мгновенно
  if (isFirebaseConfigured()) {
    void this.firestore.create(trip);           // 2) в облако, фоном
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

### 4. `app.ts` — запуск при старте

```typescript
ngOnInit(): void {
  void this.store.loadFromFirestore();
}
```

### 5. `firestore.rules` — правила доступа

```
match /{document=**} {
  allow read, write: if true;   // открыто для демо (без Auth)
}
```

### 6. `firebase.json` + `.firebaserc` — деплой

```json
{ "hosting": { "public": "dist/Autobaza/browser" } }
```

---

## Архитектура данных

```
┌──────────────┐   signal/computed   ┌─────────────┐
│  Компоненты  │ ◄─────────────────► │  TripStore  │
└──────────────┘                     └──────┬──────┘
                                            │ create/update/delete
                                            ▼
                                     ┌──────────────┐
                                     │  Firestore   │
                                     │  Service     │
                                     └──────┬───────┘
                                            │ onSnapshot / setDoc
                                            ▼
                                     ┌──────────────┐
                                     │    Cloud     │
                                     │  Firestore   │
                                     │  trips/{id}  │
                                     └──────────────┘
```

1. Пользователь нажимает «Добавить» → `store.create()` → Signal обновляет UI
2. Параллельно `firestore.create()` пишет документ в облако
3. `onSnapshot` возвращает данные → `trips.set(remote)` → UI синхронизирован

---

## Маршрутизация

| Путь | Компонент | Описание |
|------|-----------|----------|
| `/` | → redirect | → `/trip-center` |
| `/trip-center` | TripCenter → TripList | список рейсов |
| `/trip-center/:id` | → TripDetails | детали рейса |
| `/addTrip` | AddTrip | форма добавления |
| `/addTrip/:id` | AddTrip | форма редактирования |

Ленивая загрузка: `loadChildren` в `app.routes.ts`.

---

## Angular Signals (часть 2, осталось базой)

```typescript
trips = signal<Trip[]>([...TRIPS]);       // хранилище
trips.update(list => [...list, trip]);    // иммутабельное обновление
formValid = computed(() => ...);          // производная (валидация)
```

---

## Запуск локально

```bash
npm install
npm start          # http://localhost:4200
```

## Деплой после правок

```bash
npm run build
firebase deploy --only hosting
```

## Firestore

```bash
firebase deploy --only firestore:rules
```

Данные: [Firestore Console](https://console.firebase.google.com/project/lab2part3-31c63/firestore/databases/-default-/data)

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

## Условие частей 1–2 (для контекста)

**Часть 1:** `ng new`, Bootstrap, jumbotron, папка `trips` (TripCenter/TripList/TripDetails), lazy routing, CRUD-сервис, mock-данные.

**Часть 2:** форма добавления/обновления/удаления, Angular Signals, ссылка на форму, ввод нового водителя (`datalist`).

---

## Автор

**Нурмедов Азат**
Telegram: @fakevv | Email: azatnurmedovv2554@gmail.com
