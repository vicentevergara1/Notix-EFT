package com.duoc.notixnuevo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.notixnuevo.data.Feriado
import com.duoc.notixnuevo.data.NotixRepository
import com.duoc.notixnuevo.data.Sesion
import com.duoc.notixnuevo.data.Tramite
import com.duoc.notixnuevo.data.Usuario
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class EstadoPantalla(
    val cargando: Boolean = false,
    val mensaje: String = "",
    val usuario: Usuario? = null,
    val tramites: List<Tramite> = emptyList(),
    val feriados: List<Feriado> = emptyList()
)

class NotixViewModel(
    private val repositorio: NotixRepository = NotixRepository()
) : ViewModel() {
    var estado by mutableStateOf(EstadoPantalla())
        private set

    // El token vive solo en memoria; al cerrar la app hay que iniciar sesion otra vez.
    private var token: String? = null

    private fun ejecutar(accion: suspend () -> Unit) {
        if (estado.cargando) return
        viewModelScope.launch {
            estado = estado.copy(cargando = true, mensaje = "")
            try {
                accion()
            } catch (error: Exception) {
                val detalle = if (error is HttpException) "HTTP " + error.code()
                    else error.message ?: "Sin detalle"
                estado = estado.copy(mensaje = "No se pudo completar la operacion: $detalle")
            } finally {
                estado = estado.copy(cargando = false)
            }
        }
    }

    private suspend fun iniciar(sesion: Sesion) {
        token = sesion.token
        val tramites = runCatching { repositorio.tramites(sesion.token) }.getOrDefault(emptyList())
        val feriados = runCatching { repositorio.feriados() }.getOrDefault(emptyList())
        estado = EstadoPantalla(
            usuario = sesion.usuario,
            tramites = tramites,
            feriados = feriados,
            mensaje = "Sesion iniciada"
        )
    }

    fun login(correo: String, password: String) {
        validarCredenciales(correo, password)?.let {
            estado = estado.copy(mensaje = it)
            return
        }
        ejecutar { iniciar(repositorio.login(correo, password)) }
    }

    fun registro(nombre: String, correo: String, password: String) {
        validarRegistro(nombre, correo, password)?.let {
            estado = estado.copy(mensaje = it)
            return
        }
        ejecutar { iniciar(repositorio.registrar(nombre, correo, password)) }
    }

    fun salir() {
        if (estado.cargando) return
        token = null
        estado = EstadoPantalla(mensaje = "Sesion cerrada")
    }

    private fun tokenActivo(): String = requireNotNull(token) { "Inicia sesion nuevamente." }

    fun cargarDatos() = ejecutar {
        val t = tokenActivo()
        val lista = repositorio.tramites(t)
        val feriados = runCatching { repositorio.feriados() }.getOrDefault(emptyList())
        estado = estado.copy(tramites = lista, feriados = feriados, mensaje = "Datos actualizados")
    }

    fun crearTramite(tipo: String, descripcion: String) {
        if (!esPersona(estado.usuario?.tipo_usuario.orEmpty())) {
            estado = estado.copy(mensaje = "Solo una Persona puede crear un tramite.")
            return
        }
        validarTramite(tipo, descripcion)?.let {
            estado = estado.copy(mensaje = it)
            return
        }
        ejecutar {
            val t = tokenActivo()
            repositorio.crear(t, tipo, descripcion)
            estado = estado.copy(tramites = repositorio.tramites(t), mensaje = "Tramite enviado")
        }
    }

    fun resolverTramite(id: Long, nuevoEstado: String, observacion: String) {
        if (!esNotario(estado.usuario?.tipo_usuario.orEmpty())) {
            estado = estado.copy(mensaje = "Solo el Notario puede revisar tramites.")
            return
        }
        if (nuevoEstado != "Aprobado" && nuevoEstado != "Devuelto") {
            estado = estado.copy(mensaje = "Estado no permitido")
            return
        }
        if (nuevoEstado == "Devuelto" && observacion.isBlank()) {
            estado = estado.copy(mensaje = "Escribe una observacion para devolver el tramite.")
            return
        }
        ejecutar {
            val t = tokenActivo()
            repositorio.resolver(t, id, nuevoEstado,
                if (nuevoEstado == "Devuelto") observacion else null)
            estado = estado.copy(tramites = repositorio.tramites(t), mensaje = "Revision guardada")
        }
    }

    fun eliminarTramite(tramite: Tramite) {
        val u = estado.usuario
        if (u == null || !esPersona(u.tipo_usuario) || tramite.usuario_id != u.id) {
            estado = estado.copy(mensaje = "No puedes eliminar este tramite.")
            return
        }
        ejecutar {
            val t = tokenActivo()
            repositorio.eliminar(t, tramite.id)
            estado = estado.copy(tramites = repositorio.tramites(t), mensaje = "Tramite eliminado")
        }
    }
}
