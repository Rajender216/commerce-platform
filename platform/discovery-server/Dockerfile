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
COPY platform/discovery-server/src platform/discovery-server/src

RUN chmod +x mvnw
RUN ./mvnw -pl platform/discovery-server -am -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /opt/discovery-server

RUN groupadd --system commerce && useradd --system --gid commerce --home-dir /opt/discovery-server discovery
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /workspace/platform/discovery-server/target/discovery-server-*-exec.jar app.jar

USER discovery:commerce
EXPOSE 8761

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=5 \
    CMD ["curl", "-fsS", "http://127.0.0.1:8761/actuator/health"]

ENTRYPOINT ["java", "-jar", "app.jar"]
