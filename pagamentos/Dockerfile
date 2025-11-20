FROM gradle:jdk21-jammy AS build

WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
COPY ./src/main/resources/pagamentos.json /app/data/pagamentos.json

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]