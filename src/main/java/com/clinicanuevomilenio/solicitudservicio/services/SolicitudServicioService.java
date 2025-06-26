package com.clinicanuevomilenio.solicitudservicio.services;

import com.clinicanuevomilenio.solicitudservicio.dto.*;
import com.clinicanuevomilenio.solicitudservicio.mapper.SolicitudMapper;
import com.clinicanuevomilenio.solicitudservicio.models.*;
import com.clinicanuevomilenio.solicitudservicio.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolicitudServicioService {

    // Repositorios locales
    private final SolicitudServicioRepository solicitudRepository;
    private final PrioridadSolicitudRepository prioridadRepository;
    private final EstadoSolicitudRepository estadoRepository;
    private final TipoIncidenciaRepository tipoIncidenciaRepository;
    private final AsignacionServicioRepository asignacionServicioRepository;

    // Clientes para otras APIs
    private final UsuarioClientService usuarioClient;
    private final PabellonClientService pabellonClient;
    private final EquipamientoClientService equipamientoClient;

    // Mappers
    private final SolicitudMapper solicitudMapper;

    @Transactional
    public SolicitudServicioRespuestaDTO crearDesdeIncidencia(IncidenciaCreacionDTO dto, Integer usuarioId) {
        PabellonDTO pabellon = pabellonClient.obtenerPabellonPorId(dto.getPabellonId());
        if (pabellon == null) throw new EntityNotFoundException("Pabellón no encontrado con ID: " + dto.getPabellonId());

        UsuarioDTO solicitante = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (solicitante == null) throw new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId);

        EquipamientoDTO equipamiento = null;
        if (dto.getEquipamientoId() != null) {
            equipamiento = equipamientoClient.obtenerEquipamientoPorId(dto.getEquipamientoId());
        }

        TipoIncidencia tipoIncidencia = tipoIncidenciaRepository.findById(dto.getTipoIncidenciaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de incidencia no encontrado con ID: " + dto.getTipoIncidenciaId()));

        PrioridadSolicitud prioridad = prioridadRepository.findById(2) // Asumimos Prioridad "Alta"
                .orElseThrow(() -> new EntityNotFoundException("Prioridad 'Alta' no configurada."));
        EstadoSolicitud estadoInicial = estadoRepository.findById(1) // Asumimos Estado "Pendiente"
                .orElseThrow(() -> new EntityNotFoundException("Estado 'Pendiente' no configurado."));

        SolicitudServicio nuevaSolicitud = new SolicitudServicio();
        nuevaSolicitud.setTipo("Reporte de Incidencia");
        nuevaSolicitud.setDescripcion("Incidencia reportada: " + tipoIncidencia.getNombre() + ". Detalles: " + dto.getDescripcion());
        nuevaSolicitud.setFechaHrSolicitud(LocalDateTime.now());
        nuevaSolicitud.setFechaHrRequerida(LocalDateTime.now().plusHours(1));
        nuevaSolicitud.setPrioridad(prioridad);
        nuevaSolicitud.setEstado(estadoInicial);
        nuevaSolicitud.setPabellonId(dto.getPabellonId());
        nuevaSolicitud.setUsuarioSolicitanteId(usuarioId);
        nuevaSolicitud.setEquipamientoId(dto.getEquipamientoId());

        RegistroIncidencia nuevaIncidencia = new RegistroIncidencia();
        nuevaIncidencia.setDescripcion(dto.getDescripcion());
        nuevaIncidencia.setFechaHrReporte(LocalDateTime.now());
        nuevaIncidencia.setTipoIncidencia(tipoIncidencia);
        nuevaIncidencia.setSolicitudServicio(nuevaSolicitud);

        nuevaSolicitud.setIncidencias(List.of(nuevaIncidencia));

        SolicitudServicio guardada = solicitudRepository.save(nuevaSolicitud);

        return solicitudMapper.toResponseDTO(guardada, solicitante, pabellon, equipamiento, null);
    }

    @Transactional
    public SolicitudServicioRespuestaDTO crearSolicitud(SolicitudServicioCreacionDTO dto, Integer usuarioSolicitanteId) {
        PabellonDTO pabellon = pabellonClient.obtenerPabellonPorId(dto.getPabellonId());
        if (pabellon == null) throw new EntityNotFoundException("Pabellón no encontrado con ID: " + dto.getPabellonId());

        UsuarioDTO solicitante = usuarioClient.obtenerUsuarioPorId(usuarioSolicitanteId);
        if (solicitante == null) {
            throw new EntityNotFoundException("Usuario solicitante no encontrado con ID: " + usuarioSolicitanteId);
        }

        EquipamientoDTO equipamiento = null;
        if (dto.getEquipamientoId() != null) {
            equipamiento = equipamientoClient.obtenerEquipamientoPorId(dto.getEquipamientoId());
            if (equipamiento == null) throw new EntityNotFoundException("Equipamiento no encontrado con ID: " + dto.getEquipamientoId());
        }

        PrioridadSolicitud prioridad = prioridadRepository.findById(dto.getPrioridadId())
                .orElseThrow(() -> new EntityNotFoundException("Prioridad no encontrada con ID: " + dto.getPrioridadId()));

        EstadoSolicitud estadoInicial = estadoRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Estado 'Pendiente' no configurado en la BD."));

        SolicitudServicio nuevaSolicitud = new SolicitudServicio();
        nuevaSolicitud.setTipo(dto.getTipo());
        nuevaSolicitud.setDescripcion(dto.getDescripcion());
        nuevaSolicitud.setFechaHrSolicitud(LocalDateTime.now());
        nuevaSolicitud.setFechaHrRequerida(dto.getFechaHrRequerida());
        nuevaSolicitud.setComentarios(dto.getComentarios());
        nuevaSolicitud.setPrioridad(prioridad);
        nuevaSolicitud.setEstado(estadoInicial);
        nuevaSolicitud.setPabellonId(pabellon.getId());
        nuevaSolicitud.setUsuarioSolicitanteId(solicitante.getIdUsuario());
        nuevaSolicitud.setEquipamientoId(equipamiento != null ? equipamiento.getId() : null);

        SolicitudServicio guardada = solicitudRepository.save(nuevaSolicitud);

        return solicitudMapper.toResponseDTO(guardada, solicitante, pabellon, equipamiento, null);
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioRespuestaDTO> listarSolicitudes() {
        List<SolicitudServicio> solicitudes = solicitudRepository.findAll();
        return enriquecerListaSolicitudes(solicitudes);
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioRespuestaDTO> listarSolicitudesPendientes() {
        List<SolicitudServicio> solicitudes = solicitudRepository.findByEstado_IdNotIn(List.of(4, 5));
        return enriquecerListaSolicitudes(solicitudes);
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarPersonalPorRol(String rol) {
        return usuarioClient.obtenerUsuariosPorRol(rol);
    }

    @Transactional(readOnly = true)
    public SolicitudServicioRespuestaDTO buscarSolicitudPorId(Integer id) {
        SolicitudServicio solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id));

        PabellonDTO pabellon = pabellonClient.obtenerPabellonPorId(solicitud.getPabellonId());
        UsuarioDTO solicitante = usuarioClient.obtenerUsuarioPorId(solicitud.getUsuarioSolicitanteId());

        EquipamientoDTO equipamiento = null;
        if (solicitud.getEquipamientoId() != null) {
            equipamiento = equipamientoClient.obtenerEquipamientoPorId(solicitud.getEquipamientoId());
        }

        AsignacionServicioDTO asignacion = null;
        if (solicitud.getAsignacion() != null) {
            UsuarioDTO usuarioAsignado = usuarioClient.obtenerUsuarioPorId(solicitud.getAsignacion().getUsuarioAsignadoId());
            asignacion = solicitudMapper.toAsignacionDTO(solicitud.getAsignacion(), usuarioAsignado);
        }

        return solicitudMapper.toResponseDTO(solicitud, solicitante, pabellon, equipamiento, asignacion);
    }

    @Transactional
    public SolicitudServicioRespuestaDTO asignarTarea(AsignacionRequestDTO dto) {
        SolicitudServicio solicitud = solicitudRepository.findById(dto.getSolicitudId())
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + dto.getSolicitudId()));

        if (solicitud.getAsignacion() != null) {
            throw new IllegalStateException("Esta solicitud ya ha sido asignada.");
        }

        UsuarioDTO usuarioAsignado = usuarioClient.obtenerUsuarioPorId(dto.getUsuarioAsignadoId());
        if (usuarioAsignado == null) {
            throw new EntityNotFoundException("El usuario a asignar no existe con ID: " + dto.getUsuarioAsignadoId());
        }

        AsignacionServicio nuevaAsignacion = new AsignacionServicio();
        nuevaAsignacion.setUsuarioAsignadoId(usuarioAsignado.getIdUsuario());
        nuevaAsignacion.setFechaAsignacion(LocalDate.now());
        nuevaAsignacion.setEstado("Asignada");

        EstadoSolicitud estadoAsignado = estadoRepository.findById(2) // Asumimos que 2 = "Asignada"
                .orElseThrow(() -> new EntityNotFoundException("Estado 'Asignada' no configurado en la BD."));

        solicitud.setEstado(estadoAsignado);
        solicitud.setAsignacion(nuevaAsignacion);

        SolicitudServicio solicitudActualizada = solicitudRepository.save(solicitud);
        return buscarSolicitudPorId(solicitudActualizada.getId());
    }

    private List<SolicitudServicioRespuestaDTO> enriquecerListaSolicitudes(List<SolicitudServicio> solicitudes) {
        if (solicitudes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> pabellonIds = solicitudes.stream().map(SolicitudServicio::getPabellonId).distinct().toList();
        List<Integer> solicitanteIds = solicitudes.stream().map(SolicitudServicio::getUsuarioSolicitanteId).distinct().toList();
        List<Integer> equipamientoIds = solicitudes.stream().map(SolicitudServicio::getEquipamientoId).filter(id -> id != null).distinct().toList();
        List<Integer> usuarioAsignadoIds = solicitudes.stream()
                .filter(sol -> sol.getAsignacion() != null)
                .map(sol -> sol.getAsignacion().getUsuarioAsignadoId())
                .distinct()
                .toList();

        Map<Integer, PabellonDTO> pabellonesMap = pabellonClient.obtenerPabellonesPorIds(pabellonIds).stream().collect(Collectors.toMap(PabellonDTO::getId, Function.identity()));
        Map<Integer, UsuarioDTO> usuariosMap = usuarioClient.obtenerUsuariosPorIds(solicitanteIds).stream().collect(Collectors.toMap(UsuarioDTO::getIdUsuario, Function.identity()));
        Map<Integer, EquipamientoDTO> equipamientosMap = equipamientoClient.obtenerEquipamientosPorIds(equipamientoIds).stream().collect(Collectors.toMap(EquipamientoDTO::getId, Function.identity()));
        Map<Integer, UsuarioDTO> usuariosAsignadosMap = usuarioClient.obtenerUsuariosPorIds(usuarioAsignadoIds).stream().collect(Collectors.toMap(UsuarioDTO::getIdUsuario, Function.identity()));

        return solicitudes.stream()
                .map(solicitud -> {
                    PabellonDTO pabellon = pabellonesMap.get(solicitud.getPabellonId());
                    UsuarioDTO solicitante = usuariosMap.get(solicitud.getUsuarioSolicitanteId());
                    EquipamientoDTO equipamiento = equipamientosMap.get(solicitud.getEquipamientoId());
                    AsignacionServicioDTO asignacionDTO = null;
                    if (solicitud.getAsignacion() != null) {
                        UsuarioDTO usuarioAsignado = usuariosAsignadosMap.get(solicitud.getAsignacion().getUsuarioAsignadoId());
                        asignacionDTO = solicitudMapper.toAsignacionDTO(solicitud.getAsignacion(), usuarioAsignado);
                    }
                    return solicitudMapper.toResponseDTO(solicitud, solicitante, pabellon, equipamiento, asignacionDTO);
                })
                .collect(Collectors.toList());
    }
}