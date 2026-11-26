package com.duoc.notix.usuarios;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "notix.jwt.secret=clave-de-pruebas-notix-muy-larga-con-32-bytes",
    "spring.datasource.url=jdbc:h2:mem:usuarios_test;DB_CLOSE_DELAY=-1",
    "notix.admin.email=",
    "notix.admin.password="
})
class AuthApiTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void registroLoginYPerfil() throws Exception {
        String correo = "persona.pruebas@notix.test";
        String registro = json.writeValueAsString(new AuthController.Registro("Persona Test", correo, "ClaveSegura123"));
        mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
            .content(registro)).andExpect(status().isCreated())
            .andExpect(jsonPath("$.authToken").isNotEmpty());
        String login = json.writeValueAsString(new AuthController.Login(correo, "ClaveSegura123"));
        String resultado = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content(login)).andExpect(status().isOk())
            .andExpect(jsonPath("$.authToken").isNotEmpty())
            .andReturn().getResponse().getContentAsString();
        String token = json.readTree(resultado).get("authToken").asText();
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipo_usuario").value("Persona"))
            .andExpect(jsonPath("$.correo").value(correo));
    }

    @Test
    void sinTokenNoAccedeAlPerfil() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }
}
