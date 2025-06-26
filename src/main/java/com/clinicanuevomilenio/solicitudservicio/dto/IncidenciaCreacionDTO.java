package com.clinicanuevomilenio.solicitudservicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncidenciaCreacionDTO {

    @NotNull(message = "Debe especificar el pabellón donde ocurrió la incidencia.")
    private Integer pabellonId;

    private Integer equipamientoId; // Opcional, si la incidencia es sobre un equipo

    @NotNull(message = "Debe seleccionar el tipo de incidencia.")
    private Integer tipoIncidenciaId;

    @NotBlank(message = "La descripción de la incidencia no puede estar vacía.")
    private String descripcion;
}