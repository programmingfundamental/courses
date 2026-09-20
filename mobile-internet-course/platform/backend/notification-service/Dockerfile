FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/quarkus-app/ /app/
ENV QUARKUS_HTTP_HOST=0.0.0.0
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
