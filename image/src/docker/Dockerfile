FROM docker.io/eclipse-temurin:17-jre

COPY service/target/libs /app/libs
COPY service/target/inoa-service-*.jar /app/inoa.jar
COPY app/dist /app/static

USER 1000

ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
