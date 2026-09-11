# Лабораторная работа №1 — React

Разработка приложения с использованием React.  
**Вариант 8**: Автобаза. Список автомобилей.

---

## 📋 Условие

Разработать React-приложение, состоящее из трех компонентов:

1. **Example** — компонент для знакомства с React, демонстрирующий работу с состоянием.
2. **Regions** — компонент для поиска областей Беларуси по подстроке с динамической фильтрацией.
3. **MySearch** — компонент для поиска автомобилей (Вариант 8) с использованием Material UI.

Все компоненты должны быть реализованы как функциональные с использованием хуков.

**Часть 2:** Создать SPA с использованием React Router и HashRouter. Разместить App Bar (Material UI) сверху, меню со ссылками слева и область контента в центре. Использовать Grid для макета.

---

## 🛠 Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" height="28"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript" height="28"/>
  <img src="https://img.shields.io/badge/React_Router-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white" alt="React Router" height="28"/>
  <img src="https://img.shields.io/badge/Webpack-8DD6F9?style=for-the-badge&logo=webpack&logoColor=black" alt="Webpack" height="28"/>
  <img src="https://img.shields.io/badge/Babel-F9DC3E?style=for-the-badge&logo=babel&logoColor=black" alt="Babel" height="28"/>
  <img src="https://img.shields.io/badge/Material_UI-007FFF?style=for-the-badge&logo=mui&logoColor=white" alt="Material UI" height="28"/>
  <img src="https://img.shields.io/badge/Express-000000?style=for-the-badge&logo=express&logoColor=white" alt="Express" height="28"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" alt="HTML5" height="28"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" alt="CSS3" height="28"/>
</p>

---

## 📚 Компоненты

### 1. Getting Started (`Example`)
- `useState` для управления состоянием
- Обработка событий (`onChange`)
- `useEffect` для побочных эффектов

### 2. Поиск областей (`Regions`)
- Фильтрация по подстроке (регистронезависимо)
- Оптимизация с `useMemo`
- Сортировка по алфавиту

### 3. Автобаза (`MySearch`) — Вариант 8
- Material UI (`List`, `ListItem`, `Paper`, `Typography`, `TextField`)
- Фильтрация и сортировка
- Сообщение "Совпадений не найдено"

---

## 🧭 SPA и маршрутизация (Часть 2)

Приложение объединено в SPA с использованием **React Router** и **HashRouter**.

### Структура SPA
- **App Bar** (Material UI) — верхняя панель с заголовком
- **Sidebar** — колонка со ссылками на компоненты (`Example`, `Regions`, `MySearch`)
- **Content Area** — область для отображения выбранного компонента
- **Grid** (Material UI) — для адаптивного макета (12 колонок)

### Маршруты

| Путь | Компонент |
|------|-----------|
| `/` | Home (приветствие) |
| `/example` | Example |
| `/regions` | Regions |
| `/mysearch` | MySearch |

### Ключевые особенности
- `HashRouter` — для хэш-ссылок без настройки сервера
- `Routes` / `Route` — определение маршрутов (React Router v6)
- `Link` — навигация без перезагрузки страницы
- `useMemo` — оптимизация фильтрации

---

## 🖥 Сервер Express

Для запуска проекта используется Express-сервер, который раздаёт статические файлы и обрабатывает маршруты.

```javascript
const express = require('express');
const path = require('path');
const app = express();
const PORT = 3000;

app.use(express.static('dist'));
app.use(express.static('public'));
```

---

## 📁 Структура

```
demo_react_lab1/
├── components/
│   ├── Example/
│   ├── Regions/
│   └── MySearch/
├── modelData/
├── public/
│   ├── Getting-started.html
│   ├── p2.html
│   └── p4.html
├── src/
│   ├── p1.jsx
│   ├── p2.jsx
│   ├── p3.jsx
│   └── p4.jsx          (SPA)
├── dist/
├── server.js
├── package.json
└── webpack.config.js
```

---

## 🚀 Запуск

```bash
# Установка зависимостей
npm install

# Сборка проекта
npm run build

# Автоматическая сборка при изменениях
npm run watch

# Запуск сервера
npm start

# Сборка + запуск сервера
npm run dev
```

После запуска сервер доступен по адресу `http://localhost:3000`.

---

## 📄 Страницы

| Компонент | URL | Файл |
|-----------|-----|------|
| Example | `/` | `public/Getting-started.html` |
| Regions | `/p2` | `public/p2.html` |
| SPA (все компоненты) | `/p4` | `public/p4.html` |

---

## ✅ Что сделано

### Часть 1
- [x] Переписан `Example` в функциональный компонент с хуками
- [x] Переписан `Regions` в функциональный компонент
- [x] Создан `MySearch` для варианта "Автобаза"
- [x] Подключена Material UI
- [x] Настроен Webpack + Babel

### Часть 2
- [x] Установлен `react-router-dom`
- [x] Создан SPA с использованием `HashRouter`
- [x] Добавлен App Bar (Material UI)
- [x] Добавлен Sidebar со ссылками
- [x] Настроены маршруты (`Routes` / `Route`)
- [x] Использован `Grid` для макета
- [x] Поднят Express-сервер

---

## 👤 Автор

**Нурмедов Азат**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com

---

# Laboratory Work #1 — React

Developing an application using React.  
**Variant 8**: Car Database. Vehicle List.

---

## 📋 Task

Develop a React application consisting of three components:

1. **Example** — a getting-started component demonstrating state management.
2. **Regions** — a component for searching Belarus regions by substring with dynamic filtering.
3. **MySearch** — a car search component (Variant 8) using Material UI.

All components must be implemented as functional components using hooks.

**Part 2:** Create an SPA using React Router and HashRouter. Place an App Bar (Material UI) at the top, a menu with links on the left, and content area in the center. Use Grid for the layout.

---

## 🛠 Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" height="28"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript" height="28"/>
  <img src="https://img.shields.io/badge/React_Router-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white" alt="React Router" height="28"/>
  <img src="https://img.shields.io/badge/Webpack-8DD6F9?style=for-the-badge&logo=webpack&logoColor=black" alt="Webpack" height="28"/>
  <img src="https://img.shields.io/badge/Babel-F9DC3E?style=for-the-badge&logo=babel&logoColor=black" alt="Babel" height="28"/>
  <img src="https://img.shields.io/badge/Material_UI-007FFF?style=for-the-badge&logo=mui&logoColor=white" alt="Material UI" height="28"/>
  <img src="https://img.shields.io/badge/Express-000000?style=for-the-badge&logo=express&logoColor=white" alt="Express" height="28"/>
  <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" alt="HTML5" height="28"/>
  <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" alt="CSS3" height="28"/>
</p>

---

## 📚 Components

### 1. Getting Started (`Example`)
- `useState` for state management
- Event handling (`onChange`)
- `useEffect` for side effects

### 2. Regions Search (`Regions`)
- Case-insensitive substring filtering
- `useMemo` optimization
- Alphabetical sorting

### 3. Car Database (`MySearch`) — Variant 8
- Material UI (`List`, `ListItem`, `Paper`, `Typography`, `TextField`)
- Filtering and sorting
- "No matches found" message

---

## 🧭 SPA & Routing (Part 2)

The app is combined into an SPA using **React Router** and **HashRouter**.

### SPA Structure
- **App Bar** (Material UI) — top panel with the title
- **Sidebar** — column with links to components (`Example`, `Regions`, `MySearch`)
- **Content Area** — displays the selected component
- **Grid** (Material UI) — for responsive layout (12 columns)

### Routes

| Path | Component |
|------|-----------|
| `/` | Home (welcome) |
| `/example` | Example |
| `/regions` | Regions |
| `/mysearch` | MySearch |

### Key Features
- `HashRouter` — for hash-based URLs without server setup
- `Routes` / `Route` — route definitions (React Router v6)
- `Link` — navigation without page reload
- `useMemo` — optimization of filtering

---

## 🖥 Express Server

The project uses an Express server to serve static files and handle routes.

```javascript
const express = require('express');
const path = require('path');
const app = express();
const PORT = 3000;

app.use(express.static('dist'));
app.use(express.static('public'));
```

---

## 📁 Structure

```
demo_react_lab1/
├── components/
│   ├── Example/
│   ├── Regions/
│   └── MySearch/
├── modelData/
├── public/
│   ├── Getting-started.html
│   ├── p2.html
│   └── p4.html
├── src/
│   ├── p1.jsx
│   ├── p2.jsx
│   ├── p3.jsx
│   └── p4.jsx          (SPA)
├── dist/
├── server.js
├── package.json
└── webpack.config.js
```

---

## 🚀 Run

```bash
# Install dependencies
npm install

# Build the project
npm run build

# Auto-rebuild on changes
npm run watch

# Start the server
npm start

# Build + start server
npm run dev
```

After starting, the server is available at `http://localhost:3000`.

---

## 📄 Pages

| Component | URL | File |
|-----------|-----|------|
| Example | `/` | `public/Getting-started.html` |
| Regions | `/p2` | `public/p2.html` |
| SPA (all components) | `/p4` | `public/p4.html` |

---

## ✅ Done

### Part 1
- [x] Rewritten `Example` as functional component with hooks
- [x] Rewritten `Regions` as functional component
- [x] Created `MySearch` for "Car Database" variant
- [x] Integrated Material UI
- [x] Configured Webpack + Babel

### Part 2
- [x] Installed `react-router-dom`
- [x] Created SPA using `HashRouter`
- [x] Added App Bar (Material UI)
- [x] Added Sidebar with links
- [x] Configured routes (`Routes` / `Route`)
- [x] Used `Grid` for layout
- [x] Started Express server

---

## 👤 Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com