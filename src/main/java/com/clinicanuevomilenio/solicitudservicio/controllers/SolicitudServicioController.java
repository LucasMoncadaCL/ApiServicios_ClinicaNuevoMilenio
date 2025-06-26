package com.clinicanuevomilenio.solicitudservicio.controllers;

import com.clinicanuevomilenio.solicitudservicio.dto.AsignacionRequestDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.IncidenciaCreacionDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.SolicitudServicioCreacionDTO;
import com.clinicanuevomilenio.solicitudservicio.dto.SolicitudServicioRespuestaDTO;
import com.clinicanuevomilenio.solicitudservicio.services.SolicitudServicioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes-servicio")
@RequiredArgsConstructor
public class SolicitudServicioController {

    private final SolicitudServicioService solicitudService; //

    /**
     * Crea una nueva solicitud de servicio (mantenimiento, aseo, etc.).
     * La autenticación y autorización (usuario logueado) son manejadas por el API Gateway.
     */
    @PostMapping
    // @PreAuthorize("isAuthenticated()") // ELIMINADO: Seguridad delegada al API Gateway
    public ResponseEntity<?> crearSolicitud(
            @RequestBody SolicitudServicioCreacionDTO dto,
            @RequestHeader("X-User-Id") Integer usuarioSolicitanteId) { //
        try {
            SolicitudServicioRespuestaDTO solicitudCreada = solicitudService.crearSolicitud(dto, usuarioSolicitanteId); //
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada); //
        } catch (EntityNotFoundException e) { //
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage())); //
        }
    }

    /**
     * Lista todas las solicitudes de servicio.
     * La autorización (solo administradores) es manejada por el API Gateway.
     */
    @GetMapping
    // @PreAuthorize("hasRole('ADMINISTRATIVO')") // ELIMINADO: Seguridad delegada al API Gateway
    public ResponseEntity<List<SolicitudServicioRespuestaDTO>> listarSolicitudes() { //
        return ResponseEntity.ok(solicitudService.listarSolicitudes()); //
    }

    /**
     * Obtiene una solicitud de servicio específica por su ID.
     * La autenticación (usuario logueado) es manejada por el API Gateway.
     */
    @GetMapping("/{id}")
    // @PreAuthorize("isAuthenticated()") // ELIMINADO: Seguridad delegada al API Gateway
    public ResponseEntity<?> obtenerSolicitudPorId(@PathVariable Integer id) { //
        try {
            return ResponseEntity.ok(solicitudService.buscarSolicitudPorId(id)); //
        } catch (EntityNotFoundException e) { //
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage())); //
        }
    }

    /**
     * Asigna una solicitud de servicio a un miembro del personal.
     * La autorización (solo administradores) es manejada por el API Gateway.
     */
    @PostMapping("/asignar")
    // @PreAuthorize("hasRole('ADMINISTRATIVO')") // ELIMINADO: Seguridad delegada al API Gateway
    public ResponseEntity<?> asignarTarea(@RequestBody AsignacionRequestDTO dto) { //
        try {
            SolicitudServicioRespuestaDTO solicitudAsignada = solicitudService.asignarTarea(dto); //
            return ResponseEntity.ok(solicitudAsignada); //
        } catch (EntityNotFoundException | IllegalStateException e) { //
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage())); //
        }
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<String>> listarTiposDeServicio() {
        List<String> tipos = List.of(
                "Limpieza",
                "Mantenimiento de Equipo",
                "Reparación de Infraestructura",
                "Reposición de Suministros",
                "Soporte Técnico"
        );
        return ResponseEntity.ok(tipos);
    }

    @PostMapping("/incidencias")
    public ResponseEntity<?> reportarIncidencia(
            @RequestBody IncidenciaCreacionDTO dto,
            @RequestHeader("X-User-Id") Integer usuarioId) {
        try {
            SolicitudServicioRespuestaDTO solicitudCreada = solicitudService.crearDesdeIncidencia(dto, usuarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}