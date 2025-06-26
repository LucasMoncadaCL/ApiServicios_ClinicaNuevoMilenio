package com.clinicanuevomilenio.solicitudservicio.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SolicitudServicioCreacionDTO {

    @NotBlank(message = "El tipo de servicio no puede estar vacío.")
    private String tipo;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion;

    @NotNull(message = "La fecha y hora requerida no puede ser nula.")
    @Future(message = "La fecha y hora requerida debe ser futura.")
    private LocalDateTime fechaHrRequerida;

    private String comentarios;

    @NotNull(message = "Debe seleccionar una prioridad.")
    private Integer prioridadId;

    @NotNull(message = "Debe seleccionar un pabellón.")
    private Integer pabellonId;

    private Integer equipamientoId; // Opcional
}
