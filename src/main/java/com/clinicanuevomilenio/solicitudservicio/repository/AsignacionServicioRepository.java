package com.clinicanuevomilenio.solicitudservicio.repository;

import com.clinicanuevomilenio.solicitudservicio.models.AsignacionServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionServicioRepository extends JpaRepository<AsignacionServicio, Integer> {
}