package com.duoc.notixnuevo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.notixnuevo.data.DatosNotix
import com.duoc.notixnuevo.data.Feriado
import com.duoc.notixnuevo.data.NotixRepository
import com.duoc.notixnuevo.data.RepositorioDemo
import com.duoc.notixnuevo.data.Tramite
import com.duoc.notixnuevo.data.Usuario
import kotlinx.coroutines.launch

enum class ModoDatos { DEMO, XANO }
data class EstadoPantalla(
    val modo: ModoDatos = ModoDatos.DEMO,
    val cargando: Boolean = false,
    val mensaje: String = "Modo DEMO: no modifica Xano.",
    val usuarios: List<Usuario> = emptyList(),
    val tramites: List<Tramite> = emptyList(),
    val feriados: List<Feriado> = emptyList()
)

class NotixViewModel(
    private val demo: DatosNotix = RepositorioDemo(),
    private val xano: DatosNotix = NotixRepository()
) : ViewModel() {
    var estado by mutableStateOf(EstadoPantalla())
        private set
    private val repo: DatosNotix
        get() = if (estado.modo == ModoDatos.DEMO) demo else xano

    fun cambiarModo(modo: ModoDatos) {
        if (estado.cargando) return
        estado = EstadoPantalla(
            modo = modo,
            mensaje = if (modo == ModoDatos.DEMO) "DEMO local: datos temporales."
                else "XANO real: requiere endpoints funcionando."
        )
    }

    private fun ejecutar(accion: suspend () -> Unit) {
        if (estado.cargando) return
        viewModelScope.launch {
            estado = estado.copy(cargando = true, mensaje = "")
            try {
                accion()
            } catch (e: Exception) {
                estado = estado.copy(mensaje = "Error " + estado.modo + ": " +
                    (e.message ?: "Sin detalle") + ". Revisar endpoint.")
            } finally {
                estado = estado.copy(cargando = false)
            }
        }
    }

    fun cargarUsuarios() = ejecutar {
        estado = estado.copy(usuarios = repo.listarUsuarios(), mensaje = "Usuarios consultados")
    }
    fun cargarTramites() = ejecutar {
        estado = estado.copy(tramites = repo.listarTramites(), mensaje = "Tramites consultados")
    }
    fun cargarFeriados() = ejecutar {
        estado = estado.copy(feriados = repo.listarFeriados(), mensaje = "Feriados consultados")
    }
    fun crearTramite(usuarioId: String, tipo: String, descripcion: String) {
        val error = validarTramite(usuarioId, tipo, descripcion)
        if (error != null) { estado = estado.copy(mensaje = error); return }
        ejecutar {
            repo.crear(usuarioId.trim().toLong(), tipo.trim(), descripcion.trim())
            estado = estado.copy(tramites = repo.listarTramites(), mensaje = "Tramite creado")
        }
    }
    fun cambiarEstado(t: Tramite, nuevo: String) = ejecutar {
        repo.cambiarEstado(t, nuevo)
        estado = estado.copy(tramites = repo.listarTramites(), mensaje = "Estado actualizado")
    }
    fun eliminarTramite(id: Long) = ejecutar {
        repo.eliminar(id)
        estado = estado.copy(tramites = repo.listarTramites(), mensaje = "Tramite eliminado")
    }
}
