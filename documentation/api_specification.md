# API Specification — Spending Tracker Backend

**Version**: v0.0.1  
**Base URL**: `http://localhost:8081/api/v1`  
**Swagger UI**: `http://localhost:8081/swagger-ui/index.html`  

---

## Общие требования

### Аутентификация
Все запросы (кроме создания пользователя) требуют заголовок:

```
X-User-Email: user@example.com
```

Email валидируется через `ValidEmailHeader` аннотацией.

### Формат данных
- **Request/Response**: JSON
- **Даты**: `YYYY-MM-DD` (ISO LocalDate)
- **Суммы**: `BigDecimal` (строка в JSON)

---

## Модуль User (`/user`)

### POST `/user`
Создание нового пользователя

**Request Body** — [`UserRequest`](../backend/src/main/java/spending/tracker/backend/dto/UserRequest.java):
```json
{
  "email": "user@example.com",
  "googleSheetsId": "sheet123"
}
```

**Response** — [`UserResponse`](../backend/src/main/java/spending/tracker/backend/dto/UserResponse.java) (200 OK):
```json
{
  "id": 1,
  "email": "user@example.com",
  "googleSheetsId": "sheet123"
}
```

**Ошибки**:
- `400` — валидация (некорректный email, пустые поля)

---

### GET `/user`
Получение списка всех пользователей

**Response** — массив [`UserResponse`](../backend/src/main/java/spending/tracker/backend/dto/UserResponse.java) (200 OK):
```json
[
  {
    "id": 1,
    "email": "user@example.com",
    "googleSheetsId": "sheet123"
  }
]
```

---

## Модуль Category (`/categories`)

Все эндпоинты требуют заголовок `X-User-Email`.

### GET `/categories`
Получение всех категорий пользователя

**Headers**:
- `X-User-Email`: `user@example.com`

**Response** — массив [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) (200 OK):
```json
[
  {
    "id": 1,
    "name": "Food",
    "userEmail": "user@example.com"
  }
]
```

---

### GET `/categories/{id}`
Получение категории по ID

**Path Parameters**:
- `id` — Long (идентификатор категории)

**Headers**:
- `X-User-Email`: `user@example.com`

**Response** — [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) (200 OK):
```json
{
  "id": 1,
  "name": "Food",
  "userEmail": "user@example.com"
}
```

**Ошибки**:
- `404` — категория не найдена или не принадлежит пользователю

---

### POST `/categories`
Создание новой категории

**Headers**:
- `X-User-Email`: `user@example.com`

**Request Body** — [`CategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/CategoryRequest.java):
```json
{
  "name": "Food"
}
```

**Response** — [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) (200 OK):
```json
{
  "id": 1,
  "name": "Food",
  "userEmail": "user@example.com"
}
```

**Ошибки**:
- `400` — валидация (пустое имя)
- `409` — категория с таким именем уже существует у пользователя

---

### PUT `/categories/{id}`
Обновление существующей категории

**Path Parameters**:
- `id` — Long (идентификатор категории)

**Headers**:
- `X-User-Email`: `user@example.com`

**Request Body** — [`CategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/CategoryRequest.java):
```json
{
  "name": "Updated Food"
}
```

**Response** — [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) (200 OK):
```json
{
  "id": 1,
  "name": "Updated Food",
  "userEmail": "user@example.com"
}
```

**Ошибки**:
- `400` — валидация
- `404` — категория не найдена
- `409` — категория с таким именем уже существует у пользователя

---

### DELETE `/categories/{id}`
Удаление категории по ID

**Path Parameters**:
- `id` — Long (идентификатор категории)

**Headers**:
- `X-User-Email`: `user@example.com`

**Response**: 200 OK (тело пустое)

**Ошибки**:
- `404` — категория не найдена
- `409` — категория используется в расходах (нельзя удалить)

---

## Модуль SubCategory (`/subcategories`)

Все эндпоинты требуют заголовок `X-User-Email`.

### GET `/subcategories`
Получение всех подкатегорий по ID категории

**Query Parameters**:
- `categoryId` — Long (идентификатор категории)

**Headers**:
- `X-User-Email`: `user@example.com`

**Response** — массив [`SubCategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryResponse.java) (200 OK):
```json
[
  {
    "id": 1,
    "name": "Restaurants",
    "categoryId": 1,
    "categoryName": "Food"
  }
]
```

---

### POST `/subcategories`
Создание новой подкатегории

**Headers**:
- `X-User-Email`: `user@example.com`

**Request Body** — [`SubCategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryRequest.java):
```json
{
  "name": "Restaurants",
  "categoryId": 1
}
```

**Response** — [`SubCategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryResponse.java) (200 OK):
```json
{
  "id": 1,
  "name": "Restaurants",
  "categoryId": 1,
  "categoryName": "Food"
}
```

**Ошибки**:
- `400` — валидация (пустое имя, отсутствует categoryId)
- `409` — подкатегория с таким именем уже существует у пользователя в данной категории

---

### PUT `/subcategories/{id}`
Обновление существующей подкатегории

**Path Parameters**:
- `id` — Long (идентификатор подкатегории)

**Headers**:
- `X-User-Email`: `user@example.com`

**Request Body** — [`SubCategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryRequest.java):
```json
{
  "name": "Updated Restaurants",
  "categoryId": 1
}
```

**Response** — [`SubCategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryResponse.java) (200 OK):
```json
{
  "id": 1,
  "name": "Updated Restaurants",
  "categoryId": 1,
  "categoryName": "Food"
}
```

**Ошибки**:
- `400` — валидация
- `404` — подкатегория не найдена
- `409` — подкатегория с таким именем уже существует у пользователя в данной категории

---

### DELETE `/subcategories/{id}`
Удаление подкатегории по ID

**Path Parameters**:
- `id` — Long (идентификатор подкатегории)

**Headers**:
- `X-User-Email`: `user@example.com`

**Response**: 200 OK (тело пустое)

**Ошибки**:
- `404` — подкатегория не найдена
- `409` — подкатегория используется в расходах (нельзя удалить)

---

## Модуль Spending (`/spending`)

Все эндпоинты требуют заголовок `X-User-Email`.

### GET `/spending`
Получение всех расходов пользователя

**Headers**:
- `X-User-Email`: `user@example.com`

**Response** — массив [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) (200 OK):
```json
[
  {
    "id": 1,
    "amount": "150.00",
    "categoryId": 1,
    "categoryName": "Food",
    "subcategoryId": 2,
    "subcategoryName": "Restaurants",
    "date": "2024-01-15",
    "description": "Lunch at restaurant",
    "userEmail": "user@example.com"
  }
]
```

---

### GET `/spending/{id}`
Получение расхода по ID

**Path Parameters**:
- `id` — Long (идентификатор расхода)

**Headers**:
- `X-User-Email`: `user@example.com`

**Response** — [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) (200 OK):
```json
{
  "id": 1,
  "amount": "150.00",
  "categoryId": 1,
  "categoryName": "Food",
  "subcategoryId": 2,
  "subcategoryName": "Restaurants",
  "date": "2024-01-15",
  "description": "Lunch at restaurant",
  "userEmail": "user@example.com"
}
```

**Ошибки**:
- `404` — расход не найден или не принадлежит пользователю

---

### POST `/spending`
Создание нового расхода

**Headers**:
- `X-User-Email`: `user@example.com`

**Request Body** — [`SpendingRequest`](../backend/src/main/java/spending/tracker/backend/dto/SpendingRequest.java):
```json
{
  "amount": 150.00,
  "categoryId": 1,
  "subcategoryId": 2,
  "description": "Lunch at restaurant"
}
```

**Примечание**: Поле `date` не передаётся — устанавливается автоматически как текущая дата сервера.

**Response** — [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) (200 OK):
```json
{
  "id": 1,
  "amount": "150.00",
  "categoryId": 1,
  "categoryName": "Food",
  "subcategoryId": 2,
  "subcategoryName": "Restaurants",
  "date": "2024-01-15",
  "description": "Lunch at restaurant",
  "userEmail": "user@example.com"
}
```

**Ошибки**:
- `400` — валидация (сумма не указана, категория не указана)
- `404` — категория/подкатегория не найдена или не принадлежит пользователю

---

### PUT `/spending/{id}`
Обновление существующего расхода

**Path Parameters**:
- `id` — Long (идентификатор расхода)

**Headers**:
- `X-User-Email`: `user@example.com`

**Request Body** — [`SpendingRequest`](../backend/src/main/java/spending/tracker/backend/dto/SpendingRequest.java):
```json
{
  "amount": 200.00,
  "categoryId": 1,
  "subcategoryId": 2,
  "description": "Updated description"
}
```

**Response** — [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) (200 OK):
```json
{
  "id": 1,
  "amount": "200.00",
  "categoryId": 1,
  "categoryName": "Food",
  "subcategoryId": 2,
  "subcategoryName": "Restaurants",
  "date": "2024-01-15",
  "description": "Updated description",
  "userEmail": "user@example.com"
}
```

**Ошибки**:
- `400` — валидация
- `404` — расход не найден или не принадлежит пользователю, категория/подкатегория не найдены

---

### DELETE `/spending/{id}`
Удаление расхода по ID

**Path Parameters**:
- `id` — Long (идентификатор расхода)

**Headers**:
- `X-User-Email`: `user@example.com`

**Response**: 200 OK (тело пустое)

**Ошибки**:
- `404` — расход не найден или не принадлежит пользователю

---

## Общие коды ошибок

| Код | Описание |
|-----|----------|
| `400` | Bad Request — ошибки валидации входных данных |
| `404` | Not Found — ресурс не найден |
| `409` | Conflict — дублирование имени или нарушение ограничений (категория используется) |

---

## Сводная таблица эндпоинтов

| Модуль | Метод | Эндпоинт | Описание | Request Body | Response |
|--------|-------|----------|----------|--------------|----------|
| User | POST | `/user` | Создать пользователя | [`UserRequest`](../backend/src/main/java/spending/tracker/backend/dto/UserRequest.java) | [`UserResponse`](../backend/src/main/java/spending/tracker/backend/dto/UserResponse.java) |
| User | GET | `/user` | Получить всех пользователей | — | [`UserResponse[]`](../backend/src/main/java/spending/tracker/backend/dto/UserResponse.java) |
| Category | GET | `/categories` | Получить категории пользователя | — | [`CategoryResponse[]`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) |
| Category | GET | `/categories/{id}` | Получить категорию по ID | — | [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) |
| Category | POST | `/categories` | Создать категорию | [`CategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/CategoryRequest.java) | [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) |
| Category | PUT | `/categories/{id}` | Обновить категорию | [`CategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/CategoryRequest.java) | [`CategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/CategoryResponse.java) |
| Category | DELETE | `/categories/{id}` | Удалить категорию | — | 200 OK |
| SubCategory | GET | `/subcategories?categoryId={id}` | Получить подкатегории категории | — | [`SubCategoryResponse[]`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryResponse.java) |
| SubCategory | POST | `/subcategories` | Создать подкатегорию | [`SubCategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryRequest.java) | [`SubCategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryResponse.java) |
| SubCategory | PUT | `/subcategories/{id}` | Обновить подкатегорию | [`SubCategoryRequest`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryRequest.java) | [`SubCategoryResponse`](../backend/src/main/java/spending/tracker/backend/dto/SubCategoryResponse.java) |
| SubCategory | DELETE | `/subcategories/{id}` | Удалить подкатегорию | — | 200 OK |
| Spending | GET | `/spending` | Получить расходы пользователя | — | [`SpendingResponse[]`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) |
| Spending | GET | `/spending/{id}` | Получить расход по ID | — | [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) |
| Spending | POST | `/spending` | Создать расход | [`SpendingRequest`](../backend/src/main/java/spending/tracker/backend/dto/SpendingRequest.java) | [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) |
| Spending | PUT | `/spending/{id}` | Обновить расход | [`SpendingRequest`](../backend/src/main/java/spending/tracker/backend/dto/SpendingRequest.java) | [`SpendingResponse`](../backend/src/main/java/spending/tracker/backend/dto/SpendingResponse.java) |
| Spending | DELETE | `/spending/{id}` | Удалить расход | — | 200 OK |