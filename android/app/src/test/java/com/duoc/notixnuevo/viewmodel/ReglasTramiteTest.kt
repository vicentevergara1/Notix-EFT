package com.duoc.notixnuevo.viewmodel

import org.junit.Assert.*
import org.junit.Test

class ReglasTramiteTest {
    @Test fun datosValidos() = assertNull(validarTramite("1", "Poder", "Ejemplo"))
    @Test fun idInvalido() = assertNotNull(validarTramite("0", "Poder", "Ejemplo"))
    @Test fun tipoVacio() = assertNotNull(validarTramite("1", "", "Ejemplo"))
    @Test fun descripcionVacia() = assertNotNull(validarTramite("1", "Poder", ""))
}
