package com.clinicanuevomilenio.solicitudservicio.dto;

import lombok.Data;

// Representa la respuesta que esperamos de la usuarios-api
@Data
public class UsuarioDTO {
    private Integer idUsuario;
    private String username;
    private String nombreCompleto;
    private String rol;
}