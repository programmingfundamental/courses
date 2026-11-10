FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S lab && adduser -S -G lab lab && mkdir /app/data && chown lab:lab /app/data
COPY --from=build /build/target/web-security-assessment.jar /app/app.jar
USER lab
ENV HOST=0.0.0.0 PORT=9010 DATA_DIR=/app/data
EXPOSE 9010
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
