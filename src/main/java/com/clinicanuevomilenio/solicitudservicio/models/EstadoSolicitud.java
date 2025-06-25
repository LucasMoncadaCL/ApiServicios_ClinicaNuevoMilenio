package com.clinicanuevomilenio.solicitudservicio.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "estado_solicitud")
@Data
public class EstadoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "es_activo", nullable = false)
    private boolean esActivo;
}