package com.duoc.notixnuevo.data

import java.io.IOException
import retrofit2.HttpException

// Repository: recibe solicitudes del ViewModel y elige el servicio correcto.
class NotixRepository(
    private val usuariosApi: AuthApi = RetrofitCliente.usuarios,
    private val tramitesApi: TramitesApi = RetrofitCliente.tramites,
    private val xanoApi: XanoFeriadosApi = RetrofitCliente.feriadosXano,
    private val nagerApi: NagerFeriadosApi = RetrofitCliente.feriadosNager
) {
    suspend fun login(correo: String, password: String): Sesion {
        val respuesta = usuariosApi.login(LoginRequest(correo.trim(), password))
        require(respuesta.authToken.isNotBlank()) { "Falta authToken" }
        val usuario = usuariosApi.perfil(bearer(respuesta.authToken))
        return Sesion(respuesta.authToken, usuario)
    }

    suspend fun registrar(nombre: String, correo: String, password: String): Sesion {
        val respuesta = usuariosApi.registro(
            RegistroRequest(nombre.trim(), correo.trim(), password)
        )
        require(respuesta.authToken.isNotBlank()) { "Falta authToken" }
        val usuario = usuariosApi.perfil(bearer(respuesta.authToken))
        return Sesion(respuesta.authToken, usuario)
    }

    suspend fun tramites(token: String): List<Tramite> = tramitesApi.tramites(bearer(token))

    suspend fun crear(token: String, tipo: String, descripcion: String): Tramite =
        tramitesApi.crear(bearer(token), CrearTramiteRequest(tipo.trim(), descripcion.trim()))

    suspend fun resolver(token: String, id: Long, estado: String, observacion: String?): Tramite =
        tramitesApi.resolver(bearer(token), id,
            CambiarEstadoRequest(estado, observacion?.trim()))

    suspend fun eliminar(token: String, id: Long) {
        val respuesta = tramitesApi.eliminar(bearer(token), id)
        check(respuesta.isSuccessful) { "Error HTTP " + respuesta.code() }
    }

    suspend fun feriados(): List<Feriado> {
        // Xano almacena feriados; si falla la API, se consulta directamente Nager.Date.
        try {
            return xanoApi.listar().map { it.copy(origen = "Xano") }
        } catch (_: IOException) {
            return desdeNager()
        } catch (_: HttpException) {
            return desdeNager()
        }
    }

    private suspend fun desdeNager(): List<Feriado> =
        nagerApi.proximos("CL").mapIndexed { indice, dto ->
            Feriado(
                id = indice.toLong() + 1,
                nombre = dto.localName.ifBlank { dto.name },
                fecha = dto.date,
                pais = "CL",
                origen = "Nager.Date"
            )
        }

    private fun bearer(token: String): String = "Bearer $token"
}
