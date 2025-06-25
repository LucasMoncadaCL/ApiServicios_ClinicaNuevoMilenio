package com.clinicanuevomilenio.solicitudservicio.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_incidencia")
@Data
public class RegistroIncidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "incidencia_id")
    private Integer id;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    @Column(name = "fecha_hr_reporte", nullable = false)
    private LocalDateTime fechaHrReporte;

    // --- Relaciones Internas ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoIncidencia tipoIncidencia;

    // Relación con la solicitud de servicio a la que pertenece
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOLICITUD_SERVICIO_solicitud_id", nullable = false)
    private SolicitudServicio solicitudServicio;
}