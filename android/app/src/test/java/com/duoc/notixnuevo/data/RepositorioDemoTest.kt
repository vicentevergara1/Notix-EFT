package com.duoc.notixnuevo.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RepositorioDemoTest {
    @Test fun crearModificarEliminar() = runBlocking {
        val repo = RepositorioDemo()
        assertEquals(2, repo.listarUsuarios().size)
        val nuevo = repo.crear(1, "Poder", "Prueba")
        assertTrue(repo.listarTramites().any { it.id == nuevo.id })
        repo.cambiarEstado(nuevo, "Aprobado")
        assertEquals("Aprobado", repo.listarTramites().first { it.id == nuevo.id }.estado)
        repo.eliminar(nuevo.id)
        assertFalse(repo.listarTramites().any { it.id == nuevo.id })
    }
}
