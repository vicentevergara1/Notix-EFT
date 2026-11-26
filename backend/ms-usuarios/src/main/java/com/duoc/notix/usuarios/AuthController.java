package com.duoc.notix.usuarios;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository usuarios;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtEncoder jwtEncoder;

    public AuthController(UsuarioRepository usuarios, JwtEncoder jwtEncoder) {
        this.usuarios = usuarios;
        this.jwtEncoder = jwtEncoder;
    }

    public record Registro(
        @NotBlank String nombre,
        @Email @NotBlank String correo,
        @Size(min = 8) String password
    ) {}
    public record Login(@Email @NotBlank String correo, @NotBlank String password) {}
    public record TokenResponse(String authToken) {}
    public record UsuarioResponse(Long id, String nombre, String correo, String tipo_usuario) {}

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registrar(@Valid @RequestBody Registro datos) {
        String correo = datos.correo().trim().toLowerCase();
        if (usuarios.existsByCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Correo ya registrado");
        }
        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(datos.nombre().trim());
        usuario.setCorreo(correo);
        usuario.setContrasenaHash(passwordEncoder.encode(datos.password()));
        usuario.setTipoUsuario("Persona");
        usuario = usuarios.save(usuario);
        return new TokenResponse(tokenPara(usuario));
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody Login datos) {
        UsuarioEntity usuario = usuarios.findByCorreo(datos.correo().trim().toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));
        if (!passwordEncoder.matches(datos.password(), usuario.getContrasenaHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }
        return new TokenResponse(tokenPara(usuario));
    }

    @GetMapping("/me")
    public UsuarioResponse perfil(@org.springframework.security.core.annotation.AuthenticationPrincipal Jwt jwt) {
        long id = ((Number) jwt.getClaim("uid")).longValue();
        UsuarioEntity usuario = usuarios.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cuenta ya no existe"));
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(),
            usuario.getCorreo(), usuario.getTipoUsuario());
    }

    private String tokenPara(UsuarioEntity usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("notix")
            .subject(usuario.getCorreo())
            .issuedAt(ahora)
            .expiresAt(ahora.plusSeconds(3600))
            .claim("uid", usuario.getId())
            .claim("rol", usuario.getTipoUsuario())
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
