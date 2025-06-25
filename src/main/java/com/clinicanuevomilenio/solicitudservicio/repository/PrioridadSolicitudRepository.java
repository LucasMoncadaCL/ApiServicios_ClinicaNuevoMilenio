package com.clinicanuevomilenio.solicitudservicio.repository;

import com.clinicanuevomilenio.solicitudservicio.models.PrioridadSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrioridadSolicitudRepository extends JpaRepository<PrioridadSolicitud, Integer> {
}