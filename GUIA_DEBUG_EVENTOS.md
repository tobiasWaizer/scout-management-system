# 🔧 GUÍA DE DEBUGGING - Eventos No Se Muestran en el Calendario

## 📋 Problema Reportado

El calendario no muestra ningún evento aunque hay dos cargados en la base de datos.

## 🔍 Pasos para Diagnosticar

### Paso 1: Abrir la Consola del Navegador

1. Abre el calendario en http://localhost:8080/calendario.html
2. Presiona **F12** para abrir las herramientas de desarrollo
3. Ir a la pestaña **Console** (Consola)

Deberías ver logs similares a estos:

```
📍 Cargando ramas desde /api/ramas...
✅ Ramas cargadas: (2) [{…}, {…}]
✅ 2 ramas agregadas al dropdown
🔍 Cargando eventos desde: /api/eventos/rango?desde=2026-02-17T00:00:00&hasta=2026-04-18T23:59:59
📥 Respuesta de API: {mensaje: "Eventos obtenidos exitosamente", desde: "2026-02-17T00:00:00", hasta: "2026-04-18T23:59:59", personaId: null, cantidadEventos: 2, eventos: Array(2)}
  📅 Evento: "Mi Actividad" - Tipo: ACTIVIDAD - Start: 2026-03-15T10:00:00 - End: 2026-03-15T11:00:00
  ✅ Mapeado: {id: 1, title: "Mi Actividad", start: "2026-03-15T10:00:00", end: "2026-03-15T11:00:00", ...}
```

### Paso 2: Verificar la Respuesta de la API

Si ves un error o la respuesta está vacía:

1. En la consola, busca la línea que dice: `🔍 Cargando eventos desde:`
2. Haz click en esa línea para expandirla
3. Copia la URL completa
4. Abre una nueva pestaña y pega esa URL
5. Deberías ver un JSON con los eventos

**Ejemplo de respuesta correcta:**

```json
{
  "mensaje": "Eventos obtenidos exitosamente",
  "desde": "2026-02-17T00:00:00",
  "hasta": "2026-04-18T23:59:59",
  "personaId": null,
  "cantidadEventos": 2,
  "eventos": [
    {
      "id": 1,
      "titulo": "Mi Actividad",
      "title": "Mi Actividad",
      "fechaInicio": "2026-03-15T10:00:00",
      "start": "2026-03-15T10:00:00",
      "fechaFin": "2026-03-15T11:00:00",
      "end": "2026-03-15T11:00:00",
      "alcanceEvento": "RAMA",
      "ramaId": 1,
      "tipoEvento": "ACTIVIDAD"
    }
  ]
}
```

### Paso 3: Verificar Problemas Comunes

#### ❌ Problema: `cantidadEventos: 0`

**Causas posibles:**

1. **Las fechas no coinciden**
   - Los eventos tienen `fechaInicio` fuera del rango solicitado
   - Solución: Crear eventos dentro del mes actual visible en el calendario

2. **No hay eventos en esa rama**
   - Si seleccionaste una rama en el dropdown y `ramaId` se envía, pero los eventos pertenecen a otra rama
   - Solución: Selecciona "Todas las ramas" en el dropdown

3. **El tipo de evento está filtrado**
   - Si deseleccionaste ACTIVIDAD, CURSO, BINGO o CAMPAMENTO
   - Solución: Marca todos los checkboxes de tipos

#### ❌ Problema: Error 404 o 500 en la API

**Qué verificar:**

1. El servidor está corriendo:
   ```bash
   curl http://localhost:8080/api/ramas
   ```

2. Verifica que la base de datos tenga datos:
   - Abre tu cliente SQL
   - Ejecuta: `SELECT * FROM eventos;`

#### ❌ Problema: `"eventos": []` (lista vacía)

**Causas probables:**

1. Los eventos no están dentro del rango de fechas visible
   - El calendario muestra un mes/semana/día específico
   - Navega a donde están los eventos
   - Ejemplo: Si creaste eventos en marzo, asegúrate de estar viendo marzo

2. Los tipos de evento están deseleccionados
   - Abre el calendario
   - Marca todos los checkboxes: ACTIVIDAD, CURSO, BINGO, CAMPAMENTO

3. Hay un filtro de rama activo
   - Selecciona "Todas las ramas" en el dropdown

## 🛠️ Soluciones

### Solución 1: Limpiar Caché y Recargar

1. Presiona **Ctrl+Shift+Delete** (Windows) o **Cmd+Shift+Delete** (Mac)
2. Elimina el caché del navegador
3. Recarga la página
4. Verifica de nuevo la consola

### Solución 2: Crear un Evento de Prueba

Usa la API para crear un evento en la fecha actual:

```bash
curl -X POST http://localhost:8080/api/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Evento Test",
    "fechaInicio": "2026-03-18T10:00:00",
    "fechaFin": "2026-03-18T12:00:00",
    "tipoEvento": "ACTIVIDAD",
    "alcanceEvento": "GENERAL"
  }'
```

Luego recarga el calendario y verifica si aparece.

### Solución 3: Verificar Datos en BD

```sql
-- Ver todos los eventos
SELECT id, titulo, fecha_inicio, fecha_fin, alcance_evento, rama_id 
FROM eventos;

-- Ver solo eventos de hoy/semana
SELECT * FROM eventos 
WHERE fecha_inicio >= DATE_SUB(NOW(), INTERVAL 30 DAY)
AND fecha_inicio <= DATE_ADD(NOW(), INTERVAL 30 DAY);
```

### Solución 4: Ver Logs del Servidor

En el archivo de logs de Spring Boot (usualmente `logs/application.log` o consola):

Busca líneas como:
```
[EventoController] GET /api/eventos/rango
[EventoService] obtenerEventosEnRango: desde=... hasta=... personaId=... ramaId=... tipoEvento=...
```

## 📊 Checklist de Verificación

- [ ] Consola F12 sin errores
- [ ] API devuelve `cantidadEventos > 0`
- [ ] Los eventos tienen `fechaInicio` y `fechaFin`
- [ ] Las fechas están en el rango del calendario visible
- [ ] Todos los checkboxes de tipos están marcados
- [ ] Dropdown de ramas dice "Todas las ramas"
- [ ] Base de datos tiene registros en la tabla `eventos`
- [ ] Servidor está corriendo sin errores

## 🎯 Siguientes Pasos

Si aún no aparecen los eventos:

1. **Comparte los logs** de la consola (F12 → Console)
2. **Comparte la URL** que se intenta cargar
3. **Comparte la respuesta JSON** del endpoint `/api/eventos/rango`
4. **Verifica** si los eventos existen en la BD con:
   ```sql
   SELECT COUNT(*) FROM eventos;
   ```

## 📝 Información Útil

### Endpoint de Eventos

```
GET /api/eventos/rango
Parámetros:
  - desde: LocalDateTime (formato ISO: 2026-03-18T00:00:00)
  - hasta: LocalDateTime
  - ramaId (opcional): Long
  - personaId (opcional): Long
  - tipoEvento (opcional): ACTIVIDAD, CURSO, BINGO, CAMPAMENTO
```

### Respuesta Esperada

```json
{
  "mensaje": "Eventos obtenidos exitosamente",
  "desde": "...",
  "hasta": "...",
  "cantidadEventos": 2,
  "eventos": [...]
}
```

### Log Esperado en Consola

```
📍 Cargando ramas desde /api/ramas...
✅ Ramas cargadas: [...]
🔍 Cargando eventos desde: /api/eventos/rango?...
📥 Respuesta de API: {...}
  📅 Evento: "..." - Tipo: ... - Start: ... - End: ...
  ✅ Mapeado: {...}
✅ Eventos mapeados: [...]
```

## 🔗 Enlaces Útiles

- Calendario: http://localhost:8080/calendario.html
- Panel Principal: http://localhost:8080/index.html
- Guía: http://localhost:8080/guia-calendario.html

## 💡 Tips

1. **Usa el DevTools Network tab** para ver exactamente qué se envía y recibe
2. **Crea eventos simples primero** (sin campamentos complejos)
3. **Prueba con "Todas las ramas"** para descartar filtros
4. **Verifica las fechas** - deben estar en el rango del mes visible

---

**¿Problemas?** Revisa la consola (F12) y proporciona:
1. Los logs de la consola
2. La URL cargada
3. El JSON retornado por la API
4. Los datos en la base de datos

