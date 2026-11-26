package com.duoc.notixnuevo.viewmodel

import com.duoc.notixnuevo.data.Feriado
import com.duoc.notixnuevo.data.NotixRepository
import com.duoc.notixnuevo.data.Sesion
import com.duoc.notixnuevo.data.Tramite
import com.duoc.notixnuevo.data.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotixViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<NotixRepository>()

    @Before fun preparar() {
        Dispatchers.setMain(dispatcher)
    }

    @After fun terminar() {
        Dispatchers.resetMain()
    }

    private val persona = Usuario(1, "Ana", "ana@test.cl", "Persona")
    private val notario = Usuario(2, "Notario", "notario@test.cl", "Notario")
    private val tramite = Tramite(10, 1, "Poder", "Gestion", "Pendiente")

    @Test fun personaPuedeIniciarSesionYVerSusTramites() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("token", persona)
        coEvery { repository.tramites("token") } returns listOf(tramite)
        coEvery { repository.feriados() } returns listOf(Feriado(1,"Navidad","2026-12-25","CL"))
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        assertEquals("Ana", vm.estado.usuario?.nombre)
        assertEquals(1, vm.estado.tramites.size)
        assertFalse(vm.estado.cargando)
    }

    @Test fun noSePuedeCrearTramiteSinSesion() = runTest(dispatcher) {
        val vm = NotixViewModel(repository)
        vm.crearTramite("Poder", "Gestion")
        runCurrent()
        assertTrue(vm.estado.mensaje.contains("Persona"))
        coVerify(exactly = 0) { repository.crear(any(), any(), any()) }
    }

    @Test fun notarioRechazaCreacionYPuedeAprobar() = runTest(dispatcher) {
        coEvery { repository.login("notario@test.cl", "Clave12345") } returns Sesion("jwt", notario)
        coEvery { repository.tramites("jwt") } returns listOf(tramite)
        coEvery { repository.feriados() } returns emptyList()
        coEvery { repository.resolver("jwt", 10, "Aprobado", null) } returns
            tramite.copy(estado = "Aprobado")
        val vm = NotixViewModel(repository)
        vm.login("notario@test.cl", "Clave12345")
        runCurrent()
        vm.crearTramite("Poder", "Gestion")
        assertTrue(vm.estado.mensaje.contains("Persona"))
        vm.resolverTramite(10, "Aprobado", "")
        runCurrent()
        coVerify(exactly = 1) { repository.resolver("jwt", 10, "Aprobado", null) }
    }

    @Test fun devolverExigeObservacion() = runTest(dispatcher) {
        coEvery { repository.login("notario@test.cl", "Clave12345") } returns Sesion("jwt", notario)
        coEvery { repository.tramites("jwt") } returns emptyList()
        coEvery { repository.feriados() } returns emptyList()
        val vm = NotixViewModel(repository)
        vm.login("notario@test.cl", "Clave12345")
        runCurrent()
        vm.resolverTramite(10, "Devuelto", " ")
        runCurrent()
        assertTrue(vm.estado.mensaje.contains("observacion"))
        coVerify(exactly = 0) { repository.resolver(any(), any(), any(), any()) }
    }

    @Test fun errorHTTPNoSePresentaComoExito() = runTest(dispatcher) {
        coEvery { repository.login(any(), any()) } throws IllegalStateException("API no disponible")
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        assertNull(vm.estado.usuario)
        assertTrue(vm.estado.mensaje.contains("API no disponible"))
    }
}
