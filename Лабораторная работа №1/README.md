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

---

## 🛠 Стек технологий

<p align="left">
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" height="28"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript" height="28"/>
  <img src="https://img.shields.io/badge/Webpack-8DD6F9?style=for-the-badge&logo=webpack&logoColor=black" alt="Webpack" height="28"/>
  <img src="https://img.shields.io/badge/Babel-F9DC3E?style=for-the-badge&logo=babel&logoColor=black" alt="Babel" height="28"/>
  <img src="https://img.shields.io/badge/Material_UI-007FFF?style=for-the-badge&logo=mui&logoColor=white" alt="Material UI" height="28"/>
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

## 📁 Структура

```
demo_react_lab1/
├── components/
│   ├── Example/
│   ├── Regions/
│   └── MySearch/
├── modelData/
├── public/
├── src/
├── dist/
├── package.json
└── webpack.config.js
```

---

## 🚀 Запуск

```bash
npm install
npm run build
npm run watch
```

---

## 📄 Страницы

| Компонент | Файл |
|-----------|------|
| Example | `public/Getting-started.html` |
| Regions | `public/p2.html` |
| MySearch | `components/MySearch/MySearch.html` |

---

## ✅ Что сделано

- [x] Переписан `Example` в функциональный компонент с хуками
- [x] Переписан `Regions` в функциональный компонент
- [x] Создан `MySearch` для варианта "Автобаза"
- [x] Подключена Material UI
- [x] Настроен Webpack + Babel

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

---

## 🛠 Tech Stack

<p align="left">
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React" height="28"/>
  <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript" height="28"/>
  <img src="https://img.shields.io/badge/Webpack-8DD6F9?style=for-the-badge&logo=webpack&logoColor=black" alt="Webpack" height="28"/>
  <img src="https://img.shields.io/badge/Babel-F9DC3E?style=for-the-badge&logo=babel&logoColor=black" alt="Babel" height="28"/>
  <img src="https://img.shields.io/badge/Material_UI-007FFF?style=for-the-badge&logo=mui&logoColor=white" alt="Material UI" height="28"/>
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

## 📁 Structure

```
demo_react_lab1/
├── components/
├── modelData/
├── public/
├── src/
├── dist/
├── package.json
└── webpack.config.js
```

---

## 🚀 Run

```bash
npm install
npm run build
npm run watch
```

---

## 📄 Pages

| Component | File |
|-----------|------|
| Example | `public/Getting-started.html` |
| Regions | `public/p2.html` |
| MySearch | `components/MySearch/MySearch.html` |

---

## ✅ Done

- [x] Rewritten `Example` as functional component with hooks
- [x] Rewritten `Regions` as functional component
- [x] Created `MySearch` for "Car Database" variant
- [x] Integrated Material UI
- [x] Configured Webpack + Babel

---

## 👤 Author

**Azat Nurmedov**  
Telegram: @fakevv | Instagram: nurmedovv | Email: azatnurmedovv2554@gmail.com