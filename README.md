# Backend Application with Top Stack for Microservices Architecture  
## Microservice - Main  
[![ru](https://img.shields.io/badge/lang-ru-green.svg)](https://github.com/BornToNight/main-kotlin/blob/main/README.ru.md)
[![java](https://img.shields.io/badge/java-blue.svg)](https://github.com/BornToNight/main)

The goal of this project is to practice with the most popular stack for Java/Kotlin developers (IMHO).

### Microservices:
- [proto-common](https://github.com/BornToNight/proto-common)
  - Basic gRPC classes (**main** and **reader**).
- [**main**](https://github.com/BornToNight/main-kotlin) (:5001) :white_check_mark:
  - The main service, front (postman) communicates **ONLY** with main. Includes **docker-compose** and **k8s** configurations.
- [writer](https://github.com/BornToNight/writer-kotlin) (:5002)
  - Microservice for Kafka practice. Receives messages from Kafka and writes them to PostgreSQL.
- [reader](https://github.com/BornToNight/reader-kotlin) (:5003)
  - Microservice for gRPC practice. **main** can send requests to **reader**.
- [admin](https://github.com/BornToNight/admin-kotlin) (:5004)
  - Spring Admin UI for service monitoring.

![drawio2](https://github.com/user-attachments/assets/cd5bb990-c4b6-4c34-8477-1e75c0c68cf5)

### Stack:
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
- Prometheus + Grafana (Monitoring microservices)
- Docker + compose - easy way to run
- Kubernetes - hard way to run (Multiple app instances, restart dead microservices, load balancer, easy configuration) (**main**)

### Stack TODO/Missed:
- Camunda (**TODO**)
- S3 (Paid, **maybe later**)
- SOAP (Deprecated, **skipped**)
- CI/CD (Leave it for DevOps)
- Sharding and replication for PostgreSQL (Leave it for DB admins)

### DB model:
you can find [model](https://github.com/BornToNight/main/blob/main/src/main/resources/example.dbm) in **main** microservice in **resources** directory.
![PG](https://github.com/user-attachments/assets/5fe4e4ea-ec33-448d-9849-c20952a66248)

## After the Run, you will be able to access:
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
7. Kubernetes Dashboard (only k8s, opens in the browser via the command)
![Kubernetes Dashboard](https://github.com/user-attachments/assets/201e0933-0f4b-428f-9487-59c691af6061)

## How to Run

### 1. Pull all microservices

### 2. In **proto-common**, run the following command in terminal/IDEA:

```
gradle publishToMavenLocal
```

### 3. In Run/Debug Configuration, add the *.env* file

### 4. Install and run Docker machine

### 5. **Docker Compose Way**
1. Run services in *docker-compose.yaml*
2. Start all microservices in IDEA or run the command:
```
gradle bootRun
```
3. Now everything works! (ALL LOGINS AND PASSWORDS CAN BE FOUND IN THE **.env** FILE IN THE **main** microservice). <br>
By default, login and password are created in the "users" table. (You can change them in the **insertAdmin.sql** file) <br>
login ```admin``` <br>
password ```adminPassword```

### 6. **Kubernetes Way (Minikube + Docker)**
1. Install minikube
2. Install kubectl (or use minikube kubectl)
3. Start minikube:
```
minikube start
```
4. Enable ingress:
```
minikube addons enable ingress
```
5. Enable dashboard:
```
minikube addons enable dashboard
```
6. Go to the **main** microservice directory and run the command:
```
kubectl create secret generic my-secret --from-env-file=k8s.env
```
7. Go to the project directories of **main, writer, reader, admin** services and run commands (build Jar and build Docker Image):
```
.\gradlew build
```
```
docker build -t username/serviceName:version .
```
  Example:
```
docker build -t borntonight/main:1 .
```
8. Push your microservice images to Docker Hub or load them into minikube from your local machine using:
```
minikube image load borntonight/main:1
```
9. Repeat steps 7 and 8 for Grafana (**main** microservice -> config/grafana)
10. Upload configurations to minikube using:
```
kubectl apply -f .\config\k8s
```
11. Add to the **hosts** file on your machine:
```
127.0.0.1 pet.grafana
127.0.0.1 pet.prometheus
127.0.0.1 pet.kibana
127.0.0.1 pet.admin
127.0.0.1 pet.main
```
12. Run the tunnel command:
```
minikube tunnel
```
13. Run the command to open the dashboard:
```
minikube dashboard
```
14. Now everything works! (ALL LOGINS AND PASSWORDS CAN BE FOUND IN THE **.env** FILE IN THE **main** microservice)

### 7. To call endpoints, you can use Postman
1. Send a POST request to http://localhost:5001/api/auth/generate / http://pet.main/api/auth/generate <br>
2. Receive the "token". You can change the expiration time on the website - https://jwt.io/ (default signature - "secret" (you can change it in the **.env** file)) or modify the expiration time in the **.env** file.

## Implicit cases
1. If you are using PostgreSQL on your local machine, you need to create a user with the login "admindb"

## Contacts
Feel free to ask questions or suggest improvements :sunglasses: <br>
Telegram - [@BTNtelegram](https://t.me/BTNtelegram)
