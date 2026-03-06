# Etapa 1: Construcción
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copiar wrapper de maven y pom
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline

# Construir el proyecto
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
