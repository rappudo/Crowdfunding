FROM gradle:jdk21-jammy AS build

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
COPY ./src/main/resources/usuarios.json /app/data/usuarios.json

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]