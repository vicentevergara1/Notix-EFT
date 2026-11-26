package com.duoc.notix.tramites;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TramiteRepository extends JpaRepository<TramiteEntity, Long> {
    List<TramiteEntity> findByUsuarioIdOrderByIdDesc(Long usuarioId);
}
