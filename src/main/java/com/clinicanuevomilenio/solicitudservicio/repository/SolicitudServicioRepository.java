package com.clinicanuevomilenio.solicitudservicio.repository;

import com.clinicanuevomilenio.solicitudservicio.models.SolicitudServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudServicioRepository extends JpaRepository<SolicitudServicio, Integer> {

    // --- Métodos Personalizados para Búsquedas Frecuentes ---

    /**
     * Busca todas las solicitudes de servicio para un pabellón específico.
     */
    List<SolicitudServicio> findByPabellonId(Integer pabellonId);

    /**
     * Busca todas las solicitudes de servicio creadas por un usuario específico.
     */
    List<SolicitudServicio> findByUsuarioSolicitanteId(Integer usuarioId);

    /**
     * Busca todas las solicitudes de servicio que tienen un estado específico.
     */
    List<SolicitudServicio> findByEstadoId(Integer estadoId);

    List<SolicitudServicio> findByEstado_IdNotIn(List<Integer> estados);
}