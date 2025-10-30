FROM openjdk:21

COPY build/libs/main.jar /main.jar

ENTRYPOINT ["java", "-jar", "/main.jar"]
