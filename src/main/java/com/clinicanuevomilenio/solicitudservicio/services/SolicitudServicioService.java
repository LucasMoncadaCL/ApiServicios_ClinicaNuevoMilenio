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
    private final SolicitudServicioRepository solicitudRepository; //
    private final PrioridadSolicitudRepository prioridadRepository; //
    private final EstadoSolicitudRepository estadoRepository; //
    private final AsignacionServicioRepository asignacionServicioRepository; // Añadir este si se necesita guardar AsignacionServicio independientemente de SolicitudServicio

    // Clientes para otras APIs
    private final UsuarioClientService usuarioClient; //
    private final PabellonClientService pabellonClient; //
    private final EquipamientoClientService equipamientoClient; //

    // Mappers
    private final SolicitudMapper solicitudMapper; //

    private final TipoIncidenciaRepository tipoIncidenciaRepository;

    @Transactional
    public SolicitudServicioRespuestaDTO crearDesdeIncidencia(IncidenciaCreacionDTO dto, Integer usuarioId) {
        // 1. Validar entidades externas (pabellón, equipo, usuario)
        PabellonDTO pabellon = pabellonClient.obtenerPabellonPorId(dto.getPabellonId());
        if (pabellon == null) throw new EntityNotFoundException("Pabellón no encontrado con ID: " + dto.getPabellonId());

        UsuarioDTO solicitante = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (solicitante == null) throw new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId);

        EquipamientoDTO equipamiento = null;
        if (dto.getEquipamientoId() != null) {
            equipamiento = equipamientoClient.obtenerEquipamientoPorId(dto.getEquipamientoId());
        }

        // 2. Validar entidades locales
        TipoIncidencia tipoIncidencia = tipoIncidenciaRepository.findById(dto.getTipoIncidenciaId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de incidencia no encontrado con ID: " + dto.getTipoIncidenciaId()));

        // Asumimos que una incidencia tiene prioridad alta (ID 2) y estado inicial pendiente (ID 1)
        PrioridadSolicitud prioridad = prioridadRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Prioridad 'Alta' no configurada."));
        EstadoSolicitud estadoInicial = estadoRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Estado 'Pendiente' no configurado."));

        // 3. Crear la Solicitud de Servicio
        SolicitudServicio nuevaSolicitud = new SolicitudServicio();
        nuevaSolicitud.setTipo("Reporte de Incidencia"); // Tipo de servicio genérico
        nuevaSolicitud.setDescripcion("Incidencia reportada: " + tipoIncidencia.getNombre() + ". Detalles: " + dto.getDescripcion());
        nuevaSolicitud.setFechaHrSolicitud(LocalDateTime.now());
        nuevaSolicitud.setFechaHrRequerida(LocalDateTime.now().plusHours(1)); // Requerido en 1 hora por defecto
        nuevaSolicitud.setPrioridad(prioridad);
        nuevaSolicitud.setEstado(estadoInicial);
        nuevaSolicitud.setPabellonId(dto.getPabellonId());
        nuevaSolicitud.setUsuarioSolicitanteId(usuarioId);
        nuevaSolicitud.setEquipamientoId(dto.getEquipamientoId());

        // 4. Crear el Registro de Incidencia
        RegistroIncidencia nuevaIncidencia = new RegistroIncidencia();
        nuevaIncidencia.setDescripcion(dto.getDescripcion());
        nuevaIncidencia.setFechaHrReporte(LocalDateTime.now());
        nuevaIncidencia.setTipoIncidencia(tipoIncidencia);
        nuevaIncidencia.setSolicitudServicio(nuevaSolicitud); // Vinculamos la incidencia a la solicitud

        // Añadimos la incidencia a la lista de la solicitud
        nuevaSolicitud.setIncidencias(List.of(nuevaIncidencia));

        // 5. Guardar la solicitud (gracias a CascadeType.ALL, la incidencia se guardará con ella)
        SolicitudServicio guardada = solicitudRepository.save(nuevaSolicitud);

        // 6. Devolver la respuesta
        return solicitudMapper.toResponseDTO(guardada, solicitante, pabellon, equipamiento, null);
    }

    @Transactional
    public SolicitudServicioRespuestaDTO crearSolicitud(SolicitudServicioCreacionDTO dto, Integer usuarioSolicitanteId) { //
        // 1. Validar entidades externas
        PabellonDTO pabellon = pabellonClient.obtenerPabellonPorId(dto.getPabellonId()); //
        if (pabellon == null) throw new EntityNotFoundException("Pabellón no encontrado con ID: " + dto.getPabellonId()); //

        // El usuarioSolicitanteId ya viene del API Gateway, que lo ha autenticado.
        // Aquí solo lo buscamos para enriquecer la respuesta, no para autenticar.
        UsuarioDTO solicitante = usuarioClient.obtenerUsuarioPorId(usuarioSolicitanteId); //
        if (solicitante == null) {
            // Esto podría indicar un problema de sincronización de IDs o un ID inválido enviado por el Gateway.
            // Es una validación de existencia, no de autenticación.
            throw new EntityNotFoundException("Usuario solicitante no encontrado con ID: " + usuarioSolicitanteId + ". Contacte a un administrador.");
        }

        EquipamientoDTO equipamiento = null; //
        if (dto.getEquipamientoId() != null) { //
            equipamiento = equipamientoClient.obtenerEquipamientoPorId(dto.getEquipamientoId()); //
            if (equipamiento == null) throw new EntityNotFoundException("Equipamiento no encontrado con ID: " + dto.getEquipamientoId()); //
        }

        // 2. Validar entidades locales
        PrioridadSolicitud prioridad = prioridadRepository.findById(dto.getPrioridadId()) //
                .orElseThrow(() -> new EntityNotFoundException("Prioridad no encontrada con ID: " + dto.getPrioridadId())); //

        EstadoSolicitud estadoInicial = estadoRepository.findById(1) // Asumimos que 1 = "Pendiente"
                .orElseThrow(() -> new EntityNotFoundException("Estado 'Pendiente' no configurado en la BD. Por favor, contacte a un administrador.")); //

        // 3. Crear y guardar la entidad
        SolicitudServicio nuevaSolicitud = new SolicitudServicio(); //
        nuevaSolicitud.setTipo(dto.getTipo()); //
        nuevaSolicitud.setDescripcion(dto.getDescripcion()); //
        nuevaSolicitud.setFechaHrSolicitud(LocalDateTime.now()); //
        nuevaSolicitud.setFechaHrRequerida(dto.getFechaHrRequerida()); //
        nuevaSolicitud.setComentarios(dto.getComentarios()); //
        nuevaSolicitud.setPrioridad(prioridad); //
        nuevaSolicitud.setEstado(estadoInicial); //
        nuevaSolicitud.setPabellonId(pabellon.getId()); //
        nuevaSolicitud.setUsuarioSolicitanteId(solicitante.getIdUsuario()); //
        nuevaSolicitud.setEquipamientoId(equipamiento != null ? equipamiento.getId() : null); //

        SolicitudServicio guardada = solicitudRepository.save(nuevaSolicitud); //

        // 4. Construir y devolver la respuesta enriquecida
        return solicitudMapper.toResponseDTO(guardada, solicitante, pabellon, equipamiento, null); //
    }

    @Transactional(readOnly = true)
    public List<SolicitudServicioRespuestaDTO> listarSolicitudes() { //
        List<SolicitudServicio> solicitudes = solicitudRepository.findAll(); //
        if (solicitudes.isEmpty()) return Collections.emptyList(); //

        // --- Optimización N+1 para todas las solicitudes ---
        List<Integer> pabellonIds = solicitudes.stream().map(SolicitudServicio::getPabellonId).distinct().toList(); //
        List<Integer> solicitanteIds = solicitudes.stream().map(SolicitudServicio::getUsuarioSolicitanteId).distinct().toList(); //
        List<Integer> equipamientoIds = solicitudes.stream().map(SolicitudServicio::getEquipamientoId).filter(id -> id != null).distinct().toList(); //

        // Recopilar IDs de usuarios asignados para la optimización N+1
        List<Integer> usuarioAsignadoIds = solicitudes.stream()
                .filter(sol -> sol.getAsignacion() != null)
                .map(sol -> sol.getAsignacion().getUsuarioAsignadoId())
                .distinct()
                .toList();

        Map<Integer, PabellonDTO> pabellonesMap = pabellonClient.obtenerPabellonesPorIds(pabellonIds).stream().collect(Collectors.toMap(PabellonDTO::getId, Function.identity())); //
        Map<Integer, UsuarioDTO> solicitantesMap = usuarioClient.obtenerUsuariosPorIds(solicitanteIds).stream().collect(Collectors.toMap(UsuarioDTO::getIdUsuario, Function.identity())); //
        Map<Integer, EquipamientoDTO> equipamientosMap = equipamientoClient.obtenerEquipamientosPorIds(equipamientoIds).stream().collect(Collectors.toMap(EquipamientoDTO::getId, Function.identity())); //

        // Obtener todos los usuarios asignados en una sola llamada
        Map<Integer, UsuarioDTO> usuariosAsignadosMap = usuarioClient.obtenerUsuariosPorIds(usuarioAsignadoIds).stream().collect(Collectors.toMap(UsuarioDTO::getIdUsuario, Function.identity()));

        return solicitudes.stream() //
                .map(solicitud -> {
                    PabellonDTO pabellon = pabellonesMap.get(solicitud.getPabellonId()); //
                    UsuarioDTO solicitante = solicitantesMap.get(solicitud.getUsuarioSolicitanteId()); //
                    EquipamientoDTO equipamiento = equipamientosMap.get(solicitud.getEquipamientoId()); //

                    AsignacionServicioDTO asignacionDTO = null;
                    if (solicitud.getAsignacion() != null) {
                        UsuarioDTO usuarioAsignado = usuariosAsignadosMap.get(solicitud.getAsignacion().getUsuarioAsignadoId());
                        asignacionDTO = solicitudMapper.toAsignacionDTO(solicitud.getAsignacion(), usuarioAsignado);
                    }

                    return solicitudMapper.toResponseDTO(solicitud, solicitante, pabellon, equipamiento, asignacionDTO); //
                })
                .collect(Collectors.toList()); //
    }

    @Transactional(readOnly = true)
    public SolicitudServicioRespuestaDTO buscarSolicitudPorId(Integer id) { //
        SolicitudServicio solicitud = solicitudRepository.findById(id) //
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + id)); //

        // --- Orquestación para enriquecer la respuesta ---
        PabellonDTO pabellon = pabellonClient.obtenerPabellonPorId(solicitud.getPabellonId()); //
        UsuarioDTO solicitante = usuarioClient.obtenerUsuarioPorId(solicitud.getUsuarioSolicitanteId()); //

        EquipamientoDTO equipamiento = null; //
        if (solicitud.getEquipamientoId() != null) { //
            equipamiento = equipamientoClient.obtenerEquipamientoPorId(solicitud.getEquipamientoId()); //
        }

        AsignacionServicioDTO asignacion = null; //
        if (solicitud.getAsignacion() != null) { //
            UsuarioDTO usuarioAsignado = usuarioClient.obtenerUsuarioPorId(solicitud.getAsignacion().getUsuarioAsignadoId()); //
            asignacion = solicitudMapper.toAsignacionDTO(solicitud.getAsignacion(), usuarioAsignado); //
        }

        return solicitudMapper.toResponseDTO(solicitud, solicitante, pabellon, equipamiento, asignacion); //
    }

    @Transactional
    public SolicitudServicioRespuestaDTO asignarTarea(AsignacionRequestDTO dto) { //
        // 1. Validar que la solicitud exista y no esté ya asignada o completada
        SolicitudServicio solicitud = solicitudRepository.findById(dto.getSolicitudId()) //
                .orElseThrow(() -> new EntityNotFoundException("Solicitud de servicio no encontrada con ID: " + dto.getSolicitudId())); //

        if (solicitud.getAsignacion() != null) { //
            throw new IllegalStateException("Esta solicitud ya ha sido asignada."); //
        }

        // 2. Validar que el usuario a asignar exista
        UsuarioDTO usuarioAsignado = usuarioClient.obtenerUsuarioPorId(dto.getUsuarioAsignadoId()); //
        if (usuarioAsignado == null) { //
            throw new EntityNotFoundException("El usuario a asignar no existe con ID: " + dto.getUsuarioAsignadoId() + ". Contacte a un administrador."); //
        }
        // Aquí se podría añadir lógica de negocio si el rol del usuario asignado es importante
        // Por ejemplo, si se envía el rol desde el Gateway en un header X-User-Roles
        // String rolesDelUsuarioQueAsigna = obtenerRolesDelHeader();
        // if (!rolesDelUsuarioQueAsigna.contains("ADMINISTRATIVO")) {
        //    throw new AccessDeniedException("Solo los administradores pueden asignar tareas.");
        // }

        // 3. Crear y guardar la nueva asignación
        AsignacionServicio nuevaAsignacion = new AsignacionServicio(); //
        nuevaAsignacion.setUsuarioAsignadoId(usuarioAsignado.getIdUsuario()); //
        nuevaAsignacion.setFechaAsignacion(LocalDate.now()); //
        nuevaAsignacion.setEstado("Asignada"); //

        // Guardar la asignación primero si la relación en SolicitudServicio no usa CascadeType.ALL en OneToOne
        // Si ya tiene CascadeType.ALL, al guardar solicitud se guarda la asignación.
        // asumimos que tiene CascadeType.ALL: @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        // entonces no es necesario asignacionServicioRepository.save(nuevaAsignacion);

        // 4. Actualizar el estado y la asignación de la solicitud principal
        EstadoSolicitud estadoAsignado = estadoRepository.findById(2) // Asumimos que 2 = "Asignada"
                .orElseThrow(() -> new EntityNotFoundException("Estado 'Asignada' no configurado en la BD. Por favor, contacte a un administrador.")); //

        solicitud.setEstado(estadoAsignado); //
        solicitud.setAsignacion(nuevaAsignacion); //

        SolicitudServicio solicitudActualizada = solicitudRepository.save(solicitud); //

        // Devolvemos la solicitud actualizada y enriquecida
        return buscarSolicitudPorId(solicitudActualizada.getId()); //
    }
}