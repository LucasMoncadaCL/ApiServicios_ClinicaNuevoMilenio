package com.clinicanuevomilenio.solicitudservicio.repository;

import com.clinicanuevomilenio.solicitudservicio.models.RegistroIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistroIncidenciaRepository extends JpaRepository<RegistroIncidencia, Integer> {
}