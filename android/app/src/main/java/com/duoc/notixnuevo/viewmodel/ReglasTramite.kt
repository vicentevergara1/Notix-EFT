package com.duoc.notixnuevo.viewmodel

fun validarTramite(usuarioId: String, tipo: String, descripcion: String): String? {
    val id = usuarioId.trim().toLongOrNull()
    if (id == null || id <= 0) return "Escribe un ID de usuario valido."
    if (tipo.isBlank()) return "Escribe el tipo de tramite."
    if (descripcion.isBlank()) return "Escribe una descripcion."
    return null
}
