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
COPY platform/api-gateway/src platform/api-gateway/src

RUN chmod +x mvnw
RUN ./mvnw -pl platform/api-gateway -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /opt/api-gateway

RUN groupadd --system commerce && useradd --system --gid commerce --home-dir /opt/api-gateway gateway
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/platform/api-gateway/target/api-gateway-*-exec.jar app.jar

USER gateway:commerce
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD ["curl", "-fsS", "http://127.0.0.1:8080/actuator/health"]

ENTRYPOINT ["java", "-jar", "app.jar"]
