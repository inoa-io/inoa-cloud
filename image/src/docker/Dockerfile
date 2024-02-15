FROM docker.io/eclipse-temurin:17-jre

COPY service/target/libs /app/libs
COPY service/target/inoa-service-*.jar /app/inoa.jar
COPY app/dist /static

ENTRYPOINT ["java", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/inoa.jar"]
