package com.duoc.notixnuevo.data

// CRUD local de practica: no consulta Xano ni persiste al cerrar la aplicacion.
class RepositorioDemo : DatosNotix {
    private val usuarios = listOf(
        Usuario(1, "Ana Perez", "ana@notix.test", "Persona"),
        Usuario(2, "Carlos Munoz", "carlos@notix.test", "Notario")
    )
    private val tramites = mutableListOf(
        Tramite(1, 1, "Poder simple", "Gestion administrativa", "Pendiente"),
        Tramite(2, 1, "Declaracion jurada", "Declaracion simulada", "Devuelto")
    )
    private val feriados = listOf(
        Feriado(1, "Inmaculada Concepcion", "2026-12-08", "CL"),
        Feriado(2, "Navidad", "2026-12-25", "CL")
    )
    private var siguienteId = 3L

    override suspend fun listarUsuarios() = usuarios
    override suspend fun listarTramites() = tramites.toList()
    override suspend fun listarFeriados() = feriados

    override suspend fun crear(usuarioId: Long, tipo: String, descripcion: String): Tramite {
        require(usuarios.any { it.id == usuarioId }) { "El usuario no existe" }
        val nuevo = Tramite(siguienteId++, usuarioId, tipo, descripcion, "Pendiente")
        tramites.add(nuevo)
        return nuevo
    }
    override suspend fun cambiarEstado(tramite: Tramite, nuevoEstado: String): Tramite {
        val i = tramites.indexOfFirst { it.id == tramite.id }
        require(i >= 0) { "El tramite no existe" }
        require(nuevoEstado == "Aprobado" || nuevoEstado == "Devuelto")
        return tramites[i].copy(estado = nuevoEstado).also { tramites[i] = it }
    }
    override suspend fun eliminar(id: Long) {
        require(tramites.removeAll { it.id == id }) { "El tramite no existe" }
    }
}
