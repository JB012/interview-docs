FROM gradle:latest AS build

WORKDIR /app
COPY . .

RUN gradle build -x test

FROM eclipse-temurin:25

WORKDIR /app

COPY --from=build /app/build/libs/server-0.1-all.jar my.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","my.jar"]