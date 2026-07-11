FROM eclipse-temurin:25
COPY /server/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]