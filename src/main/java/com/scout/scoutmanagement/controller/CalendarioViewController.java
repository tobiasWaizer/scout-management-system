package com.scout.scoutmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controlador de vista para el calendario de eventos.
 * 
 * Proporciona endpoints para redirigir a la vista del calendario con filtros
 * pre-aplicados.
 * 
 * MEJORAS IMPLEMENTADAS:
 * ✅ Interfaz moderna con gradientes y diseño responsive
 * ✅ Desplegable de ramas con carga dinámica
 * ✅ Checkboxes de tipos de evento (CURSO, BINGO, ACTIVIDAD, CAMPAMENTO)
 * ✅ Modal mejorado con detalles de eventos
 * ✅ Colores distintivos por tipo y alcance
 * ✅ Leyenda de alcances
 * ✅ Iconos Font Awesome
 * ✅ Filtrado en tiempo real
 * ✅ Responsividad completa (mobile, tablet, desktop)
 * 
 * @see /static/calendario.html
 * @see /static/js/calendario.js
 */
@Controller
public class CalendarioViewController {

    /**
     * Redirige a la vista del calendario filtrando por rama.
     * 
     * Uso: GET /ramas/{idRama}/calendario
     * Redirige a: /calendario.html?ramaId={idRama}
     * 
     * Ejemplo:
     * - GET /ramas/1/calendario → /calendario.html?ramaId=1
     * - La vista del calendario cargará eventos solo de la rama 1
     * 
     * @param idRama ID de la rama a filtrar
     * @return URL de redirección con parámetro ramaId
     */
    @GetMapping("/ramas/{idRama}/calendario")
    public String redirigirCalendarioDeRama(@PathVariable Long idRama) {
        return "redirect:/calendario.html?ramaId=" + idRama;
    }
}

