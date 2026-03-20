package com.scout.scoutmanagement.backend.response;

import com.scout.scoutmanagement.domain.EventoActividad;
import com.scout.scoutmanagement.domain.EventoCurso;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.domain.EventoBingo;
import com.scout.scoutmanagement.domain.EventoCampamento;
import com.scout.scoutmanagement.domain.EventoConsejoDeGrupo;
import com.scout.scoutmanagement.domain.TipoEvento;
import com.scout.scoutmanagement.domain.Pagos.CostosVariables;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiResponseBuilder {

    public Map<String, Object> persona(String mensaje, Persona persona) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", persona.getId());
        respuesta.put("nombre", persona.getNombre());
        respuesta.put("apellido", persona.getApellido());
        respuesta.put("dni", persona.getDni());
        respuesta.put("mail", persona.getMail());
        respuesta.put("rol", persona.getRol());
        respuesta.put("ramaId", persona.getRama() != null ? persona.getRama().getId() : null);
        respuesta.put("activo", persona.getActivo());
        return respuesta;
    }

    public Map<String, Object> evento(String mensaje, Evento evento) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.putAll(eventoCalendario(evento));
        if (evento instanceof EventoActividad eventoActividad) {
            respuesta.put("objetivo", eventoActividad.getObjetivo());
            respuesta.put("desarrollo", eventoActividad.getDesarrollo());
            respuesta.put("materiales", eventoActividad.getMateriales());
            respuesta.put("recupero", eventoActividad.getRecupero());
            respuesta.put("duracionMinutos", eventoActividad.getDuracionMinutos());
            respuesta.put("educadorResponsableId", eventoActividad.getEducadorResponsable().getId());
            respuesta.put("beneficiarioACargoId", eventoActividad.getBeneficiarioACargo().getId());
        }
        if (evento instanceof EventoCurso eventoCurso) {
            respuesta.put("nivelCurso", eventoCurso.getNivel());
            respuesta.put(
                "suscriptosCursoIds",
                eventoCurso.getSuscriptos().stream().map(Persona::getId).toList()
            );
        }
        if (evento instanceof EventoCampamento campamento) {
            respuesta.put("lugar", campamento.getLugar());
            respuesta.put("contactoLugar", campamento.getContactoLugar());
        }
        if (evento instanceof EventoBingo eventoBingo) {
            respuesta.put("detalle", "Evento bingo creado");
            respuesta.put("cocinerosIds", eventoBingo.getCocineros().stream().map(Persona::getId).toList());
            respuesta.put("cartonerosIds", eventoBingo.getCartoneros().stream().map(Persona::getId).toList());
            respuesta.put("bachaIds", eventoBingo.getBacha().stream().map(Persona::getId).toList());
        }
        return respuesta;
    }

    public Map<String, Object> eventosCalendario(
        String mensaje,
        LocalDateTime desde,
        LocalDateTime hasta,
        Long personaId,
        List<Evento> eventos
    ) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("desde", desde);
        respuesta.put("hasta", hasta);
        respuesta.put("personaId", personaId);
        respuesta.put("cantidadEventos", eventos.size());
        respuesta.put(
            "eventos",
            eventos.stream().map(this::eventoCalendario).toList()
        );
        return respuesta;
    }

    public Map<String, Object> costo(String mensaje, CostosVariables costoCreado) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", costoCreado.getId());
        respuesta.put("nombre", costoCreado.getNombre());
        respuesta.put("cantidadCuotas", costoCreado.getCantidadCuotas());
        respuesta.put(
            "cuotas",
            costoCreado.getCuotas().stream().map(cuota -> {
                Map<String, Object> cuotaMap = new LinkedHashMap<>();
                cuotaMap.put("ordenCuota", cuota.getOrdenCuota());
                cuotaMap.put("monto", cuota.getMonto());
                return cuotaMap;
            }).toList()
        );
        respuesta.put("importeTotal", costoCreado.getImporte());
        respuesta.put(
            "creadoPorId",
            costoCreado.getCreadoPor() != null ? costoCreado.getCreadoPor().getId() : null
        );
        return respuesta;
    }

    public Map<String, Object> pago(String mensaje, Pago pagoCreado) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", pagoCreado.getId());
        respuesta.put("fecha", pagoCreado.getFecha());
        respuesta.put("personaId", pagoCreado.getPersona().getId());
        respuesta.put("personaQueRegistraId", pagoCreado.getPersonaQueRegistra().getId());
        respuesta.put("montoTotal", pagoCreado.getMontoTotal());
        respuesta.put("cuotasPagadas", cuotasPagadas(pagoCreado.getCuotas()));
        return respuesta;
    }

    public Map<String, Object> pagosDePersona(String mensaje, Long personaId, Integer anio, List<Pago> pagos) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("personaId", personaId);
        respuesta.put("anioFiltro", anio);
        respuesta.put("cantidadPagos", pagos.size());
        respuesta.put(
            "pagos",
            pagos.stream().map(pago -> {
                Map<String, Object> pagoMap = new LinkedHashMap<>();
                pagoMap.put("id", pago.getId());
                pagoMap.put("fecha", pago.getFecha());
                pagoMap.put("personaQueRegistraId", pago.getPersonaQueRegistra().getId());
                pagoMap.put("montoTotal", pago.getMontoTotal());
                pagoMap.put("cuotasPagadas", cuotasPagadas(pago.getCuotas()));
                return pagoMap;
            }).toList()
        );
        return respuesta;
    }

    public Map<String, Object> cuotasDePersona(
        String mensaje,
        Long personaId,
        boolean pendiente,
        boolean activo,
        Integer anio,
        List<Cuota> cuotas
    ) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("personaId", personaId);
        respuesta.put("filtroPendiente", pendiente);
        respuesta.put("filtroActivo", activo);
        respuesta.put("anioFiltro", anio);
        respuesta.put("cantidadCuotas", cuotas.size());
        respuesta.put("cuotas", cuotas.stream().map(this::cuotaEnRespuesta).toList());
        return respuesta;
    }

    private List<Map<String, Object>> cuotasPagadas(List<Cuota> cuotas) {
        return cuotas.stream().map(this::cuotaEnRespuesta).toList();
    }

    private Map<String, Object> cuotaEnRespuesta(Cuota cuota) {
        Map<String, Object> cuotaMap = new LinkedHashMap<>();
        cuotaMap.put("id", cuota.getId());
        cuotaMap.put("ordenCuota", cuota.getOrdenCuota());
        cuotaMap.put("monto", cuota.getMonto());
        cuotaMap.put("costoId", cuota.getCosto().getId());
        cuotaMap.put("motivo", cuota.getCosto().getMotivo());
        cuotaMap.put("pagoId", cuota.getPago() != null ? cuota.getPago().getId() : null);
        cuotaMap.put("pendiente", cuota.getPago() == null);
        return cuotaMap;
    }

    private Map<String, Object> eventoCalendario(Evento eventoEntidad) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("id", eventoEntidad.getId());
        evento.put("titulo", eventoEntidad.getTitulo());
        evento.put("fechaInicio", eventoEntidad.getFechaInicio());
        evento.put("fechaFin", eventoEntidad.getFechaFin());
        evento.put("alcanceEvento", eventoEntidad.getAlcanceEvento());
        evento.put("ramaId", eventoEntidad.getRama() != null ? eventoEntidad.getRama().getId() : null);
        evento.put("tipoEvento", tipoEvento(eventoEntidad));

        if (eventoEntidad instanceof EventoActividad eventoActividad) {
            evento.put("educadorResponsableId", eventoActividad.getEducadorResponsable().getId());
            evento.put("beneficiarioACargoId", eventoActividad.getBeneficiarioACargo().getId());
        }

        if (eventoEntidad instanceof EventoCurso eventoCurso) {
            evento.put("nivelCurso", eventoCurso.getNivel());
            evento.put("suscriptosCursoIds", eventoCurso.getSuscriptos().stream().map(Persona::getId).toList());
        }

        if (eventoEntidad instanceof EventoCampamento campamento) {
            evento.put("lugar", campamento.getLugar());
        }

        return evento;
    }

    private TipoEvento tipoEvento(Evento evento) {
        if (evento instanceof EventoActividad) {
            return TipoEvento.ACTIVIDAD;
        }
        if (evento instanceof EventoCurso) {
            return TipoEvento.CURSO;
        }
        if (evento instanceof EventoCampamento) {
            return TipoEvento.CAMPAMENTO;
        }
        if (evento instanceof EventoBingo) {
            return TipoEvento.BINGO;
        }
        if (evento instanceof EventoConsejoDeGrupo) {
            return TipoEvento.CONSEJO;
        }
        throw new IllegalArgumentException("Tipo de evento no soportado: " + evento.getClass().getSimpleName());
    }
}



