package com.clinicanuevomilenio.solicitudservicio.repository;

import com.clinicanuevomilenio.solicitudservicio.models.TipoIncidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoIncidenciaRepository extends JpaRepository<TipoIncidencia, Integer> {
}