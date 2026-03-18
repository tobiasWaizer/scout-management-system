-- Migracion de modelo legacy de pagos/costos al modelo actual (Costos + Cuota + Pago)
-- Ejecutar en PostgreSQL sobre la base scout-management.
-- Recomendado: correr primero en una copia de la BD.

BEGIN;

-- 1) Si un costo aun no tiene cuotas, crear cuota unica con el importe del costo.
--    Si la tabla legacy tenia costos.pago_id, se conserva ese vinculo en cuota.pago_id.
DO $$
DECLARE
    v_tiene_pago_id boolean;
BEGIN
    SELECT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'costos'
          AND column_name = 'pago_id'
    ) INTO v_tiene_pago_id;

    IF v_tiene_pago_id THEN
        EXECUTE '
            INSERT INTO cuota (orden_cuota, monto, costo_id, pago_id)
            SELECT 1, c.importe, c.id, c.pago_id
            FROM costos c
            WHERE NOT EXISTS (
                SELECT 1 FROM cuota q WHERE q.costo_id = c.id
            )
        ';
    ELSE
        EXECUTE '
            INSERT INTO cuota (orden_cuota, monto, costo_id, pago_id)
            SELECT 1, c.importe, c.id, NULL
            FROM costos c
            WHERE NOT EXISTS (
                SELECT 1 FROM cuota q WHERE q.costo_id = c.id
            )
        ';
    END IF;
END $$;

-- 2) Si existe tabla legacy cuota_variable, migrar sus cuotas al nuevo esquema cuota.
--    Ajusta nombres de columnas si en tu esquema anterior eran distintos.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name = 'cuota_variable'
    ) THEN
        INSERT INTO cuota (orden_cuota, monto, costo_id, pago_id)
        SELECT cv.orden_cuota, cv.monto, cv.costo_variable_id, cv.pago_id
        FROM cuota_variable cv
        WHERE NOT EXISTS (
            SELECT 1
            FROM cuota q
            WHERE q.costo_id = cv.costo_variable_id
              AND q.orden_cuota = cv.orden_cuota
        );
    END IF;
END $$;

-- 3) Recalcular monto total de cada pago desde sus cuotas.
UPDATE pago p
SET monto_total = x.total
FROM (
    SELECT q.pago_id, COALESCE(SUM(q.monto), 0) AS total
    FROM cuota q
    WHERE q.pago_id IS NOT NULL
    GROUP BY q.pago_id
) x
WHERE p.id = x.pago_id;

-- 4) Recalcular importe total de cada costo desde sus cuotas.
UPDATE costos c
SET importe = x.total
FROM (
    SELECT q.costo_id, COALESCE(SUM(q.monto), 0) AS total
    FROM cuota q
    GROUP BY q.costo_id
) x
WHERE c.id = x.costo_id;

COMMIT;

-- Validaciones rapidas post-migracion:
-- SELECT COUNT(*) AS costos_sin_cuotas FROM costos c WHERE NOT EXISTS (SELECT 1 FROM cuota q WHERE q.costo_id = c.id);
-- SELECT id, monto_total FROM pago ORDER BY id;
-- SELECT id, importe FROM costos ORDER BY id;
