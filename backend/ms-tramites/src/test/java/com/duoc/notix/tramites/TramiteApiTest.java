package com.duoc.notix.tramites;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "notix.jwt.secret=clave-de-pruebas-notix-muy-larga-con-32-bytes",
    "spring.datasource.url=jdbc:h2:mem:tramites_test;DB_CLOSE_DELAY=-1"
})
class TramiteApiTest {
    @Autowired MockMvc mvc;
    @Autowired JwtEncoder encoder;

    private String token(long id, String rol) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("notix")
            .subject("cuenta@notix.test").issuedAt(ahora).expiresAt(ahora.plusSeconds(3600))
            .claim("uid", id).claim("rol", rol).build();
        return "Bearer " + encoder.encode(JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @Test
    void usuarioCreaNotarioAprueba() throws Exception {
        String persona = token(1001, "Persona");
        String notario = token(2001, "Notario");
        String result = mvc.perform(post("/api/tramites")
            .header("Authorization", persona)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tipo_tramite\":\"Poder\",\"descripcion\":\"Gestion\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estado").value("Pendiente"))
            .andExpect(jsonPath("$.usuario_id").value(1001))
            .andReturn().getResponse().getContentAsString();
        long id = new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(result).get("id").asLong();
        mvc.perform(put("/api/tramites/" + id)
            .header("Authorization", persona).contentType(MediaType.APPLICATION_JSON)
            .content("{\"estado\":\"Aprobado\"}")).andExpect(status().isForbidden());
        mvc.perform(put("/api/tramites/" + id)
            .header("Authorization", notario).contentType(MediaType.APPLICATION_JSON)
            .content("{\"estado\":\"Aprobado\"}")).andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("Aprobado"));
        mvc.perform(delete("/api/tramites/" + id).header("Authorization", persona))
            .andExpect(status().isConflict());
    }

    @Test
    void requiereToken() throws Exception {
        mvc.perform(get("/api/tramites")).andExpect(status().isUnauthorized());
    }
}
