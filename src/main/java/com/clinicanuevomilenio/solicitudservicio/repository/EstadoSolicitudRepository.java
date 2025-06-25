package com.clinicanuevomilenio.solicitudservicio.repository;

import com.clinicanuevomilenio.solicitudservicio.models.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoSolicitudRepository extends JpaRepository<EstadoSolicitud, Integer> {
}