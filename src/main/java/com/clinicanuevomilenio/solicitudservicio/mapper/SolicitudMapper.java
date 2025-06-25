package com.clinicanuevomilenio.solicitudservicio.mapper;

import com.clinicanuevomilenio.solicitudservicio.dto.*;
import com.clinicanuevomilenio.solicitudservicio.dto.EquipamientoDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.PabellonDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.UsuarioDTO;
import com.clinicanuevomilenio.solicitudservicio.models.AsignacionServicio;
import com.clinicanuevomilenio.solicitudservicio.models.SolicitudServicio;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    /**
     * Convierte una entidad SolicitudServicio y los DTOs externos en el DTO de respuesta final.
     */
    public SolicitudServicioRespuestaDTO toResponseDTO(
            SolicitudServicio solicitud,
            UsuarioDTO solicitante,
            PabellonDTO pabellon,
            EquipamientoDTO equipamiento,
            AsignacionServicioDTO asignacion) {

        if (solicitud == null) {
            return null;
        }

        // Mapeo de Prioridad
        PrioridadSolicitudDTO prioridadDTO = new PrioridadSolicitudDTO();
        prioridadDTO.setId(solicitud.getPrioridad().getId());
        prioridadDTO.setNombre(solicitud.getPrioridad().getNombre());
        prioridadDTO.setNivel(solicitud.getPrioridad().getNivel());

        // Mapeo de Estado
        EstadoSolicitudDTO estadoDTO = new EstadoSolicitudDTO();
        estadoDTO.setId(solicitud.getEstado().getId());
        estadoDTO.setNombre(solicitud.getEstado().getNombre());

        return SolicitudServicioRespuestaDTO.builder()
                .id(solicitud.getId())
                .tipo(solicitud.getTipo())
                .descripcion(solicitud.getDescripcion())
                .fechaHrSolicitud(solicitud.getFechaHrSolicitud())
                .fechaHrRequerida(solicitud.getFechaHrRequerida())
                .fechaHrCompletado(solicitud.getFechaHrCompletado())
                .comentarios(solicitud.getComentarios())
                .prioridad(prioridadDTO)
                .estado(estadoDTO)
                .usuarioSolicitante(solicitante)
                .pabellon(pabellon)
                .equipamiento(equipamiento)
                .asignacion(asignacion)
                .build();
    }

    // --- MÉTODO FALTANTE AÑADIDO ---
    /**
     * Convierte una entidad AsignacionServicio y un UsuarioDTO en el DTO de respuesta de la asignación.
     * @param asignacion La entidad de la base de datos.
     * @param usuarioAsignado El DTO del usuario obtenido de la usuarios-api.
     * @return El DTO de asignación enriquecido.
     */
    public AsignacionServicioDTO toAsignacionDTO(AsignacionServicio asignacion, UsuarioDTO usuarioAsignado) {
        if (asignacion == null) {
            return null;
        }

        return AsignacionServicioDTO.builder()
                .id(asignacion.getId())
                .fechaAsignacion(asignacion.getFechaAsignacion())
                .fechaFinalizacion(asignacion.getFechaFinalizacion())
                .estado(asignacion.getEstado())
                .usuarioAsignado(usuarioAsignado) // Anidamos el DTO del usuario
                .build();
    }
}