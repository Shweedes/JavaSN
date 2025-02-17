# SocialNetwork

## Описание проекта
SocialNetwork — это REST API для простой социальной сети, написанное на **Spring Boot** с использованием **PostgreSQL**. Проект поддерживает управление пользователями и группами, а также их взаимодействие через связь **Many-to-Many**.

## Стек технологий
- **Java 17**
- **Spring Boot** (Spring Web, Spring Data JPA)
- **PostgreSQL**
- **Hibernate**
- **Maven**
- **MapStruct**
- **CheckStyle**
- **IntelliJ IDEA**

## Установка и запуск
### 1. Клонирование репозитория
```sh
git clone https://github.com/your-username/SocialNetwork.git
cd SocialNetwork
```

### 2. Настройка базы данных
В файле `application.properties` настрой параметры подключения к **PostgreSQL**:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/social_network
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Сборка и запуск
```sh
mvn clean install
mvn spring-boot:run
```

## API эндпоинты

### **Пользователи** (`/users`)
- **Создать пользователя** (POST `/users`)
- **Получить пользователя по ID** (GET `/users/{id}`)
- **Обновить пользователя** (PUT `/users/{id}`)
- **Удалить пользователя** (DELETE `/users/{id}`)
- **Добавить пользователя в группу** (PUT `/users/{userId}/groups/{groupId}`)
- **Удалить пользователя из группы** (DELETE `/users/{userId}/groups/{groupId}`)

### **Группы** (`/groups`)
- **Создать группу** (POST `/groups`)
- **Получить группу по ID** (GET `/groups/{id}`)
- **Обновить группу** (PUT `/groups/{id}`)
- **Удалить группу** (DELETE `/groups/{id}`)

## Тестирование API через Postman
1. Установи [Postman](https://www.postman.com/).
2. Создай новый **POST-запрос** для создания пользователя:
   ```json
   {
       "username": "qwerty",
       "password": "qwerty"
   }
   ```
3. Запусти **GET-запрос** на `/users/{id}`, чтобы проверить созданного пользователя.
4. Повтори шаги для групп.

## Разработка
Если ты хочешь внести вклад:
1. Форкни репозиторий
2. Создай новую ветку (`git checkout -b feature-branch`)
3. Внеси изменения и закоммить (`git commit -m "Описание изменений"`)
4. Запушь в свою ветку (`git push origin feature-branch`)
5. Создай Pull Request

## Лицензия
Этот проект распространяется под лицензией **MIT**.

