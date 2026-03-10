FROM eclipse-temurin:21-jdk-alpine

COPY build/libs/*.jar /tmp/

# Выбор самой последней версии
RUN mv $(ls /tmp/*.jar | grep -v plain | sort -V | tail -1) app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]