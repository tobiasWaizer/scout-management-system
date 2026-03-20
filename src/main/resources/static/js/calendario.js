function toLocalDateTimeString(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}

function toDateTimeLocalValue(dateValue) {
    if (!dateValue) {
        return "";
    }

    const date = dateValue instanceof Date ? dateValue : new Date(dateValue);
    if (Number.isNaN(date.getTime())) {
        return "";
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function dateTimeLocalToApiString(value) {
    if (!value) {
        return null;
    }
    return `${value}:00`;
}

const eventosEnActualizacion = new Set();
const MODAL_ANIMACION_MS = 180;
const DEBUG = false;

function debugLog(...args) {
    if (DEBUG) {
        console.log(...args);
    }
}

function debugError(...args) {
    if (DEBUG) {
        console.error(...args);
    }
}

function prepararAnimacionModal(overlay, content) {
    overlay.style.opacity = "0";
    overlay.style.transition = `opacity ${MODAL_ANIMACION_MS}ms ease`;
    content.style.opacity = "0";
    content.style.transform = "translateY(8px) scale(0.98)";
    content.style.transition = `opacity ${MODAL_ANIMACION_MS}ms ease, transform ${MODAL_ANIMACION_MS}ms ease`;
}

function mostrarModalAnimado(overlay, content) {
    window.requestAnimationFrame(() => {
        overlay.style.opacity = "1";
        content.style.opacity = "1";
        content.style.transform = "translateY(0) scale(1)";
    });
}

function cerrarModalAnimado(overlay, content, onClosed) {
    overlay.style.opacity = "0";
    content.style.opacity = "0";
    content.style.transform = "translateY(8px) scale(0.98)";

    window.setTimeout(() => {
        overlay.remove();
        if (typeof onClosed === "function") {
            onClosed();
        }
    }, MODAL_ANIMACION_MS);
}

function mostrarFeedbackCalendario(mensaje, esError) {
    const id = "calendario-feedback";
    let feedback = document.getElementById(id);

    if (!feedback) {
        feedback = document.createElement("div");
        feedback.id = id;
        feedback.style.cssText = "position: fixed; top: 16px; right: 16px; z-index: 10000; padding: 10px 14px; border-radius: 8px; color: #fff; font-weight: 600; box-shadow: 0 6px 18px rgba(0,0,0,0.2);";
        document.body.appendChild(feedback);
    }

    feedback.textContent = mensaje;
    feedback.style.backgroundColor = esError ? "#d32f2f" : "#2e7d32";
    feedback.style.display = "block";

    window.clearTimeout(feedback._hideTimeout);
    feedback._hideTimeout = window.setTimeout(() => {
        feedback.style.display = "none";
    }, 3000);
}

async function obtenerMensajeError(response) {
    try {
        const data = await response.json();
        if (data && data.mensaje) {
            return data.mensaje;
        }
    } catch (error) {
        // Si el backend no devolvio JSON valido, usamos el status HTTP.
    }
    return `Error HTTP ${response.status}`;
}

async function persistirFechasEvento(evento) {
    const eventoId = evento.id;
    if (!eventoId) {
        throw new Error("No se pudo reprogramar: el evento no tiene ID");
    }

    if (eventosEnActualizacion.has(eventoId)) {
        throw new Error("El evento ya se esta actualizando, intenta nuevamente");
    }

    const inicio = evento.start;
    const fin = evento.end || evento.start;

    if (!inicio || !fin) {
        throw new Error("No se pudo reprogramar: fechas invalidas");
    }

    eventosEnActualizacion.add(eventoId);
    try {
        const response = await fetch(`/api/eventos/${encodeURIComponent(eventoId)}/fechas`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                fechaInicio: toLocalDateTimeString(inicio),
                fechaFin: toLocalDateTimeString(fin)
            })
        });

        if (!response.ok) {
            const mensaje = await obtenerMensajeError(response);
            throw new Error(mensaje);
        }
    } finally {
        eventosEnActualizacion.delete(eventoId);
    }
}

async function obtenerDetalleEvento(eventoId) {
    const response = await fetch(`/api/eventos/${encodeURIComponent(eventoId)}`);
    if (!response.ok) {
        const mensaje = await obtenerMensajeError(response);
        throw new Error(mensaje);
    }
    return response.json();
}

function construirPayloadEdicion(detalle, overrides) {
    const payload = {
        tipoEvento: detalle.tipoEvento,
        titulo: overrides.titulo,
        fechaInicio: overrides.fechaInicio,
        fechaFin: overrides.fechaFin,
        alcanceEvento: detalle.alcanceEvento,
        ramaId: detalle.ramaId,
        educadorResponsableId: detalle.educadorResponsableId,
        beneficiarioACargoId: detalle.beneficiarioACargoId,
        duracionMinutos: detalle.duracionMinutos,
        objetivo: detalle.objetivo,
        desarrollo: detalle.desarrollo,
        materiales: detalle.materiales,
        recupero: detalle.recupero,
        nivelCurso: detalle.nivelCurso,
        suscriptosCursoIds: detalle.suscriptosCursoIds,
        lugar: detalle.lugar,
        contactoLugar: detalle.contactoLugar,
        cocinerosIds: detalle.cocinerosIds,
        cartonerosIds: detalle.cartonerosIds,
        bachaIds: detalle.bachaIds
    };

    Object.keys(payload).forEach(key => {
        if (payload[key] === undefined) {
            delete payload[key];
        }
    });

    return payload;
}

async function persistirEdicionEvento(eventoId, payload) {
    const response = await fetch(`/api/eventos/${encodeURIComponent(eventoId)}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        const mensaje = await obtenerMensajeError(response);
        throw new Error(mensaje);
    }
}

async function eliminarEventoPorId(eventoId) {
    const response = await fetch(`/api/eventos/${encodeURIComponent(eventoId)}`, {
        method: "DELETE"
    });

    if (!response.ok) {
        const mensaje = await obtenerMensajeError(response);
        throw new Error(mensaje);
    }
}

function estaSobrePapelera(jsEvent, papeleraEl) {
    if (!jsEvent || !papeleraEl) {
        return false;
    }

    const rect = papeleraEl.getBoundingClientRect();
    const x = jsEvent.clientX;
    const y = jsEvent.clientY;

    return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
}

function marcarPapeleraActiva(papeleraEl, activa, sobre) {
    if (!papeleraEl) {
        return;
    }

    papeleraEl.classList.toggle("activa", Boolean(activa));
    papeleraEl.classList.toggle("sobre", Boolean(sobre));
}

function crearTrackerPapelera(papeleraEl) {
    let listener = null;

    return {
        iniciar() {
            if (!papeleraEl || listener) {
                return;
            }

            listener = (e) => {
                const estaSobre = estaSobrePapelera(e, papeleraEl);
                marcarPapeleraActiva(papeleraEl, true, estaSobre);
            };

            document.addEventListener("mousemove", listener);
        },
        detener() {
            if (listener) {
                document.removeEventListener("mousemove", listener);
                listener = null;
            }
            marcarPapeleraActiva(papeleraEl, false, false);
        }
    };
}

function confirmarEliminacionEvento(tituloEvento) {
    return new Promise((resolve) => {
        const modal = document.createElement("div");
        modal.style.cssText = "position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.45); display: flex; align-items: center; justify-content: center; z-index: 10002; padding: 20px;";

        const content = document.createElement("div");
        content.style.cssText = "background-color: #fff; border-radius: 12px; padding: 22px; max-width: 460px; width: 100%; box-shadow: 0 12px 36px rgba(0,0,0,0.22); border-top: 4px solid #d32f2f;";
        content.innerHTML = `
            <div style="display:flex; align-items:center; gap:10px; margin-bottom:10px; color:#d32f2f; font-weight:700; font-size:18px;">
                <i class="fas fa-triangle-exclamation"></i>
                <span>Confirmar eliminación</span>
            </div>
            <p style="margin:0 0 8px; color:#1f2937; line-height:1.45;">
                Vas a eliminar este evento de forma permanente.
            </p>
            <p style="margin:0 0 18px; color:#6b7280; font-size:13px; line-height:1.45;">
                <strong>Evento:</strong> ${String(tituloEvento || "Sin titulo").replace(/</g, "&lt;").replace(/>/g, "&gt;")}
            </p>
            <div style="display:flex; gap:10px;">
                <button id="btn-confirmar-eliminar" style="flex:1; padding:10px; background:#d32f2f; color:#fff; border:none; border-radius:6px; cursor:pointer; font-weight:600;">
                    <i class="fas fa-trash"></i> Eliminar
                </button>
                <button id="btn-cancelar-eliminar" style="flex:1; padding:10px; background:#6c757d; color:#fff; border:none; border-radius:6px; cursor:pointer; font-weight:600;">
                    Cancelar
                </button>
            </div>
        `;

        modal.appendChild(content);
        document.body.appendChild(modal);
        prepararAnimacionModal(modal, content);
        mostrarModalAnimado(modal, content);

        let keyHandler = null;
        let yaCerrado = false;

        const cerrar = (resultado) => {
            if (yaCerrado) {
                return;
            }
            yaCerrado = true;

            if (keyHandler) {
                document.removeEventListener("keydown", keyHandler);
                keyHandler = null;
            }
            cerrarModalAnimado(modal, content, () => resolve(resultado));
        };

        const confirmarBtn = content.querySelector("#btn-confirmar-eliminar");
        const cancelarBtn = content.querySelector("#btn-cancelar-eliminar");

        confirmarBtn.onclick = () => cerrar(true);
        cancelarBtn.onclick = () => cerrar(false);
        modal.onclick = (e) => {
            if (e.target === modal) {
                cerrar(false);
            }
        };

        keyHandler = (e) => {
            if (e.key === "Escape") {
                cerrar(false);
            }
        };
        document.addEventListener("keydown", keyHandler);
    });
}

function abrirModalEdicionEvento(evento, calendar, detalleActual) {
    const modal = document.createElement("div");
    modal.style.cssText = "position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 10001; padding: 20px;";

    const content = document.createElement("div");
    content.style.cssText = "background-color: #fff; border-radius: 12px; padding: 24px; max-width: 540px; width: 100%; box-shadow: 0 10px 40px rgba(0,0,0,0.2);";
    content.innerHTML = `
        <h3 style="margin-top:0; margin-bottom:16px;">Editar evento</h3>
        <label style="display:block; font-weight:600; margin-bottom:6px;">Titulo</label>
        <input id="edit-titulo" type="text" value="${(detalleActual.titulo || "").replace(/"/g, "&quot;")}" style="width:100%; margin-bottom:12px; padding:10px; border:1px solid #d0d7de; border-radius:6px;" />

        <label style="display:block; font-weight:600; margin-bottom:6px;">Fecha inicio</label>
        <input id="edit-fecha-inicio" type="datetime-local" value="${toDateTimeLocalValue(detalleActual.fechaInicio || evento.start)}" style="width:100%; margin-bottom:12px; padding:10px; border:1px solid #d0d7de; border-radius:6px;" />

        <label style="display:block; font-weight:600; margin-bottom:6px;">Fecha fin</label>
        <input id="edit-fecha-fin" type="datetime-local" value="${toDateTimeLocalValue(detalleActual.fechaFin || evento.end || evento.start)}" style="width:100%; margin-bottom:16px; padding:10px; border:1px solid #d0d7de; border-radius:6px;" />

        <div style="display:flex; gap:8px;">
            <button id="btn-guardar-edicion" style="flex:1; padding:10px; background:#2e7d32; color:#fff; border:none; border-radius:6px; cursor:pointer;">Guardar cambios</button>
            <button id="btn-cancelar-edicion" style="flex:1; padding:10px; background:#6c757d; color:#fff; border:none; border-radius:6px; cursor:pointer;">Cancelar</button>
        </div>
    `;

    modal.appendChild(content);
    document.body.appendChild(modal);
    prepararAnimacionModal(modal, content);
    mostrarModalAnimado(modal, content);

    const tituloInput = content.querySelector("#edit-titulo");
    const fechaInicioInput = content.querySelector("#edit-fecha-inicio");
    const fechaFinInput = content.querySelector("#edit-fecha-fin");
    const guardarBtn = content.querySelector("#btn-guardar-edicion");
    const cancelarBtn = content.querySelector("#btn-cancelar-edicion");

    cancelarBtn.onclick = () => cerrarModalAnimado(modal, content);
    modal.onclick = (e) => {
        if (e.target === modal) {
            cerrarModalAnimado(modal, content);
        }
    };

    guardarBtn.onclick = async () => {
        const titulo = (tituloInput.value || "").trim();
        const fechaInicio = dateTimeLocalToApiString(fechaInicioInput.value);
        const fechaFin = dateTimeLocalToApiString(fechaFinInput.value);

        if (!titulo || !fechaInicio || !fechaFin) {
            mostrarFeedbackCalendario("Completa titulo, inicio y fin", true);
            return;
        }
        if (fechaInicio > fechaFin) {
            mostrarFeedbackCalendario("La fecha de inicio no puede ser mayor a la fecha fin", true);
            return;
        }

        guardarBtn.disabled = true;
        guardarBtn.textContent = "Guardando...";

        try {
            const payload = construirPayloadEdicion(detalleActual, { titulo, fechaInicio, fechaFin });
            await persistirEdicionEvento(evento.id, payload);
            cerrarModalAnimado(modal, content);
            calendar.refetchEvents();
            mostrarFeedbackCalendario("Evento modificado correctamente", false);
        } catch (error) {
            mostrarFeedbackCalendario(error.message || "No se pudo guardar el evento", true);
        } finally {
            guardarBtn.disabled = false;
            guardarBtn.textContent = "Guardar cambios";
        }
    };
}

function getIconByTipo(tipo) {
    const iconos = {
        ACTIVIDAD: "fa-star",
        CURSO: "fa-book",
        BINGO: "fa-dice",
        CAMPAMENTO: "fa-tent",
        CONSEJO: "fa-users"
    };
    return iconos[tipo] || "fa-calendar";
}

function getColorByTipo(tipo) {
    const colores = {
        ACTIVIDAD: "#3498db",
        CURSO: "#2ecc71",
        BINGO: "#f39c12",
        CAMPAMENTO: "#e74c3c",
        CONSEJO: "#8e44ad"
    };
    return colores[tipo] || "#546e7a";
}

function mapBackendEvent(evento) {
    const colorByAlcance = {
        GENERAL: "#1976d2",
        RAMA: "#2e7d32",
        EDUCADORES: "#8e44ad"
    };

    const tipo = evento.tipoEvento || "ACTIVIDAD";
    const tipoColor = getColorByTipo(tipo);
    
    // El backend expone solo formato de dominio
    const startDate = evento.fechaInicio;
    const endDate = evento.fechaFin;
    
    const mapped = {
        id: evento.id,
        title: `${evento.titulo}`,
        start: startDate,
        end: endDate,
        backgroundColor: colorByAlcance[evento.alcanceEvento] || "#546e7a",
        borderColor: colorByAlcance[evento.alcanceEvento] || "#546e7a",
        textColor: "#fff",
        extendedProps: {
            tipoEvento: tipo,
            tipoColor: tipoColor,
            tipoIcon: getIconByTipo(tipo),
            alcanceEvento: evento.alcanceEvento,
            ramaId: evento.ramaId,
            educadorResponsableId: evento.educadorResponsableId,
            beneficiarioACargoId: evento.beneficiarioACargoId,
            nivelCurso: evento.nivelCurso
        }
    };
    
    return mapped;
}

async function cargarRamas() {
    try {
        const response = await fetch('/api/ramas');
        if (!response.ok) {
            debugError("No se pudieron cargar ramas. HTTP:", response.status);
            return;
        }
        const data = await response.json();
        const ramas = Array.isArray(data.ramas) ? data.ramas : (Array.isArray(data) ? data : []);
        debugLog("Ramas cargadas:", ramas);
        
        const selectRamas = document.getElementById('selectRamas');
        selectRamas.innerHTML = "";
        ramas.forEach(rama => {
            const option = document.createElement('option');
            option.value = rama.id;
            option.textContent = rama.nombre;
            selectRamas.appendChild(option);
        });
    } catch (error) {
        debugError("Error al cargar ramas:", error);
    }
}

function obtenerFiltrosActivos() {
    const tiposSeleccionados = [];
    document.querySelectorAll('input[name="tipoEvento"]:checked').forEach(checkbox => {
        tiposSeleccionados.push(checkbox.value);
    });
    return tiposSeleccionados;
}

function obtenerAlcancesActivos() {
    const alcancesSeleccionados = [];
    document.querySelectorAll('input[name="alcanceEvento"]:checked').forEach(checkbox => {
        alcancesSeleccionados.push(checkbox.value);
    });
    return alcancesSeleccionados;
}

function obtenerRamaIdsSeleccionadas() {
    const selectRamas = document.getElementById('selectRamas');
    if (!selectRamas) {
        return [];
    }

    return Array.from(selectRamas.selectedOptions)
        .map(option => option.value)
        .filter(value => value !== "");
}

function mostrarDetalleEvento(evento, calendar) {
    const {tipoEvento, tipoIcon, tipoColor, alcanceEvento, ramaId, educadorResponsableId, beneficiarioACargoId, nivelCurso} = evento.extendedProps;
    
    const mapAlcance = {
        GENERAL: "General",
        RAMA: "Rama",
        EDUCADORES: "Educadores"
    };

    const detalles = `
        <div style="text-align: left; font-family: Arial, sans-serif;">
            <div style="margin-bottom: 12px; border-bottom: 2px solid ${tipoColor}; padding-bottom: 8px;">
                <strong style="font-size: 16px;">${evento.title}</strong>
                <div style="margin-top: 4px;">
                    <span style="display: inline-block; padding: 4px 8px; background-color: ${tipoColor}; color: white; border-radius: 4px; font-size: 12px; font-weight: 600;">
                        <i class="fas ${tipoIcon}"></i> ${tipoEvento}
                    </span>
                </div>
            </div>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 12px;">
                <div>
                    <strong style="color: #667eea;">Inicio:</strong><br>
                    <span style="font-size: 13px;">${evento.start}</span>
                </div>
                <div>
                    <strong style="color: #667eea;">Fin:</strong><br>
                    <span style="font-size: 13px;">${evento.end}</span>
                </div>
            </div>

            <div style="background-color: #f8f9fb; padding: 10px; border-radius: 6px; margin-bottom: 12px;">
                <strong style="color: #667eea;">Alcance:</strong>
                <div style="margin-top: 4px;">
                    <span style="display: inline-block; padding: 4px 8px; background-color: ${
                        alcanceEvento === 'GENERAL' ? '#1976d2' : 
                        alcanceEvento === 'RAMA' ? '#2e7d32' : 
                        '#8e44ad'
                    }; color: white; border-radius: 4px; font-size: 12px;">
                        ${mapAlcance[alcanceEvento] || alcanceEvento}
                    </span>
                </div>
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 13px;">
                <div>
                    <strong>ID Rama:</strong> ${ramaId || "—"}
                </div>
                <div>
                    <strong>Nivel Curso:</strong> ${nivelCurso || "—"}
                </div>
                <div>
                    <strong>Educador:</strong> ID ${educadorResponsableId}
                </div>
                <div>
                    <strong>Beneficiario:</strong> ID ${beneficiarioACargoId}
                </div>
            </div>
        </div>
    `;

    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
        padding: 20px;
    `;

    const content = document.createElement('div');
    content.style.cssText = `
        background-color: white;
        border-radius: 12px;
        padding: 24px;
        max-width: 500px;
        width: 100%;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        max-height: 80vh;
        overflow-y: auto;
    `;
    content.innerHTML = detalles;
    prepararAnimacionModal(modal, content);

    const actions = document.createElement("div");
    actions.style.cssText = "display:flex; gap:8px; margin-top: 20px;";

    const editBtn = document.createElement("button");
    editBtn.innerHTML = '<i class="fas fa-pen"></i> Editar';
    editBtn.style.cssText = `
        flex: 1;
        padding: 10px 16px;
        background-color: #2e7d32;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
        transition: background-color 0.3s ease;
    `;
    editBtn.onmouseover = () => editBtn.style.backgroundColor = "#256628";
    editBtn.onmouseout = () => editBtn.style.backgroundColor = "#2e7d32";
    editBtn.onclick = async () => {
        try {
            const detalleActual = await obtenerDetalleEvento(evento.id);
            cerrarModalAnimado(modal, content);
            abrirModalEdicionEvento(evento, calendar, detalleActual);
        } catch (error) {
            mostrarFeedbackCalendario(error.message || "No se pudo cargar el detalle del evento", true);
        }
    };

    const closeBtn = document.createElement("button");
    closeBtn.innerHTML = '<i class="fas fa-times"></i> Cerrar';
    closeBtn.style.cssText = `
        flex: 1;
        padding: 10px 16px;
        background-color: #667eea;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
        transition: background-color 0.3s ease;
    `;
    closeBtn.onmouseover = () => closeBtn.style.backgroundColor = '#5568d3';
    closeBtn.onmouseout = () => closeBtn.style.backgroundColor = '#667eea';
    closeBtn.onclick = () => cerrarModalAnimado(modal, content);

    actions.appendChild(editBtn);
    actions.appendChild(closeBtn);
    content.appendChild(actions);
    modal.appendChild(content);
    modal.onclick = (e) => {
        if (e.target === modal) {
            cerrarModalAnimado(modal, content);
        }
    };
    document.body.appendChild(modal);
    mostrarModalAnimado(modal, content);
}

document.addEventListener("DOMContentLoaded", function () {
    const calendarEl = document.getElementById("calendar");
    const papeleraEl = document.getElementById("papeleraEventos");
    const trackerPapelera = crearTrackerPapelera(papeleraEl);
    const personaIdDesdeQuery = new URLSearchParams(window.location.search).get("personaId");
    const ramaIdDesdeQuery = new URLSearchParams(window.location.search).get("ramaId");
    
    cargarRamas().then(() => {
        if (ramaIdDesdeQuery) {
            const selectRamas = document.getElementById('selectRamas');
            const option = Array.from(selectRamas.options).find(opt => opt.value === ramaIdDesdeQuery);
            if (option) {
                option.selected = true;
            }
        }
    });

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: "dayGridMonth",
        locale: "es",
        height: "auto",
        editable: true,
        eventStartEditable: true,
        eventDurationEditable: true,
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "dayGridMonth,timeGridWeek,timeGridDay"
        },
        events: async function (fetchInfo, successCallback, failureCallback) {
            try {
                const desde = toLocalDateTimeString(fetchInfo.start);
                const hasta = toLocalDateTimeString(fetchInfo.end);
                let url = `/api/eventos?desde=${encodeURIComponent(desde)}&hasta=${encodeURIComponent(hasta)}`;
                
                if (personaIdDesdeQuery) {
                    url += `&personaId=${encodeURIComponent(personaIdDesdeQuery)}`;
                }

                const ramaIdsSeleccionadas = obtenerRamaIdsSeleccionadas();
                ramaIdsSeleccionadas.forEach(ramaId => {
                    url += `&ramaIds=${encodeURIComponent(ramaId)}`;
                });

                const tiposSeleccionados = obtenerFiltrosActivos();
                tiposSeleccionados.forEach(tipo => {
                    url += `&tipoEvento=${encodeURIComponent(tipo)}`;
                });

                const alcancesSeleccionados = obtenerAlcancesActivos();
                alcancesSeleccionados.forEach(alcance => {
                    url += `&alcanceEvento=${encodeURIComponent(alcance)}`;
                });

                if (tiposSeleccionados.length === 0 || alcancesSeleccionados.length === 0) {
                    successCallback([]);
                    return;
                }

                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error(`Error HTTP ${response.status}`);
                }

                const data = await response.json();
                const eventos = Array.isArray(data.eventos) ? data.eventos.map(mapBackendEvent) : [];
                debugLog("Eventos obtenidos:", eventos.length);
                successCallback(eventos);
            } catch (error) {
                debugError("No se pudieron cargar los eventos", error);
                failureCallback(error);
            }
        },
        eventDrop: async function (info) {
            try {
                await persistirFechasEvento(info.event);
                mostrarFeedbackCalendario("Evento reprogramado correctamente", false);
            } catch (error) {
                info.revert();
                mostrarFeedbackCalendario(error.message || "No se pudo reprogramar el evento", true);
            }
        },
        eventDragStart: function () {
            marcarPapeleraActiva(papeleraEl, true, false);
            trackerPapelera.iniciar();
        },
        eventDragStop: async function (info) {
            const soltoEnPapelera = estaSobrePapelera(info.jsEvent, papeleraEl);
            trackerPapelera.detener();

            if (!soltoEnPapelera) {
                return;
            }

            const confirmar = await confirmarEliminacionEvento(info.event.title);
            if (!confirmar) {
                mostrarFeedbackCalendario("Eliminacion cancelada", true);
                return;
            }

            try {
                await eliminarEventoPorId(info.event.id);
                info.event.remove();
                mostrarFeedbackCalendario("Evento eliminado correctamente", false);
            } catch (error) {
                calendar.refetchEvents();
                mostrarFeedbackCalendario(error.message || "No se pudo eliminar el evento", true);
            }
        },
        eventResize: async function (info) {
            try {
                await persistirFechasEvento(info.event);
                mostrarFeedbackCalendario("Duracion de evento actualizada", false);
            } catch (error) {
                info.revert();
                mostrarFeedbackCalendario(error.message || "No se pudo actualizar la duracion", true);
            }
        },
        eventClick: function (info) {
            mostrarDetalleEvento(info.event, calendar);
        }
    });

    calendar.render();
    
    document.getElementById('selectRamas').addEventListener('change', function() {
        calendar.refetchEvents();
    });
    
    document.querySelectorAll('input[name="tipoEvento"], input[name="alcanceEvento"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            calendar.refetchEvents();
        });
    });
});

