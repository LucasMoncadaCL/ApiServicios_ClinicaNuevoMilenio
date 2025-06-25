package com.clinicanuevomilenio.solicitudservicio.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "solicitud_servicio")
@Data
public class SolicitudServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "solicitud_id")
    private Integer id;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo; // Ej: "Mantenimiento", "Aseo"

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "fecha_hr_solicitud", nullable = false)
    private LocalDateTime fechaHrSolicitud;

    @Column(name = "fecha_hr_requerida", nullable = false)
    private LocalDateTime fechaHrRequerida;

    @Column(name = "fecha_hr_completado")
    private LocalDateTime fechaHrCompletado;

    @Column(name = "comentarios", length = 100)
    private String comentarios;

    // --- Relaciones Internas (con objetos) ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prioridad_id", nullable = false)
    private PrioridadSolicitud prioridad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoSolicitud estado;

    // Una solicitud puede tener una asignación
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ASIGNACION_SERVICIO_asignacion_id")
    private AsignacionServicio asignacion;

    // Una solicitud tiene un solo registro de incidencia asociado
    @OneToMany(mappedBy = "solicitudServicio", cascade = CascadeType.ALL)
    private List<RegistroIncidencia> incidencias;

    // --- Relaciones Externas (solo con IDs) ---

    @Column(name = "PABELLON_pabellon_id", nullable = false)
    private Integer pabellonId; // ID del pabellón de la `pabellones-api`

    @Column(name = "USUARIO_id_usuario", nullable = false)
    private Integer usuarioSolicitanteId; // ID del usuario que crea la solicitud, de la `usuarios-api`

    @Column(name = "EQUIPAMIENTO_equipo_id")
    private Integer equipamientoId; // ID del equipo (si aplica), de la `equipamiento-api`
}