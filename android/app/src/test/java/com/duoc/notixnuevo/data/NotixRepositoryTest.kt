package com.duoc.notixnuevo.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import okhttp3.ResponseBody
import retrofit2.Response

class NotixRepositoryTest {
    private val usuarios = mockk<AuthApi>()
    private val tramites = mockk<TramitesApi>()
    private val xano = mockk<XanoFeriadosApi>()
    private val nager = mockk<NagerFeriadosApi>()
    private val repo = NotixRepository(usuarios, tramites, xano, nager)

    @Test fun loginDevuelveTokenYPerfil() = runBlocking {
        val usuario = Usuario(1, "Ana", "ana@test.cl", "Persona")
        coEvery { usuarios.login(LoginRequest("ana@test.cl", "secreto123")) } returns TokenResponse("jwt")
        coEvery { usuarios.perfil("Bearer jwt") } returns usuario
        val resultado = repo.login("ana@test.cl", "secreto123")
        assertEquals("jwt", resultado.token)
        assertEquals(usuario, resultado.usuario)
    }

    @Test fun registrarConsultaPerfil() = runBlocking {
        coEvery { usuarios.registro(any()) } returns TokenResponse("nuevo")
        coEvery { usuarios.perfil("Bearer nuevo") } returns Usuario(2, "Ana", "ana@test.cl", "Persona")
        assertEquals(2L, repo.registrar("Ana", "ana@test.cl", "secreto123").usuario.id)
    }

    @Test fun crearTramiteUsaBearerYNoEnviaUsuarioId() = runBlocking {
        val tramite = Tramite(2, 1, "Poder", "Gestion", "Pendiente")
        coEvery { tramites.crear("Bearer jwt", CrearTramiteRequest("Poder", "Gestion")) } returns tramite
        assertEquals(2L, repo.crear("jwt", "Poder", "Gestion").id)
        coVerify { tramites.crear("Bearer jwt", any()) }
    }

    @Test fun modificaYEliminaTramite() = runBlocking {
        val actualizado = Tramite(2, 1, "Poder", "Gestion", "Aprobado")
        coEvery { tramites.resolver("Bearer jwt", 2, any()) } returns actualizado
        coEvery { tramites.eliminar("Bearer jwt", 2) } returns Response.success(null)
        assertEquals("Aprobado", repo.resolver("jwt", 2, "Aprobado", null).estado)
        repo.eliminar("jwt", 2)
        coVerify { tramites.eliminar("Bearer jwt", 2) }
    }

    @Test fun leeFeriadosDesdeXano() = runBlocking {
        coEvery { xano.listar() } returns listOf(Feriado(1, "Navidad", "2026-12-25", "CL"))
        val feriados = repo.feriados()
        assertEquals("Xano", feriados.first().origen)
    }

    @Test fun siXanoFallaConsultaNager() = runBlocking {
        coEvery { xano.listar() } throws IOException("sin red")
        coEvery { nager.proximos("CL") } returns listOf(
            NagerFeriadoDto("2026-12-25", "Navidad", "Christmas Day")
        )
        val resultado = repo.feriados()
        assertEquals("Nager.Date", resultado.first().origen)
        assertEquals("Navidad", resultado.first().nombre)
    }
}
