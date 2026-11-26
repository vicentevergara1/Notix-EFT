package com.duoc.notix.usuarios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class NotarioBootstrap implements ApplicationRunner {
    private final UsuarioRepository usuarios;
    @Value("${notix.admin.email:}") private String correo;
    @Value("${notix.admin.password:}") private String password;

    public NotarioBootstrap(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (correo.isBlank() || password.isBlank()) return;
        if (password.length() < 8) {
            throw new IllegalStateException("NOTIX_ADMIN_PASSWORD requiere 8 caracteres");
        }
        String normalizado = correo.trim().toLowerCase();
        if (usuarios.existsByCorreo(normalizado)) return;
        UsuarioEntity notario = new UsuarioEntity();
        notario.setNombre("Notario de prueba");
        notario.setCorreo(normalizado);
        notario.setContrasenaHash(new BCryptPasswordEncoder().encode(password));
        notario.setTipoUsuario("Notario");
        usuarios.save(notario);
    }
}
