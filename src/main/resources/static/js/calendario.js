function toLocalDateTimeString(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}

function getIconByTipo(tipo) {
    const iconos = {
        ACTIVIDAD: "fa-star",
        CURSO: "fa-book",
        BINGO: "fa-dice",
        CAMPAMENTO: "fa-tent"
    };
    return iconos[tipo] || "fa-calendar";
}

function getColorByTipo(tipo) {
    const colores = {
        ACTIVIDAD: "#3498db",
        CURSO: "#2ecc71",
        BINGO: "#f39c12",
        CAMPAMENTO: "#e74c3c"
    };
    return colores[tipo] || "#546e7a";
}

function mapBackendEvent(evento) {
    const colorByAlcance = {
        GENERAL: "#1976d2",
        RAMA: "#2e7d32",
        INDIVIDUAL: "#ef6c00"
    };

    const tipo = evento.tipoEvento || "ACTIVIDAD";
    const tipoColor = getColorByTipo(tipo);
    
    // Usar start/end si existen, si no usar fechaInicio/fechaFin
    const startDate = evento.start || evento.fechaInicio;
    const endDate = evento.end || evento.fechaFin;
    
    console.log(`  📅 Evento: "${evento.titulo}" - Tipo: ${tipo} - Start: ${startDate} - End: ${endDate}`);

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
    
    console.log(`  ✅ Mapeado:`, mapped);
    return mapped;
}

async function cargarRamas() {
    try {
        console.log("📍 Cargando ramas desde /api/ramas...");
        const response = await fetch('/api/ramas');
        if (!response.ok) {
            console.error("❌ Error al cargar ramas:", response.status);
            return;
        }
        const data = await response.json();
        const ramas = Array.isArray(data.ramas) ? data.ramas : (Array.isArray(data) ? data : []);
        console.log("✅ Ramas cargadas:", ramas);
        
        const selectRama = document.getElementById('selectRama');
        ramas.forEach(rama => {
            const option = document.createElement('option');
            option.value = rama.id;
            option.textContent = rama.nombre;
            selectRama.appendChild(option);
        });
        console.log(`✅ ${ramas.length} ramas agregadas al dropdown`);
    } catch (error) {
        console.error("❌ Error al cargar ramas:", error);
    }
}

function obtenerFiltrosActivos() {
    const tiposSeleccionados = [];
    document.querySelectorAll('.checkbox-item input[type="checkbox"]:checked').forEach(checkbox => {
        tiposSeleccionados.push(checkbox.value);
    });
    return tiposSeleccionados;
}

function mostrarDetalleEvento(evento) {
    const {tipoEvento, tipoIcon, tipoColor, alcanceEvento, ramaId, educadorResponsableId, beneficiarioACargoId, nivelCurso} = evento.extendedProps;
    
    const mapAlcance = {
        GENERAL: "General",
        RAMA: "Rama",
        INDIVIDUAL: "Individual"
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
                        '#ef6c00'
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

    const closeBtn = document.createElement('button');
    closeBtn.innerHTML = '<i class="fas fa-times"></i> Cerrar';
    closeBtn.style.cssText = `
        display: block;
        margin-top: 20px;
        padding: 10px 16px;
        background-color: #667eea;
        color: white;
        border: none;
        border-radius: 6px;
        cursor: pointer;
        font-weight: 600;
        transition: background-color 0.3s ease;
        width: 100%;
    `;
    closeBtn.onmouseover = () => closeBtn.style.backgroundColor = '#5568d3';
    closeBtn.onmouseout = () => closeBtn.style.backgroundColor = '#667eea';
    closeBtn.onclick = () => modal.remove();

    content.appendChild(closeBtn);
    modal.appendChild(content);
    modal.onclick = (e) => {
        if (e.target === modal) modal.remove();
    };
    document.body.appendChild(modal);
}

document.addEventListener("DOMContentLoaded", function () {
    const calendarEl = document.getElementById("calendar");
    const personaIdDesdeQuery = new URLSearchParams(window.location.search).get("personaId");
    const ramaIdDesdeQuery = new URLSearchParams(window.location.search).get("ramaId");
    
    // Cargar ramas
    cargarRamas();
    
    // Establecer rama desde query string si existe
    if (ramaIdDesdeQuery) {
        document.getElementById('selectRama').value = ramaIdDesdeQuery;
    }

    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: "dayGridMonth",
        locale: "es",
        height: "auto",
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "dayGridMonth,timeGridWeek,timeGridDay"
        },
        events: async function (fetchInfo, successCallback, failureCallback) {
            try {
                const desde = toLocalDateTimeString(fetchInfo.start);
                const hasta = toLocalDateTimeString(fetchInfo.end);
                let url = `/api/eventos/rango?desde=${encodeURIComponent(desde)}&hasta=${encodeURIComponent(hasta)}`;
                
                if (personaIdDesdeQuery) {
                    url += `&personaId=${encodeURIComponent(personaIdDesdeQuery)}`;
                }
                
                const ramaIdSeleccionada = document.getElementById('selectRama').value;
                if (ramaIdSeleccionada) {
                    url += `&ramaId=${encodeURIComponent(ramaIdSeleccionada)}`;
                }
                
                const tiposSeleccionados = obtenerFiltrosActivos();
                if (tiposSeleccionados.length === 1) {
                    url += `&tipoEvento=${encodeURIComponent(tiposSeleccionados[0])}`;
                } else if (tiposSeleccionados.length > 1) {
                    tiposSeleccionados.forEach(tipo => {
                        url += `&tipoEvento=${encodeURIComponent(tipo)}`;
                    });
                }

                console.log("🔍 Cargando eventos desde:", url);
                const response = await fetch(url);
                if (!response.ok) {
                    throw new Error(`Error HTTP ${response.status}`);
                }

                const data = await response.json();
                console.log("📥 Respuesta de API:", data);
                const eventos = Array.isArray(data.eventos) ? data.eventos.map(mapBackendEvent) : [];
                console.log("✅ Eventos mapeados:", eventos);
                successCallback(eventos);
            } catch (error) {
                console.error("❌ No se pudieron cargar los eventos", error);
                failureCallback(error);
            }
        },
        eventClick: function (info) {
            mostrarDetalleEvento(info.event);
        }
    });

    calendar.render();
    
    // Listeners para filtros
    document.getElementById('selectRama').addEventListener('change', function() {
        calendar.refetchEvents();
    });
    
    document.querySelectorAll('.checkbox-item input[type="checkbox"]').forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            calendar.refetchEvents();
        });
    });
});

