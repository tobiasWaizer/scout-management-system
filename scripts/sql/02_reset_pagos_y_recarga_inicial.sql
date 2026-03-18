-- Reset completo de pagos/costos y recarga inicial de costos fijos.
-- ADVERTENCIA: elimina datos de pagos y costos.

BEGIN;

-- 0) Vaciar modulo pagos.
TRUNCATE TABLE cuota RESTART IDENTITY CASCADE;
TRUNCATE TABLE pago RESTART IDENTITY CASCADE;
TRUNCATE TABLE costos RESTART IDENTITY CASCADE;

-- 1) Parametros de generacion inicial.
--    Ajustar anio/mes_inicio/importes segun necesidad.
WITH params AS (
    SELECT
        EXTRACT(YEAR FROM CURRENT_DATE)::int AS anio,
        3::int AS mes_inicio,
        5000::numeric(12,2) AS importe_afiliacion,
        10000::numeric(12,2) AS importe_cuota_mensual
),
personas_activas AS (
    SELECT p.id
    FROM personas p
    WHERE p.activo = true
),
afiliaciones_insertadas AS (
    INSERT INTO costos (tipo_costo, persona_objetivo_id, creado_por_id, importe, anio, tipo, cantidad_meses)
    SELECT
        'AFILIACION',
        pa.id,
        NULL,
        prm.importe_afiliacion,
        prm.anio,
        'AFILIACION',
        1
    FROM personas_activas pa
    CROSS JOIN params prm
    RETURNING id
)
INSERT INTO cuota (orden_cuota, monto, costo_id, pago_id)
SELECT 1, prm.importe_afiliacion, a.id, NULL
FROM afiliaciones_insertadas a
CROSS JOIN params prm;

WITH params AS (
    SELECT
        EXTRACT(YEAR FROM CURRENT_DATE)::int AS anio,
        3::int AS mes_inicio,
        10000::numeric(12,2) AS importe_cuota_mensual
),
personas_activas AS (
    SELECT p.id
    FROM personas p
    WHERE p.activo = true
),
cuotas_mensuales_insertadas AS (
    INSERT INTO costos (tipo_costo, persona_objetivo_id, creado_por_id, importe, anio, tipo, cantidad_meses)
    SELECT
        'FIJO',
        pa.id,
        NULL,
        ((12 - prm.mes_inicio + 1) * prm.importe_cuota_mensual),
        prm.anio,
        'CUOTA_MENSUAL',
        (12 - prm.mes_inicio + 1)
    FROM personas_activas pa
    CROSS JOIN params prm
    RETURNING id
)
INSERT INTO cuota (orden_cuota, monto, costo_id, pago_id)
SELECT gs.orden, prm.importe_cuota_mensual, cm.id, NULL
FROM cuotas_mensuales_insertadas cm
CROSS JOIN params prm
JOIN LATERAL generate_series(1, (12 - prm.mes_inicio + 1)) AS gs(orden) ON true;

COMMIT;

-- Validaciones rapidas:
-- SELECT tipo_costo, tipo, COUNT(*) FROM costos GROUP BY tipo_costo, tipo ORDER BY tipo_costo, tipo;
-- SELECT COUNT(*) AS cuotas_sin_pago FROM cuota WHERE pago_id IS NULL;
