package com.clinicanuevomilenio.solicitudservicio.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "asignacion_servicio")
@Data
public class AsignacionServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asignacion_id")
    private Integer id;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    // Relación Externa: Solo guardamos el ID del usuario asignado.
    // La información de este usuario se obtendrá de la `usuarios-api`.
    @Column(name = "USUARIO_id_usuario", nullable = false)
    private Integer usuarioAsignadoId;
}