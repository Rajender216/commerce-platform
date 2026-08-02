# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml
COPY build-platform/pom.xml build-platform/pom.xml
COPY platform/discovery-server/pom.xml platform/discovery-server/pom.xml
COPY platform/api-gateway/pom.xml platform/api-gateway/pom.xml
COPY services/catalog-service/pom.xml services/catalog-service/pom.xml
COPY services/customer-service/pom.xml services/customer-service/pom.xml
COPY services/catalog-service/src services/catalog-service/src

RUN chmod +x mvnw
RUN ./mvnw -pl services/catalog-service -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /opt/catalog-service

RUN groupadd --system catalog && useradd --system --gid catalog --home-dir /opt/catalog-service catalog
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/services/catalog-service/target/catalog-service-*-exec.jar app.jar

USER catalog:catalog
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD ["curl", "-fsS", "http://127.0.0.1:8080/actuator/health"]

ENTRYPOINT ["java", "-jar", "app.jar"]
