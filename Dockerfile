FROM openjdk:17

WORKDIR /app

ARG JAR_FILE=./build/libs/*.jar

COPY ${JAR_FILE} sign.jar

COPY ./src/main/resources/sql /app/sql

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "sign.jar"]