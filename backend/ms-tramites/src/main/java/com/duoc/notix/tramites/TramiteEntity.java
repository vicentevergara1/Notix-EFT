package com.duoc.notix.tramites;

import jakarta.persistence.*;

@Entity
@Table(name = "tramites")
public class TramiteEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long usuarioId;
    @Column(nullable = false) private String tipoTramite;
    @Column(nullable = false, length = 2000) private String descripcion;
    @Column(nullable = false) private String estado;
    @Column(length = 1000) private String observacion;

    public Long getId() { return id; }
    public Long getUsuarioId() { return usuarioId; }
    public String getTipoTramite() { return tipoTramite; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getObservacion() { return observacion; }

    public void setUsuarioId(Long v) { usuarioId = v; }
    public void setTipoTramite(String v) { tipoTramite = v; }
    public void setDescripcion(String v) { descripcion = v; }
    public void setEstado(String v) { estado = v; }
    public void setObservacion(String v) { observacion = v; }
}
