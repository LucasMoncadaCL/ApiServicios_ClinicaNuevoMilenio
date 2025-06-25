package com.clinicanuevomilenio.solicitudservicio.dto;

import lombok.Data;

// Representa la respuesta que esperamos de la equipamiento-api
@Data
public class EquipamientoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String estado;
}