package com.clinicanuevomilenio.solicitudservicio.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "prioridad_solicitud")
@Data
public class PrioridadSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prioridad")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @Column(name = "nivel", nullable = false)
    private Integer nivel;
}