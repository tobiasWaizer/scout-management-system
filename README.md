# Scout Management

Aplicación Spring Boot para gestionar actividades de un grupo scout.

## Tecnologías

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Spring Boot Actuator
- SpringDoc OpenAPI (Swagger UI)
- Maven Wrapper (`mvnw.cmd`)

# Pruebas

- .\mvnw.cmd clean test
- .\mvnw.cmd spring-boot:run

## Requisitos previos

- JDK 17 instalado
- PostgreSQL en ejecución
- (Opcional) IDE: IntelliJ IDEA

## Configuración

Configura `src/main/resources/application.properties` con tus datos de PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/scout_management
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
