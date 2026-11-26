package com.duoc.notix.tramites;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/tramites")
public class TramiteController {
    private final TramiteRepository repositorio;
    public TramiteController(TramiteRepository repositorio) { this.repositorio = repositorio; }

    public record Crear(@NotBlank String tipo_tramite, @NotBlank @Size(max = 2000) String descripcion) {}
    public record CambiarEstado(@NotBlank String estado, String observacion) {}
    public record TramiteResponse(Long id, Long usuario_id, String tipo_tramite,
                                  String descripcion, String estado, String observacion) {}

    @GetMapping
    public List<TramiteResponse> listar(@AuthenticationPrincipal Jwt jwt) {
        List<TramiteEntity> resultado = esNotario(jwt)
            ? repositorio.findAll()
            : repositorio.findByUsuarioIdOrderByIdDesc(idUsuario(jwt));
        return resultado.stream().map(this::respuesta).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TramiteResponse crear(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody Crear datos) {
        exigirPersona(jwt);
        TramiteEntity tramite = new TramiteEntity();
        tramite.setUsuarioId(idUsuario(jwt));
        tramite.setTipoTramite(datos.tipo_tramite().trim());
        tramite.setDescripcion(datos.descripcion().trim());
        tramite.setEstado("Pendiente");
        return respuesta(repositorio.save(tramite));
    }

    @PutMapping("/{id}")
    public TramiteResponse resolver(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable Long id,
                                   @Valid @RequestBody CambiarEstado datos) {
        if (!esNotario(jwt)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo Notario");
        }
        TramiteEntity tramite = existente(id);
        if (!"Pendiente".equals(tramite.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El tramite no esta pendiente");
        }
        if (!"Aprobado".equals(datos.estado()) && !"Devuelto".equals(datos.estado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado no permitido");
        }
        if ("Devuelto".equals(datos.estado()) &&
            (datos.observacion() == null || datos.observacion().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta observacion");
        }
        tramite.setEstado(datos.estado());
        tramite.setObservacion("Devuelto".equals(datos.estado()) ? datos.observacion().trim() : null);
        return respuesta(repositorio.save(tramite));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        exigirPersona(jwt);
        TramiteEntity tramite = existente(id);
        if (!tramite.getUsuarioId().equals(idUsuario(jwt))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No es tu tramite");
        }
        if ("Aprobado".equals(tramite.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se elimina un tramite aprobado");
        }
        repositorio.delete(tramite);
    }

    private void exigirPersona(Jwt jwt) {
        if (!"Persona".equals(jwt.getClaimAsString("rol"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo Persona");
        }
    }
    private boolean esNotario(Jwt jwt) { return "Notario".equals(jwt.getClaimAsString("rol")); }
    private Long idUsuario(Jwt jwt) { return ((Number) jwt.getClaim("uid")).longValue(); }
    private TramiteEntity existente(Long id) {
        return repositorio.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tramite no encontrado"));
    }
    private TramiteResponse respuesta(TramiteEntity t) {
        return new TramiteResponse(t.getId(), t.getUsuarioId(), t.getTipoTramite(),
            t.getDescripcion(), t.getEstado(), t.getObservacion());
    }
}
