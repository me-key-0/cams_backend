FROM eclipse-temurin:21-jdk
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
