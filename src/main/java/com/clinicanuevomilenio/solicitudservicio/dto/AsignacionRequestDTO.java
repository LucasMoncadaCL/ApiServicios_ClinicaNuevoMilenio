package com.clinicanuevomilenio.solicitudservicio.dto;

import lombok.Data;

@Data
public class AsignacionRequestDTO {
    private Integer solicitudId;
    private Integer usuarioAsignadoId; // ID del técnico/personal de aseo
}