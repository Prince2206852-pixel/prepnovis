FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY src/main/resources/certs/aiven-kafka-ca.pem /app/certs/aiven-kafka-ca.pem

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]