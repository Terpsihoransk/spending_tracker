# Spending Tracker

Приложение для учёта личных расходов с синхронизацией в Google Sheets.

**Версия**: v0.0.1 (MVP1)

## Архитектура

```
Android (Kotlin) → REST API (Java 25/Spring Boot) → Google Sheets
```

## Запуск

```bash
# Backend
cd backend && mvn spring-boot:run

# Swagger UI
http://localhost:8081/swagger-ui/index.html
```

## Подробности

См. [AGENTS.md](AGENTS.md) для полной документации.