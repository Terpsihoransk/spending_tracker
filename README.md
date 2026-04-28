# Spending Tracker

Приложение для учета личных расходов с синхронизацией в Google Sheets.

## Версия

v0.0.1

## Стадия разработки

MVP1

## Описание
Мобильное приложение (Android) для фиксации расходов по категориям, просмотра сводок и синхронизации данных с облаком через REST API backend.

## Стек технологий
- **Backend**: Java 25, Spring Boot 4.0.4, Spring Security, OAuth 2.0, JPA/H2, Maven
- **Frontend**: Android (Kotlin), Room (SQLite), Retrofit, MPAndroidChart
- **Интеграции**: Google Sheets API, Google OAuth

## Структура проекта
- `backend/` - Spring Boot приложение с REST API
- `plans/` - планы и документация
- `.env` - конфигурация секретов
- `pom.xml` - корневой Maven файл

## Запуск
1. Настроить .env файл с ключами Google API
2. Запустить backend: `mvn spring-boot:run` в папке backend
3. Собрать Android приложение

## Спецификация

http://localhost:8081/swagger-ui/index.html#/