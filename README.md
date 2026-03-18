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
- `GET /api/actividades/rango?desde=YYYY-MM-DDTHH:mm:ss&hasta=YYYY-MM-DDTHH:mm:ss&personaId={id}`

### Calendario (MVP)

- Vista web: `/calendario.html`
- Frontend: FullCalendar via CDN
- Script: `src/main/resources/static/js/calendario.js`
- Fuente de datos: `GET /api/actividades/rango`
- Filtrado de visibilidad por persona: eventos `GENERAL`, eventos de su `RAMA`, y eventos `INDIVIDUAL` donde sea `personaObjetivo`

Ejemplo de request para calendario:

```http
GET /api/actividades/rango?desde=2026-01-01T00:00:00&hasta=2026-12-31T23:59:59&personaId=1
```

Ejemplo de evento en respuesta:

```json
{
  "id": 15,
  "title": "Reunion de rama",
  "start": "2026-04-05T18:00:00",
  "end": "2026-04-05T19:30:00",
  "educadorResponsableId": 1,
  "beneficiarioACargoId": 2
}
```

Ejemplos de creacion por alcance:

```json
{
  "titulo": "Reunion de rama",
  "objetivo": "Planificacion semanal",
  "desarrollo": "Definir tareas",
  "duracionMinutos": 90,
  "fechaInicio": "2026-04-05T18:00:00",
  "educadorResponsableId": 1,
  "alcanceEvento": "RAMA",
  "ramaId": 2
}
```

```json
{
  "titulo": "Curso de educador",
  "objetivo": "Formacion individual",
  "desarrollo": "Modulo 1",
  "duracionMinutos": 120,
  "fechaInicio": "2026-04-10T19:00:00",
  "educadorResponsableId": 1,
  "alcanceEvento": "INDIVIDUAL",
  "personaObjetivoId": 1
}
```

```json
{
  "titulo": "Fogon de grupo",
  "objetivo": "Integracion general",
  "desarrollo": "Actividad abierta",
  "duracionMinutos": 180,
  "fechaInicio": "2026-05-01T20:00:00",
  "educadorResponsableId": 1,
  "alcanceEvento": "GENERAL"
}
```

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
