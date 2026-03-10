# Backend Приложение с топ стэком для микросервесной архитектуры
## Микросервис - Main
[![en](https://img.shields.io/badge/lang-en-green.svg)](https://github.com/BornToNight/main-kotlin/blob/main/README.md)
[![java](https://img.shields.io/badge/java-blue.svg)](https://github.com/BornToNight/main/blob/main/README.ru.md)

Цель проекта - потренироваться с самым популярным стэком для Java/Kotlin разработчика (ИМХО).
 
### Микросервисы:
- [proto-common](https://github.com/BornToNight/proto-common)
  - Базовые классы gRPC (**main** и **reader**).
- [**main**](https://github.com/BornToNight/main-kotlin) (:5001) :white_check_mark:
  - Основной сервис, front (postman) общается **ТОЛЬКО** с main. Включает в себя **docker-compose** и **k8s** конфиги.
- [writer](https://github.com/BornToNight/writer-kotlin) (:5002)
  - Микросервис для практики по работе с **Kafka**. Получает сообщения из Kafka и записывает в PostgreSQL.
- [reader](https://github.com/BornToNight/reader-kotlin) (:5003)
  - Микросервис для практики по работе с **gRPC**. **main** может отправить запрос в **reader**.
- [admin](https://github.com/BornToNight/admin-kotlin) (:5004)
  - Spring Admin UI для мониторинка сервисов.

![drawio2](https://github.com/user-attachments/assets/cd5bb990-c4b6-4c34-8477-1e75c0c68cf5)

### Стэк:
- Kotlin 2.1.0
- Spring 3.4.2
- Hibernate (@Query, Specification, @EntityGraph)
- Scheduler (+ @SchedulerLock) (**main**)
- CircuitBreaker, Retry (**main**)
- Swagger (**main**)
- MapStruct (**main**)
- Tests (Testcontainers (integration), AutoConfigureGraphQlTester) (**main**)
- PostgreSQL
- Liquibase (**main**)
- Kafka (**main** -> **writer**)
- Redis (Cache) (**main**, Organization Entity)
- gRPC (**main** -> **reader**)
- GraphQL (**main**)
- ELK (Observe logs (**main** and **writer** write logs))
- Prometeus + Grafana (Monitoring microservices)
- Docker + compose - лёгкий путь запуска
- Kubernetes - сложный путь запуска (Несколько инстансов приложения, рестарт умерших микросервисов, балансировщик, лёгкая настройка) (**main**)

### Стэк TODO/Пропущено:
- Camunda (**TODO**)
- S3 (Платно, **возможно потом**)
- SOAP (Устарело, **пропуск**)
- CI/CD (Оставим это для DevOps)
- Шардирование и репликация PostgreSQL (Отсавим это базистам)

### БД модель:
ты можешь найти [модель](https://github.com/BornToNight/main/blob/main/src/main/resources/example.dbm) в микросервисе **main**, в папке - **resources**.
![PG](https://github.com/user-attachments/assets/5fe4e4ea-ec33-448d-9849-c20952a66248)

## После запуска, ты сможешь открыть
1. Spring admin (compose - http://localhost:5004, k8s - http://pet.admin)
![Spring Admin](https://github.com/user-attachments/assets/76484c16-8e40-4ecb-949d-afe950ffb1b6)
2. Grafana (compose - http://localhost:3000, k8s - http://pet.grafana)
![Grafana](https://github.com/user-attachments/assets/f9244000-df7e-4d7b-b29f-710d3de314b4)
3. Prometheus (compose - http://localhost:9090, k8s - http://pet.prometheus)
![Prometheus](https://github.com/user-attachments/assets/5da375cf-1e04-4175-bd46-6fc6be8df889)
4. Kibana (compose - http://localhost:5601, k8s - http://pet.kibana)
![Kibana](https://github.com/user-attachments/assets/0a6d9fc5-4608-439a-a137-35ebe3527a5e)
5. Swagger (compose - http://localhost:5001/swagger-ui/index.html, k8s - http://pet.main/swagger-ui/index.html)
![Swagger](https://github.com/user-attachments/assets/8e8e0fcc-06e3-4aa7-8596-682e95d08707)
6. Graphiql (compose - http://localhost:5001/graphiql k8s - http://pet.main/graphiql)
![GraphiQL](https://github.com/user-attachments/assets/05f56c49-36e8-45a2-85b0-1fd0edbeb3ec)
7. Kubernetes Dashboard (только k8s, открывается в браузере с помощью команды)
![Kubernetes Dashboard](https://github.com/user-attachments/assets/201e0933-0f4b-428f-9487-59c691af6061)

## Как запустить

### 1. Спуллить все микросервисы

### 2. В **proto-common** выполнить команду в terminal/IDEA

```
gradle publishToMavenLocal
```

### 3. В Run/Debug Configuration добавить *.env* файл

### 4. Установить и запустить Docker machine

### 5. **Docker compose путь**
1. Запустить services в *docker-compose.yaml*
2. Запустить все микросервисы в IDEA или выполнить команду
```
gradle bootRun
```
3. Теперь всё работает! (ВСЕ ЛОГИНЫ И ПАРОЛИ ТЫ НАЙДЁШЬ В **.env** файле в **main** микросервисе). <br>
По умолчанию логин и пароль создаются в таблице "users". (Ты можешь поменять в файле - **insertAdmin.sql**) <br>
login ```admin``` <br>
password ```adminPassword```

### 6. **Kubernetes путь (Minikube + Docker)**
1. Установить minikube
2. Установить kubectl (или используй minikube kubectl)
3. Запустить minikube
```
minikube start
```
4. Включить ingress
```
minikube addons enable ingress
```
5. Включить dashboard
```
minikube addons enable dashboard
```
6. Перейти в директорию проекта в **main** микросервисе и выполнить команду
```
kubectl create secret generic my-secret --from-env-file=k8s.env
```
7. Перейти в директорию проекта в **main, writer, reader, admin** сервисах и выполнить команды (build Jar и build Docker Image)
```
.\gradlew build
```
```
docker build -t username/serviceName:version .
```
  Пример
```
docker build -t borntonight/main:1 .
```
8. Push свои images микросервисов на Docker Hub или можно загрузить их в minikube с локального ПК с помощью команды
```
minikube image load borntonight/main:1
```
9. Повторить 7 и 8 шаг для Grafana (**main** микросервис -> config/grafana)
10. Загрузить конфиги в minikube с помощью команды
```
kubectl apply -f .\config\k8s
```
11. Вставить в **hosts** файл на своём ПК
```
127.0.0.1 pet.grafana
127.0.0.1 pet.prometheus
127.0.0.1 pet.kibana
127.0.0.1 pet.admin
127.0.0.1 pet.main
```
12. Выполнить команду для туннеля
```
minikube tunnel
```
13. Выполнить команду для открытия dashboard
```
minikube dashboard
```
14. Теперь всё работает! (ВСЕ ЛОГИНЫ И ПАРОЛИ ТЫ НАЙДЁШЬ В **.env** файле в **main** микросервисе)

### 7. Для вызова эндпоинтов можно использовать Postman
1. Отправить POST запрос на http://localhost:5001/api/auth/generate / http://pet.main/api/auth/generate <br>
2. Получить "token". Ты можешь изменить время протухания на сайте - https://jwt.io/ (подпись по умолчанию - "secret" (ты можешь изменить в **.evn** файле)) или изменить время протухания в .evn файле.

## Неявные случаи
1. Если ты используешь PostgreSQL на локальной машине - тебе нужно создать пользователя с логином "admindb"

## Контакты
Ты можешь задать вопросы или написать предложения по улучшению :sunglasses: <br>
Telegram - [@BTNtelegram](https://t.me/BTNtelegram)
