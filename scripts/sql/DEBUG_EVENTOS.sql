-- 🔧 SCRIPT DE DEBUGGING - Verificar Eventos en Base de Datos

-- 1. Ver todos los eventos
SELECT
    'TOTAL EVENTOS' as info,
    COUNT(*) as cantidad
FROM eventos;

-- 2. Ver detalles de los eventos
SELECT
    e.id,
    e.titulo,
    e.fecha_inicio,
    e.fecha_fin,
    e.alcance_evento,
    e.rama_id,
    e.dtype as tipo_evento
FROM eventos e
ORDER BY e.fecha_inicio DESC;

-- 3. Ver si hay ramas
SELECT
    'TOTAL RAMAS' as info,
    COUNT(*) as cantidad
FROM ramas;

-- 4. Ver detalle de ramas
SELECT
    r.id,
    r.nombre,
    COUNT(e.id) as cantidad_eventos
FROM ramas r
LEFT JOIN eventos e ON e.rama_id = r.id
GROUP BY r.id, r.nombre;

-- 5. Ver eventos de hoy (útil para debugging)
SELECT
    e.id,
    e.titulo,
    e.fecha_inicio,
    e.fecha_fin,
    e.alcance_evento
FROM eventos e
WHERE DATE(e.fecha_inicio) = CURDATE()
ORDER BY e.fecha_inicio;

-- 6. Ver eventos del mes actual
SELECT
    e.id,
    e.titulo,
    e.fecha_inicio,
    e.fecha_fin,
    e.alcance_evento,
    r.nombre as rama,
    e.dtype
FROM eventos e
LEFT JOIN ramas r ON e.rama_id = r.id
WHERE MONTH(e.fecha_inicio) = MONTH(CURDATE())
  AND YEAR(e.fecha_inicio) = YEAR(CURDATE())
ORDER BY e.fecha_inicio;

-- 7. Ver eventos en un rango específico (ajusta las fechas)
-- Cambiar 2026-03-01 y 2026-03-31 según el rango que quieras ver
SELECT
    e.id,
    e.titulo,
    e.fecha_inicio,
    e.fecha_fin,
    e.alcance_evento,
    r.nombre as rama
FROM eventos e
LEFT JOIN ramas r ON e.rama_id = r.id
WHERE e.fecha_inicio >= '2026-03-01'
  AND e.fecha_inicio <= '2026-03-31'
ORDER BY e.fecha_inicio;

-- 8. Ver actividades específicamente
SELECT
    e.id,
    e.titulo,
    e.fecha_inicio,
    e.fecha_fin,
    a.duracion_minutos,
    p_educador.nombre as educador,
    p_beneficiario.nombre as beneficiario
FROM eventos e
JOIN actividades a ON e.id = a.id
LEFT JOIN personas p_educador ON a.educador_id = p_educador.id
LEFT JOIN personas p_beneficiario ON a.beneficiario_id = p_beneficiario.id
ORDER BY e.fecha_inicio DESC;

-- 9. Ver si hay errores en las asociaciones
SELECT
    e.id,
    e.titulo,
    e.rama_id,
    r.nombre as rama_existe
FROM eventos e
LEFT JOIN ramas r ON e.rama_id = r.id
WHERE e.rama_id IS NOT NULL
  AND r.id IS NULL
ORDER BY e.id;

-- 10. Ver últimos 10 eventos insertados
SELECT
    e.id,
    e.titulo,
    e.fecha_inicio,
    e.fecha_fin,
    e.alcance_evento,
    e.dtype
FROM eventos e
ORDER BY e.id DESC
LIMIT 10;

