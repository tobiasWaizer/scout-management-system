# 📦 Estructura de Proyecto - Scout Management

## Resumen Ejecutivo

El proyecto ha sido reorganizado en una estructura clara y modular separando el **backend** del **frontend**, facilitando el mantenimiento y la escalabilidad.

---

## 📁 Estructura General

```
scoutmanagement/
├── backend/                    # 🔵 Todo lo relacionado al Backend
│   ├── controller/
│   │   └── api/               # REST Controllers
│   ├── service/               # Servicios de negocio
│   ├── repository/            # Acceso a datos (JPA)
│   ├── dto/                   # Data Transfer Objects
│   ├── exception/             # Excepciones personalizadas
│   └── response/              # Construcción de respuestas API
│
├── frontend/                  # 🟡 Todo lo relacionado al Frontend
│   └── controller/
│       └── view/              # View Controllers (MVC)
│
├── domain/                    # 📊 Modelos de dominio (compartido)
└── ScoutManagementApplication.java
```

---

## 🔵 Backend - Estructura Detallada

### `/backend/controller/api/`
Controllers REST que exponen la API:
- **PersonasController** → `/api/personas`
- **EventoController** → `/api/eventos`
- **RamaController** → `/api/ramas`
- **CostosController** → `/api/costos`
- **PagosController** → `/api/pagos`

**Paquete:** `com.scout.scoutmanagement.backend.controller.api`

### `/backend/service/`
Servicios que contienen la lógica de negocio:
- PersonaService
- EventoService
- RamaService
- CostosService
- PagosService
- Y más...

**Paquete:** `com.scout.scoutmanagement.backend.service`

### `/backend/repository/`
Interfaces JPA Repository para acceso a datos:
- PersonaRepository
- EventoRepository
- RamaRepository
- CostosRepository
- PagosRepository
- Y más...

**Paquete:** `com.scout.scoutmanagement.backend.repository`

### `/backend/dto/`
Data Transfer Objects para transferencia de datos:
- PersonaDTO
- EventoDTO
- CostoDTO
- PagoDTO
- Y más...

**Paquete:** `com.scout.scoutmanagement.backend.dto`

### `/backend/exception/`
Excepciones personalizadas:
- ObjectNotFoundException
- IllegalArgumentException
- Y más...

**Paquete:** `com.scout.scoutmanagement.backend.exception`

### `/backend/response/`
Constructores de respuesta API:
- ApiResponseBuilder

**Paquete:** `com.scout.scoutmanagement.backend.response`

---

## 🟡 Frontend - Estructura Detallada

### `/frontend/controller/view/`
Controllers de vista para renderizar HTML:
- **CalendarioViewController** → `/ramas/{id}/calendario`

**Paquete:** `com.scout.scoutmanagement.frontend.controller.view`

---

## 📊 Dominio Compartido

### `/domain/`
Modelos de entidades compartidas entre backend y frontend:
- Persona
- Evento (con subclases: Actividad, Curso, EventoCampamento, EventoBingo)
- Rama
- Costos (con subclases: CostosVariables, etc.)
- Y más...

**Paquete:** `com.scout.scoutmanagement.domain`

---

## 🎯 Flujo de Datos

```
Frontend (MVC)
      ↓
CalendarioViewController (view/)
      ↓
API REST (/api/*)
      ↓
Controllers (backend/controller/api/)
      ↓
Services (backend/service/)
      ↓
Repositories (backend/repository/)
      ↓
Database
```

---

## 📚 Jerarquía de Paquetes

```
com.scout.scoutmanagement
├── backend
│   ├── controller.api
│   ├── service
│   ├── repository
│   ├── dto
│   ├── exception
│   └── response
├── frontend
│   └── controller.view
└── domain
```

---

## ✅ Ventajas de esta Estructura

✅ **Separación clara** entre backend y frontend  
✅ **Facilita el mantenimiento** - cada carpeta tiene una responsabilidad específica  
✅ **Escalabilidad** - fácil agregar nuevos módulos  
✅ **Reutilización** - DTOs, Excepciones y Dominio compartidos  
✅ **Arquitectura limpia** - sigue principios SOLID  
✅ **Fácil testing** - estructura predecible para tests unitarios  

---

## 🔄 Cómo Importar

Cuando se importa en un archivo, usa:

```java
// Backend
import com.scout.scoutmanagement.backend.controller.api.*;
import com.scout.scoutmanagement.backend.service.*;
import com.scout.scoutmanagement.backend.repository.*;
import com.scout.scoutmanagement.backend.dto.*;
import com.scout.scoutmanagement.backend.exception.*;
import com.scout.scoutmanagement.backend.response.*;

// Frontend
import com.scout.scoutmanagement.frontend.controller.view.*;

// Dominio (compartido)
import com.scout.scoutmanagement.domain.*;
```

---

**Fecha de Creación:** 19 de Marzo, 2026  
**Versión:** 1.0

