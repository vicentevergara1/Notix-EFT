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

    @Test fun registroPersonaConExito() = runTest(dispatcher) {
        coEvery { repository.registrar("Ana", "ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns emptyList()
        coEvery { repository.feriados() } returns emptyList()
        val vm = NotixViewModel(repository)
        vm.registro("Ana", "ana@test.cl", "Clave12345")
        runCurrent()
        assertEquals(persona, vm.estado.usuario)
    }

    @Test fun registroInvalidoNoLlamaAlBackend() = runTest(dispatcher) {
        val vm = NotixViewModel(repository)
        vm.registro("", "ana@test.cl", "Clave12345")
        assertTrue(vm.estado.mensaje.contains("nombre"))
        coVerify(exactly = 0) { repository.registrar(any(), any(), any()) }
    }

    @Test fun loginInvalidoNoConsultaLaApi() = runTest(dispatcher) {
        val vm = NotixViewModel(repository)
        vm.login("no-es-correo", "Clave12345")
        assertTrue(vm.estado.mensaje.contains("correo"))
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test fun personaPuedeCrearTramite() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns emptyList()
        coEvery { repository.feriados() } returns emptyList()
        coEvery { repository.crear("jwt", "Poder", "Gestion") } returns tramite
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        vm.crearTramite("Poder", "Gestion")
        runCurrent()
        coVerify(exactly = 1) { repository.crear("jwt", "Poder", "Gestion") }
        assertEquals("Tramite enviado", vm.estado.mensaje)
    }

    @Test fun personaNoPuedeEnviarFormularioVacio() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns emptyList()
        coEvery { repository.feriados() } returns emptyList()
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        vm.crearTramite("", "")
        assertTrue(vm.estado.mensaje.contains("tipo"))
        coVerify(exactly = 0) { repository.crear(any(), any(), any()) }
    }

    @Test fun personaPuedeEliminarSuTramite() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns listOf(tramite)
        coEvery { repository.feriados() } returns emptyList()
        coEvery { repository.eliminar("jwt", 10) } returns Unit
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        vm.eliminarTramite(tramite)
        runCurrent()
        coVerify { repository.eliminar("jwt", 10) }
        assertEquals("Tramite eliminado", vm.estado.mensaje)
    }

    @Test fun personaNoPuedeEliminarTramiteAjeno() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns emptyList()
        coEvery { repository.feriados() } returns emptyList()
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        vm.eliminarTramite(tramite.copy(usuario_id = 55))
        assertTrue(vm.estado.mensaje.contains("No puedes"))
        coVerify(exactly = 0) { repository.eliminar(any(), any()) }
    }

    @Test fun notarioDevuelveConObservacion() = runTest(dispatcher) {
        coEvery { repository.login("notario@test.cl", "Clave12345") } returns Sesion("jwt", notario)
        coEvery { repository.tramites("jwt") } returns listOf(tramite)
        coEvery { repository.feriados() } returns emptyList()
        coEvery { repository.resolver("jwt", 10, "Devuelto", "Falta documento") } returns
            tramite.copy(estado = "Devuelto", observacion = "Falta documento")
        val vm = NotixViewModel(repository)
        vm.login("notario@test.cl", "Clave12345")
        runCurrent()
        vm.resolverTramite(10, "Devuelto", "Falta documento")
        runCurrent()
        coVerify { repository.resolver("jwt", 10, "Devuelto", "Falta documento") }
    }

    @Test fun cerrarSesionQuitaElPerfil() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns emptyList()
        coEvery { repository.feriados() } returns emptyList()
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        vm.salir()
        assertNull(vm.estado.usuario)
        assertTrue(vm.estado.tramites.isEmpty())
    }

    @Test fun refrescarConsultaTramitesYFeriados() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } returns listOf(tramite)
        coEvery { repository.feriados() } returns emptyList()
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        vm.cargarDatos()
        runCurrent()
        coVerify(exactly = 2) { repository.tramites("jwt") }
        assertEquals("Datos actualizados", vm.estado.mensaje)
    }

    @Test fun errorAlConsultarNoSePresentaComoExito() = runTest(dispatcher) {
        coEvery { repository.login("ana@test.cl", "Clave12345") } returns Sesion("jwt", persona)
        coEvery { repository.tramites("jwt") } throws IllegalStateException("backend caido")
        val vm = NotixViewModel(repository)
        vm.login("ana@test.cl", "Clave12345")
        runCurrent()
        assertNull(vm.estado.usuario)
        assertTrue(vm.estado.mensaje.contains("backend caido"))
    }
}
