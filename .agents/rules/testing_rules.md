# Правила написания тестов — Spending Tracker

Документ фиксирует обязательные соглашения для тестового кода backend. Правила применяются при написании новых и рефакторинге существующих тестов.

---

## 1. Обязательные правила

### 1.1. Модификатор доступа класса указывать явно

Каждый тестовый класс объявляется **только** с модификатором `public`. Сокращённая (package-private) форма запрещена.

```java
// ✅ правильно
public class LogbackMaskingTest {
    ...
}

// ❌ неправильно
class LogbackMaskingTest {
    ...
}
```

Правило распространяется на все тестовые классы: `*Test`, `BaseSpringBootTest`, утилиты тестовой инфраструктуры.

### 1.2. Использовать `.getFirst()` вместо `.get(0)`

Для получения первого элемента коллекции применяется метод `getFirst()`, а не индекс `get(0)`. Аналогично для последнего элемента — `getLast()` вместо `get(size() - 1)`.

```java
// ✅ правильно
Map<String, String> mdc = appender.list.getFirst().getMDCPropertyMap();

// ❌ неправильно
Map<String, String> mdc = appender.list.get(0).getMDCPropertyMap();
```

Для списков без удобных методов (`List`) допустимо использовать `getFirst()` / `getLast()` из `SequencedCollection` (Java 21+), а не прямой индекс доступа.

---

## 2. Базовые конвенции тестов проекта

### 2.1. Интеграционные тесты контроллеров

- Наследоваться от [`BaseSpringBootTest`](../../backend/src/test/java/spending/tracker/backend/base/BaseSpringBootTest.java) — он даёт `MockMvc` и заголовок `X-User-Email`.
- Профиль: `@ActiveProfiles("test")` (наследуется из базового класса).
- БД: H2 in-memory (`backend/src/test/resources/application-test.yml`, `ddl-auto: create-drop`, Liquibase отключён).
- Безопасность отключена через `spring.autoconfigure.exclude` (задано в базовом классе).
- Методы помечать `@Test`, `@SneakyThrows` (для `MockMvc`) и `@DisplayName` на русском языке.

```java
public class CategoryControllerTest extends BaseSpringBootTest {

    @Test
    @SneakyThrows
    @DisplayName("должен вернуть все категории пользователя")
    public void testGetAllCategories() {
        mockMvc.perform(get("/api/v1/categories")
                        .header(X_USER_EMAIL_HEADER, "test@example.com"))
                .andExpect(status().isOk());
    }
}
```

### 2.2. Тесты Data-слоя

- Помечать `@SpringBootTest`, `@ActiveProfiles("test")`, `@WebAppConfiguration`.
- Класс **не** помечать `@Transactional` там, где проверяется отсутствие `LazyInitializationException` — транзакцией управляет `DataService`, что соответствует реальному HTTP-запросу.
- Для assertions использовать AssertJ (`assertThat`).

### 2.3. Тестовые данные

- Создавать через [`TestDataUtils`](../../backend/src/test/java/spending/tracker/backend/util/TestDataUtils.java) (`createUser`, `createCategory`, `createSubCategory`, `createSpending`, `createSpendingWithSubCategory`).
- Не дублировать ручное создание сущностей и сохранение через репозитории в тестах.

### 2.4. Именование и структура

- Тестовые классы: `*Test.java`.
- Тесты контроллеров: `backend/src/test/java/.../controller/`.
- Тесты Data-сервисов: `backend/src/test/java/.../service/data/`.
- Тесты утилит/фильтров: соответствующие пакеты (`filter/`, `validation/`, `util/`).
- Метод теста: `<что_проверяем>_<условие>` для граничных случаев, иначе — описательное имя.

### 2.5. Логирование в тестах

- Тестовый профиль использует `backend/src/test/resources/logback-test.xml` — только консоль, без Graylog.
- Для проверки маскирования sensitive-данных загружать реальную конфигурацию (`logback-masking-verify.xml`) и проверять, что токены/пароли не попадают в вывод.
- После тестов, работающих с MDC, очищать `MDC.clear()` в `@AfterEach`.

---

## 3. Запуск тестов

```bash
# из корня репозитория
cd backend
mvn test
```

Ожидаемый результат: `BUILD SUCCESS`, все тесты зелёные.

---

## 4. Чек-лист ревью тестового кода

- [ ] Класс объявлен как `public`, модификатор доступа не опущен.
- [ ] Первый/последний элемент коллекции берётся через `getFirst()` / `getLast()`.
- [ ] Интеграционные тесты наследуются от `BaseSpringBootTest`.
- [ ] Тестовые данные создаются через `TestDataUtils`.
- [ ] MDC очищается после тестов логирования.
- [ ] Секреты (`Authorization`, `password`, `token`, JWT) не логируются и не попадают в assertions как «ожидаемые».
