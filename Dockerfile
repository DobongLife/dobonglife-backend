FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java"]
CMD ["-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", \
     "-jar", "app.jar"]