# Scripts de pagos

## Archivos

- `01_migrar_legacy_pagos_a_cuotas.sql`: migra datos del modelo anterior al nuevo (`costos` + `cuota` + `pago`).
- `02_reset_pagos_y_recarga_inicial.sql`: borra pagos/costos y regenera afiliacion + cuota mensual para personas activas.

## Recomendacion antes de correr

1. Hacer backup.
2. Frenar la app o poner `pagos.bootstrap.enabled=false` temporalmente para evitar generacion concurrente.
3. Probar primero en una base de testing.

## Ejecucion en PowerShell

```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -d scout-management -f "c:\Users\tobi4\Desktop\PROYECTOS\SistemScout\scout-management\scripts\sql\01_migrar_legacy_pagos_a_cuotas.sql"
```

```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -d scout-management -f "c:\Users\tobi4\Desktop\PROYECTOS\SistemScout\scout-management\scripts\sql\02_reset_pagos_y_recarga_inicial.sql"
```

## Nota

Si en tu esquema legacy la tabla `cuota_variable` usaba otros nombres de columna, ajusta el bloque 2 del script de migracion.
