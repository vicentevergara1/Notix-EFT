package com.duoc.notixnuevo.data

data class Usuario(
    val id: Long,
    val nombre: String,
    val correo: String,
    val tipo_usuario: String
)

data class Tramite(
    val id: Long,
    val usuario_id: Long,
    val tipo_tramite: String,
    val descripcion: String,
    val estado: String,
    val observacion: String? = null
)

data class Feriado(val id: Long, val nombre: String, val fecha: String, val pais: String)

data class LoginRequest(val correo: String, val password: String)
data class RegistroRequest(val nombre: String, val correo: String, val password: String)
data class TokenResponse(val authToken: String)
data class CrearTramiteRequest(val tipo_tramite: String, val descripcion: String)
data class CambiarEstadoRequest(val estado: String, val observacion: String? = null)

data class Sesion(val token: String, val usuario: Usuario)
