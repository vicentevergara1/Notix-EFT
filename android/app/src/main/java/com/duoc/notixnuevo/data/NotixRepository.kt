package com.duoc.notixnuevo.data

interface DatosNotix {
    suspend fun listarUsuarios(): List<Usuario>
    suspend fun listarTramites(): List<Tramite>
    suspend fun listarFeriados(): List<Feriado>
    suspend fun crear(usuarioId: Long, tipo: String, descripcion: String): Tramite
    suspend fun cambiarEstado(tramite: Tramite, nuevoEstado: String): Tramite
    suspend fun eliminar(id: Long)
}

class NotixRepository(private val api: NotixApi = RetrofitCliente.api) : DatosNotix {
    override suspend fun listarUsuarios() = api.usuarios()
    override suspend fun listarTramites() = api.tramites()
    override suspend fun listarFeriados() = api.feriados()
    override suspend fun crear(usuarioId: Long, tipo: String, descripcion: String) =
        api.crear(CrearTramite(usuarioId, tipo, descripcion))
    override suspend fun cambiarEstado(tramite: Tramite, nuevoEstado: String) =
        api.actualizar(tramite.id, ActualizarTramite(
            tramite.usuario_id, tramite.tipo_tramite, tramite.descripcion, nuevoEstado
        ))
    override suspend fun eliminar(id: Long) {
        val response = api.eliminar(id)
        check(response.isSuccessful) { "Xano devolvio HTTP " + response.code() }
    }
}
