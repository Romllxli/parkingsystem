# Parking System Backend Service

Бэкенд-сервис системы управления парковкой, разработанный на Java 17 и Spring Boot.

## Технологический стек
- **Язык:** Java 17
- **Фреймворк:** Spring Boot 3
- **База данных:** PostgreSQL
- **ORM:** Spring Data JPA (Hibernate)
- **Документация:** Swagger / OpenAPI UI
- **Сборщик:** Maven

## Инструкция по запуску

### 1. Требования
- Установленная Java 17 (или выше)
- Запущенный PostgreSQL с созданной базой данных `parking_db`

### 2. Настройка подключения к БД
Перед запуском убедитесь, что параметры в `src/main/resources/application.properties` соответствуют вашей настройке PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/parking_db
spring.datasource.username=postgres
spring.datasource.password=1234