# Scout Management

Aplicacion Spring Boot para gestionar actividades de un grupo scout.

## Modelo de dominio actual

Se unifico la gestion de beneficiarios y educadores en una sola entidad: `Persona`.

- `Persona` tiene un campo `rol` (`BENEFICIARIO` o `EDUCADOR`)
- La transicion de beneficiario a educador se hace con el endpoint de partida
- `Rama.jefeDeRama` referencia a `Persona` (validando rol educador en el servicio)

## Endpoints principales

### Personas

- `POST /api/personas`
- `PUT /api/personas/{id_persona}`
- `GET /api/personas/{id_persona}`
- `DELETE /api/personas/{id_persona}`
- `PATCH /api/personas/{id_persona}/partida`
- `PATCH /api/personas/{id_persona}/jefe-rama`

### Actividades

- `POST /api/actividades`

## Tecnologias

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Spring Validation
- Spring Boot Actuator
- SpringDoc OpenAPI (Swagger UI)
- Maven Wrapper (`mvnw.cmd`)

## Pruebas y ejecucion

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

## Requisitos previos

- JDK 17 instalado
- PostgreSQL en ejecucion
- (Opcional) IDE: IntelliJ IDEA

## Configuracion

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
```
