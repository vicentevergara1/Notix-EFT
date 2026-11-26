package com.duoc.notixnuevo.data

data class Usuario(val id: Long, val nombre: String, val correo: String, val tipo_usuario: String)
data class Tramite(val id: Long, val usuario_id: Long, val tipo_tramite: String,
                   val descripcion: String, val estado: String)
data class Feriado(val id: Long, val nombre: String, val fecha: String, val pais: String)

data class CrearTramite(val usuario_id: Long, val tipo_tramite: String,
                        val descripcion: String, val estado: String = "Pendiente")
data class ActualizarTramite(val usuario_id: Long, val tipo_tramite: String,
                             val descripcion: String, val estado: String)
