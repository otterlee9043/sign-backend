FROM openjdk:17

WORKDIR /app

ARG JAR_FILE=./build/libs/*.jar

COPY ${JAR_FILE} sign.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "sign.jar"]