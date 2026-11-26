package com.duoc.notixnuevo.viewmodel

import org.junit.Assert.*
import org.junit.Test

class ReglasTramiteTest {
    @Test fun correoInvalido() = assertNotNull(validarCredenciales("ana", "clave12345"))
    @Test fun registroValido() = assertNull(validarRegistro("Ana", "ana@notix.test", "clave12345"))
    @Test fun registroCorto() = assertNotNull(validarRegistro("Ana", "ana@notix.test", "corta"))
    @Test fun tramiteValido() = assertNull(validarTramite("Poder", "Gestion administrativa"))
    @Test fun tramiteIncompleto() = assertNotNull(validarTramite("", "Gestion"))
    @Test fun roles() {
        assertTrue(esNotario("Notario"))
        assertFalse(esNotario("Persona"))
        assertTrue(esPersona("Persona"))
    }
}
