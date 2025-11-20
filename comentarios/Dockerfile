FROM gradle:jdk21-jammy AS build

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
COPY ./src/main/resources/comentarios.json /app/data/comentarios.json

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]