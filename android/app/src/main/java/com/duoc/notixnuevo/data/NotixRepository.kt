package com.duoc.notixnuevo.data

// Esta clase concentra las peticiones que la interfaz no necesita conocer.
class NotixRepository(private val api: NotixApi = RetrofitCliente.api) {
    suspend fun login(correo: String, password: String): Sesion {
        val respuesta = api.login(LoginRequest(correo.trim(), password))
        require(respuesta.authToken.isNotBlank()) { "La API no devolvio un token" }
        return Sesion(respuesta.authToken, api.perfil(bearer(respuesta.authToken)))
    }

    suspend fun registrar(nombre: String, correo: String, password: String): Sesion {
        val respuesta = api.registro(RegistroRequest(nombre.trim(), correo.trim(), password))
        require(respuesta.authToken.isNotBlank()) { "La API no devolvio un token" }
        return Sesion(respuesta.authToken, api.perfil(bearer(respuesta.authToken)))
    }

    suspend fun tramites(token: String): List<Tramite> = api.tramites(bearer(token))

    suspend fun crear(token: String, tipo: String, descripcion: String): Tramite =
        api.crear(bearer(token), CrearTramiteRequest(tipo.trim(), descripcion.trim()))

    suspend fun resolver(token: String, id: Long, estado: String, observacion: String?): Tramite =
        api.resolver(bearer(token), id, CambiarEstadoRequest(estado, observacion?.trim()))

    suspend fun eliminar(token: String, id: Long) {
        val respuesta = api.eliminar(bearer(token), id)
        check(respuesta.isSuccessful) { "Xano devolvio HTTP " + respuesta.code() }
    }

    suspend fun feriados(): List<Feriado> = api.feriados()

    private fun bearer(token: String) = "Bearer $token"
}
