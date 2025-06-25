package com.clinicanuevomilenio.solicitudservicio.dto;

import com.clinicanuevomilenio.solicitudservicio.dto.EquipamientoDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.PabellonDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.UsuarioDTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudServicioRespuestaDTO {
    private Integer id;
    private String tipo;
    private String descripcion;
    private LocalDateTime fechaHrSolicitud;
    private LocalDateTime fechaHrRequerida;
    private LocalDateTime fechaHrCompletado;
    private String comentarios;

    // --- Datos Anidados y Enriquecidos ---
    private PrioridadSolicitudDTO prioridad;
    private EstadoSolicitudDTO estado;
    private UsuarioDTO usuarioSolicitante; // Obtenido de usuarios-api
    private PabellonDTO pabellon;           // Obtenido de pabellones-api
    private EquipamientoDTO equipamiento; // Obtenido de equipamiento-api (puede ser nulo)
    private AsignacionServicioDTO asignacion; // Puede ser nulo si aún no se ha asignado
}