package com.clinicanuevomilenio.solicitudservicio.dto;

import lombok.Data;

// Representa la respuesta que esperamos de la pabellones-api
@Data
public class PabellonDTO {
    private Integer id;
    private String nombre;
    private String edificio;
    private int piso;
}